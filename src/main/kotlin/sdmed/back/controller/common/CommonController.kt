package sdmed.back.controller.common

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.NotFoundLanguageException
import sdmed.back.config.FConstants
import sdmed.back.model.common.IRestResult
import sdmed.back.service.AzureBlobService
import sdmed.back.service.ResponseService
import java.util.*

@Tag(name = "CommonController")
@RestController
@RequestMapping(value = ["/common"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class CommonController {
	@Autowired lateinit var azureBlobService: AzureBlobService
	@Autowired lateinit var responseService: ResponseService
	@Value(value = "\${str.version}") lateinit var strVersion: String
	@Value(value = "\${str.profile}") lateinit var strprofile: String

	@Operation(summary = "language set", description = "")
	@PostMapping(value = ["/lang"])
	fun setLanguage(@Parameter(name = "lang") @RequestParam lang: String): IRestResult {
		when (lang) {
			"ko" -> LocaleContextHolder.setDefaultLocale(Locale.KOREA)
			"en" -> LocaleContextHolder.setDefaultLocale(Locale.ENGLISH)
			else -> throw NotFoundLanguageException()
		}

		return responseService.getSuccessResult()
	}
	@GetMapping(value = ["/version"])
	fun version(): IRestResult {
		return responseService.getResult("$strprofile $strVersion")
	}

//	@PostMapping(value = ["/test"], consumes = ["multipart/form-data"])
//	fun test(@RequestParam(required = true) testFile: MultipartFile): IRestResult = responseService.getResult(azureBlobService.uploadTest(testFile))
}