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

	fun fileTransfer(excelType: FExcelParserType) {

	}
	fun folderExist(excelType: FExcelParserType) {
		Optional.ofNullable(Files.createDirectories(fileLocation(excelType))).orElseThrow { FileUploadException() }
	}
	fun fileLocation(excelType: FExcelParserType, withTime: Boolean = true): Path {
		val ret = when (excelType) {
			FExcelParserType.USER -> userExcelDir
			FExcelParserType.PHARMA -> pharmaExcelDir
			FExcelParserType.HOSPITAL -> hospitalExcelDir
			FExcelParserType.MEDICINE -> medicineExcelDir
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

	fun getDateTimeString(pattern: String): String {
		val currentDate = Date()
		val localDate = currentDate.toInstant().atZone(java.time.ZoneId.of("UTC")).toLocalDateTime()
		return localDate.format(DateTimeFormatter.ofPattern(pattern))
	}
	fun parseDateTimeString(date: Date?, pattern: String) = date?.let {
		val localDate = date.toInstant().atZone(java.time.ZoneId.of("UTC")).toLocalDateTime()
		localDate.format(DateTimeFormatter.ofPattern(pattern))
	}
	fun parseDateTimeString(date: java.sql.Date?, pattern: String) = date?.let {
		parseDateTimeString(Date(it.time), pattern)
	} ?: ""
	fun parseStringToSqlDate(date: String?, pattern: String): java.sql.Date {
		val localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern))
		return java.sql.Date.valueOf(localDate)
	}
	fun parseStringToJavaDate(date: String?, pattern: String): Date {
		val localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern))
		return Date.from(localDate.atStartOfDay(java.time.ZoneId.of("UTC")).toInstant())
	}

	fun sampleFileDownload(excelType: FExcelParserType): Resource {
		val filePath = when (excelType) {
			FExcelParserType.USER -> Paths.get("${userExcelDir}/excel_upload_sample.xlsx").toAbsolutePath().normalize()
			FExcelParserType.PHARMA -> Paths.get("${pharmaExcelDir}/excel_upload_sample.xlsx").toAbsolutePath().normalize()
			FExcelParserType.HOSPITAL -> Paths.get("${hospitalExcelDir}/excel_upload_sample.xlsx").toAbsolutePath().normalize()
			FExcelParserType.MEDICINE -> Paths.get("${medicineExcelDir}/excel_upload_sample.xlsx").toAbsolutePath().normalize()
		}
		return UrlResource(filePath.toUri())
	}

	fun regexSpecialCharRemove(data: String?) = data?.let { Regex(FConstants.REGEX_SPECIAL_CHAR_REMOVE).replace(it, "") } ?: ""
	fun regexOnlyAlphabet(data: String?) = data?.let { Regex(FConstants.REGEX_ONLY_ALPHABET).replace(it, "") } ?: ""
	fun escapeString(data: String?) = data?.let { it.replace(Regex(FConstants.REGEX_ESCAPE_SQL)) { x -> ""} } ?: ""
}