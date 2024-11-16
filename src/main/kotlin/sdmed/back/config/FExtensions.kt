package sdmed.back.config

import org.apache.tomcat.util.http.fileupload.FileUploadException
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
	var userExcelDir: String = ""
	var correspondentExcelDir: String = ""

	fun fileTransfer(excelType: FExcelParserType) {

	}
	fun folderExist(excelType: FExcelParserType) {
		Optional.ofNullable(Files.createDirectories(fileLocation(excelType))).orElseThrow { FileUploadException() }
	}
	fun fileLocation(excelType: FExcelParserType): Path {
		val ret = when (excelType) {
			FExcelParserType.USER -> userExcelDir
			FExcelParserType.CORRESPONDENT -> correspondentExcelDir
		}
		return Paths.get("${ret}/${getDateTimeString("yyyy-MM-dd")}").toAbsolutePath().normalize()
	}
	fun fileCopy(file: MultipartFile, excelType: FExcelParserType): Path {
		val fileName = "${file.originalFilename ?: "userFile"}_${getDateTimeString("yyyy-MM-dd HHmmss")}"
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
		val localDate = currentDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
		return localDate.format(DateTimeFormatter.ofPattern(pattern))
	}
	fun parseDateTimeString(date: Date?, pattern: String): String {
		if (date == null) {
			return ""
		}

		val localDate = date.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate()
		return localDate.format(DateTimeFormatter.ofPattern(pattern))
	}
	fun parseStringToSqlDate(date: String?, pattern: String): java.sql.Date {
		val localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern))
		return java.sql.Date.valueOf(localDate)
	}
	fun parseStringToJavaDate(date: String?, pattern: String): Date {
		val localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern))
		return Date.from(localDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant())
	}
}