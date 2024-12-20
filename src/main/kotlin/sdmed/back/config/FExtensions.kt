package sdmed.back.config

import org.apache.tomcat.util.http.fileupload.FileUploadException
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.lang.Exception
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@Component
object FExtensions {
	var defDir: String = ""
	var imageDir: String = ""
	var documentDir: String = ""
	var userExcelDir: String = ""
	var pharmaExcelDir: String = ""
	var hospitalExcelDir: String = ""
	var medicineExcelDir: String = ""
	var medicineIngredientExcelDir: String = ""
	var medicinePriceExcelDir: String = ""

	fun folderExist(excelType: FExcelParserType) {
		Optional.ofNullable(Files.createDirectories(fileLocation(excelType))).orElseThrow { FileUploadException() }
	}
	fun fileLocation(excelType: FExcelParserType, withTime: Boolean = true): Path {
		val ret = when (excelType) {
			FExcelParserType.USER -> userExcelDir
			FExcelParserType.PHARMA -> pharmaExcelDir
			FExcelParserType.PHARMA_MEDICINE -> pharmaExcelDir
			FExcelParserType.HOSPITAL -> hospitalExcelDir
			FExcelParserType.MEDICINE -> medicineExcelDir
			FExcelParserType.MEDICINE_INGREDIENT -> medicineIngredientExcelDir
			FExcelParserType.MEDICINE_PRICE -> medicinePriceExcelDir
		}
		return if (withTime) {
			Paths.get("${ret}/${getDateTimeString("yyyy-MM-dd")}").toAbsolutePath().normalize()
		} else {
			Paths.get(ret).toAbsolutePath().normalize()
		}
	}
	fun fileCopy(file: MultipartFile, excelType: FExcelParserType, specificWord: String): Path {
		val fileName = "${getDateTimeString("yyyyMMddHHmmss")}_${specificWord}_${file.originalFilename ?: "userFile"}"
		val targetLocation = fileLocation(excelType).resolve(fileName)
		Files.copy(file.inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING)
		return targetLocation
	}
	fun fileDelete(location: Path) {
		try {
			Files.delete(location)
		} catch (_: Exception) {
		}
	}

	fun getZoneId(): ZoneId {
//		return ZoneId.systemDefault()
		return ZoneId.of("UTC")
	}
	fun getDateTimeString(pattern: String): String {
		val currentDate = Date()
		val localDate = currentDate.toInstant().atZone(getZoneId()).toLocalDateTime()
		return localDate.format(DateTimeFormatter.ofPattern(pattern))
	}
	fun parseDateTimeString(date: Date?, pattern: String) = date?.let {
		val localDate = date.toInstant().atZone(getZoneId()).toLocalDateTime()
		localDate.format(DateTimeFormatter.ofPattern(pattern))
	}
	fun parseDateTimeString(date: java.sql.Date?, pattern: String) = date?.let {
		parseDateTimeString(Date(it.time), pattern)
	} ?: ""
	fun parseStringToSqlDate(date: String?, pattern: String): java.sql.Date = LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern)).let {
		java.sql.Date.valueOf(it)
	}
	fun parseStringToJavaDate(date: String?, pattern: String) = LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern)).let {
		Date.from(it.atStartOfDay(getZoneId()).toInstant())
	}
	fun toZeroTime(date: Date) =
		Date.from(date.toInstant().atZone(getZoneId()).toLocalDateTime().toLocalDate().atStartOfDay().atZone(getZoneId()).toInstant())
	fun toCloseTime(date: Date) =
		Date.from(date.toInstant().atZone(getZoneId()).toLocalDateTime().toLocalDate().atTime(23, 59, 59).atZone(getZoneId()).toInstant())
	fun getStartEndQueryDate(startDate: Date, endDate: Date) = if (startDate > endDate) {
		Pair(toZeroTime(endDate), toCloseTime(startDate))
	} else {
		Pair(toZeroTime(startDate), toCloseTime(endDate))
	}

	fun sampleFileDownload(excelType: FExcelParserType): Resource {
		val filePath = when (excelType) {
			FExcelParserType.USER -> Paths.get("${userExcelDir}/excel_upload_sample.xlsx").toAbsolutePath().normalize()
			FExcelParserType.PHARMA -> Paths.get("${pharmaExcelDir}/excel_upload_sample.xlsx").toAbsolutePath().normalize()
			FExcelParserType.PHARMA_MEDICINE -> Paths.get("${pharmaExcelDir}/pharma-medicine_excel_upload_sample.xlsx").toAbsolutePath().normalize()
			FExcelParserType.HOSPITAL -> Paths.get("${hospitalExcelDir}/excel_upload_sample.xlsx").toAbsolutePath().normalize()
			FExcelParserType.MEDICINE -> Paths.get("${medicineExcelDir}/excel_upload_sample.xlsx").toAbsolutePath().normalize()
			FExcelParserType.MEDICINE_INGREDIENT -> Paths.get("${medicineIngredientExcelDir}/excel_upload_sample.xlsx").toAbsolutePath().normalize()
			FExcelParserType.MEDICINE_PRICE -> Paths.get("${medicinePriceExcelDir}/excel_upload_sample.xlsx").toAbsolutePath().normalize()
		}
		return UrlResource(filePath.toUri())
	}
	fun getFileExt(file: MultipartFile): String {
		val fileName = file.originalFilename ?: "unknown"
		return fileName.substring(fileName.indexOfLast { it == '.' } + 1).lowercase(Locale.getDefault())
	}
	fun getMagicNumber(file: MultipartFile, byteCount: Int = 8): String {
		file.inputStream.use { x ->
			val ret = ByteArray(byteCount)
			x.read(ret, 0, byteCount)
			return ret.joinToString(" ") { y -> "%02X".format(y) }
		}
	}
	fun detectFileMimeType(file: MultipartFile, byteCount: Int = 8): String {
		val magicNumber = getMagicNumber(file, byteCount)
		val ext = when {
			magicNumber.startsWith("50 4B 03 04") -> "file.zip"
			magicNumber.startsWith("50 4B 30 30 50 4B 03 04") -> "file.zip"
			magicNumber.startsWith("25 50 44 46") -> "file.pdf"
			magicNumber.startsWith("FF D8 FF") -> "file.jpeg"
			magicNumber.startsWith("89 50 4E 47") -> "file.png"
			magicNumber.startsWith("42 4D") -> "file.bmp"
			magicNumber.startsWith("52 49 46 46") && getMagicNumber(file, 12).contains("57 45 42 50") -> "file.webp"
			magicNumber.startsWith("66 74 79 70 68 65 69 63") -> "file.heic"
			else -> file.originalFilename ?: "file.unknown"
		}
		return ContentsType.findContentType(ext)
	}

	fun regexSpecialCharRemove(data: String?) = data?.let { Regex(FConstants.REGEX_SPECIAL_CHAR_REMOVE).replace(it, "") } ?: ""
	fun regexOnlyAlphabet(data: String?) = data?.let { Regex(FConstants.REGEX_ONLY_ALPHABET).replace(it, "") } ?: ""
	fun escapeString(data: String?) = data?.let { it.replace(Regex(FConstants.REGEX_ESCAPE_SQL)) { "" } } ?: ""
}