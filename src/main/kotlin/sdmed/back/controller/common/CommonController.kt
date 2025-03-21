package sdmed.back.controller.common

import io.swagger.v3.oas.annotations.Hidden
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.NotFoundLanguageException
import sdmed.back.config.FControllerBase
import sdmed.back.model.common.IRestResult
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.common.VersionCheckType
import sdmed.back.model.sqlCSO.user.UserDataModel
import sdmed.back.service.CommonService
import sdmed.back.service.UserService
import java.util.*

@Tag(name = "CommonController")
@RestController
@RequestMapping(value = ["/common"])
class CommonController: FControllerBase() {
	@Autowired lateinit var userService: UserService
	@Autowired lateinit var commonService: CommonService
	@Value(value = "\${str.version}") lateinit var strVersion: String
	@Value(value = "\${str.profile}") lateinit var strprofile: String

	@Operation(summary = "로그인")
	@GetMapping(value = ["/signIn"])
	fun signIn(@RequestParam id: String,
	           @RequestParam pw: String) =
		responseService.getResult(userService.signIn(id, pw))
	@Operation(summary = "로그인")
	@GetMapping(value = ["/multiSign"])
	fun signIn(@RequestParam token: String) =
		responseService.getResult(userService.tokenRefresh(token))
	@Operation(summary = "회원가입")
	@PostMapping(value = ["/signUp"])
	fun signUp(@RequestParam confirmPW: String,
	           @RequestBody data: UserDataModel) =
		responseService.getResult(userService.newUser(confirmPW, data))
	@Operation(summary = "유저 만들기")
	@PostMapping(value = ["/newUser"])
	fun newUser(@RequestHeader token: String,
							@RequestParam confirmPW: String,
							@RequestBody data: UserDataModel) =
		responseService.getResult(userService.newUser(token, confirmPW, data))
	@Operation(summary = "로그인 토큰 새로고침")
	@PostMapping(value = ["/tokenRefresh"])
	fun tokenRefresh(@RequestHeader token: String) =
		responseService.getResult(userService.tokenRefresh(token))

	@Operation(summary = "아이디 까먹음")
	@GetMapping(value = ["/findIDAuthNumber"])
	fun getFindIDAuthNumber(@RequestParam name: String, @RequestParam phoneNumber: String) =
		responseService.getSuccessResult().apply {
			commonService.getFindIDAuthNumber(name, phoneNumber)
		}
	@Operation(summary = "비밀번호 까먹음")
	@GetMapping(value = ["/findPWAuthNumber"])
	fun getFindPWAuthNumber(@RequestParam id: String, @RequestParam phoneNumber: String) =
		responseService.getSuccessResult().apply {
			commonService.getFindPWAuthNumber(id, phoneNumber)
		}
	@Operation(summary = "check auth number")
	@GetMapping(value = ["/checkAuthNumber"])
	fun getCheckAuthNumber(@RequestParam authNumber: String, @RequestParam phoneNumber: String) =
		responseService.getResult(commonService.getCheckAuthNumber(authNumber, phoneNumber))

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
	fun version() = responseService.getResult("$strprofile $strVersion")
	@GetMapping(value = ["/versionCheck"])
	fun versionCheck(@RequestParam versionCheckType: VersionCheckType) = responseService.getResult(commonService.getAbleVersion(versionCheckType))
	@GetMapping(value = ["/serverTime"])
	fun serverTime() = responseService.getResult(Date())

	@Operation(summary = "내 권한 얻기")
	@GetMapping(value = ["/myRole"])
	fun getMyRole(@RequestHeader token: String) =
		responseService.getResult(userService.getUserDataByToken(token).role)
	@Operation(summary = "내 상태 얻기")
	@GetMapping(value = ["/myState"])
	fun getMyState(@RequestHeader token: String) =
		responseService.getResult(userService.getUserDataByToken(token).status)
	@GetMapping(value = ["/blobStorageInfo"])
	fun getBlobStorageInfo(@RequestHeader token: String): IRestResult {
		userService.isValid(token)
		val tokenUser = userService.getUserDataByToken(token)
		if (!userService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		userService.isLive(tokenUser)
		return responseService.getResult(azureBlobService.getBlobStorageInfo())
	}
	@GetMapping(value = ["/generate/sas"])
	fun getGenerateSas(@RequestHeader token: String,
										 @RequestParam(required = false) containerName: String = "",
										 @RequestParam blobName: String = ""): IRestResult {
		userService.isValid(token)
		val tokenUser = userService.getUserDataByToken(token)
		if (!userService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		userService.isLive(tokenUser)

		val key = azureBlobService.generateSas(containerName, blobName)
		return responseService.getResult(key)
	}
	@PostMapping(value = ["/generate/sas/list"])
	fun postGenerateSasList(@RequestHeader token: String,
	                        @RequestParam(required = false) containerName: String = "",
	                        @RequestBody blobName: List<String> = mutableListOf()): IRestResult {
		userService.isValid(token)
		val tokenUser = userService.getUserDataByToken(token)
		if (!userService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		userService.isLive(tokenUser)

		val key = azureBlobService.generateSasList(containerName, blobName)
		return responseService.getResult(key)
	}

	@Hidden
	@PostMapping("/test", consumes = ["multipart/form-data"])
	fun test(@RequestParam file: MultipartFile): IRestResult {
		throw AuthenticationEntryPointException()
		val key = azureBlobService.uploadFile(file, "test", "fc1b9a2e-ae8a-437d-8074-a19d3acd1813")
		return responseService.getResult(key)
	}

}