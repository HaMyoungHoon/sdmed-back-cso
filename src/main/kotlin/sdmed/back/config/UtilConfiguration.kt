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
	@Value(value = "\${file.pharmaExcelDir}") var pharmaExcelDir: String = ""
	@Value(value = "\${file.hospitalExcelDir}") var hospitalExcelDir: String = ""
	@Value(value = "\${file.medicineExcelDir}") var medicineExcelDir: String = ""

	@PostConstruct
	fun init() {
		FExtensions.defDir = defDir
		FExtensions.imageDir = imageDir
		FExtensions.documentDir = documentDir
		FExtensions.userExcelDir = userExcelDir
		FExtensions.pharmaExcelDir = pharmaExcelDir
		FExtensions.hospitalExcelDir = hospitalExcelDir
		FExtensions.medicineExcelDir = medicineExcelDir
	}
}