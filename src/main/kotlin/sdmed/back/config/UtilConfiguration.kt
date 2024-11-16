package sdmed.back.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class UtilConfiguration {
	@Value(value = "\${file.defDir}") var defDir: String = ""
	@Value(value = "\${file.imageDir}") var imageDir: String = ""
	@Value(value = "\${file.userExcelDir}") var userExcelDir: String = ""
	@Value(value = "\${file.correspondentExcelDir}") var correspondentExcelDir: String = ""

	@PostConstruct
	fun init() {
		FExtensions.defDir = defDir
		FExtensions.imageDir = imageDir
		FExtensions.userExcelDir = userExcelDir
		FExtensions.correspondentExcelDir = correspondentExcelDir
	}
}