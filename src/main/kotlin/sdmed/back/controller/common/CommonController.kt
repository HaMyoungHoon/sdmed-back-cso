package sdmed.back.controller.common

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.*
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.NotFoundLanguageException
import sdmed.back.config.FConstants
import sdmed.back.model.common.IRestResult
import sdmed.back.model.common.UserRole
import sdmed.back.model.common.UserRoles
import sdmed.back.service.AzureBlobService
import sdmed.back.service.ResponseService
import sdmed.back.service.UserService
import java.util.*

@Tag(name = "CommonController")
@RestController
@RequestMapping(value = ["/common"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class CommonController {
	@Autowired lateinit var azureBlobService: AzureBlobService
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var userService: UserService
	@Value(value = "\${str.version}") lateinit var strVersion: String
	@Value(value = "\${str.profile}") lateinit var strprofile: String

	@Operation(summary = "language set", description = "")
	@PostMapping(value = ["/lang"])
	fun setLanguage(@RequestParam lang: String): IRestResult {
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

	@Operation(summary = "내 권한 얻기")
	@GetMapping(value = ["/myRole"])
	fun getMyRole(@RequestHeader token: String) =
		responseService.getResult(userService.getUserDataByToken(token).role)
	@GetMapping(value = ["/generate/sas"])
	fun getGenerateSas(@RequestHeader token: String,
										 @RequestParam(required = false) containerName: String = "",
										 @RequestParam blobUrl: String = ""): IRestResult {
		userService.isValid(token)
		val tokenUser = userService.getUserDataByToken(token)
		if (!userService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		val key = azureBlobService.generateSas(containerName, blobUrl)
		return responseService.getResult(key)
	}
}