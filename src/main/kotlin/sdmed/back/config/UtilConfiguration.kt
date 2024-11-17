package sdmed.back.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class UtilConfiguration {
	@Value(value = "\${file.defDir}") var defDir: String = ""
	@Value(value = "\${file.imageDir}") var imageDir: String = ""
	@Value(value = "\${file.documentDir}") var documentDir: String = ""
	@Value(value = "\${file.userExcelDir}") var userExcelDir: String = ""
	@Value(value = "\${file.correspondentExcelDir}") var correspondentExcelDir: String = ""
	@Value(value = "\${file.pharmaExcelDir}") var pharmaExcelDir: String = ""
	@Value(value = "\${file.hospitalExcelDir}") var hospitalExcelDir: String = ""

	@PostConstruct
	fun init() {
		FExtensions.defDir = defDir
		FExtensions.imageDir = imageDir
		FExtensions.documentDir = documentDir
		FExtensions.userExcelDir = userExcelDir
		FExtensions.correspondentExcelDir = correspondentExcelDir
		FExtensions.pharmaExcelDir = pharmaExcelDir
		FExtensions.hospitalExcelDir = hospitalExcelDir
	}
}