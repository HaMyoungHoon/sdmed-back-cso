package sdmed.back.controller.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.config.ContentsType
import sdmed.back.config.FConstants
import sdmed.back.config.FExcelParserType
import sdmed.back.config.FExtensions
import sdmed.back.model.common.IRestResult
import sdmed.back.model.common.UserDept
import sdmed.back.model.common.UserRole
import sdmed.back.model.common.UserStatus
import sdmed.back.model.sqlCSO.UserDataModel
import sdmed.back.service.ResponseService
import sdmed.back.service.UserService

@Tag(name = "UserController")
@RestController
@RequestMapping(value = ["/v1/user"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class UserController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var userService: UserService

	@Operation(summary = "유저 정보 전체")
	@GetMapping(value = ["/all"])
	fun getUserAll(@RequestHeader(required = true) token: String) =
		responseService.getResult(userService.getAllUser(token))
	@Operation(summary = "유저 정보 전체")
	@GetMapping(value = ["/all/{page}/{size}"])
	fun getUserAllPage(@RequestHeader(required = true) token: String,
										 @PathVariable("page") page: Int,
										 @PathVariable("size") size: Int) =
		responseService.getResult(userService.getAllUser(token, page, size))

	@Operation(summary = "로그인")
	@GetMapping(value = ["/signIn"])
	fun signIn(@RequestParam(required = true) id: String, @RequestParam(required = true) pw: String): IRestResult =
		responseService.getResult(userService.signIn(id, pw))
	@Operation(summary = "회원가입")
	@PostMapping(value = ["/signUp"])
	fun signUp(@RequestParam(required = true) confirmPW: String,
	           @RequestBody data: UserDataModel) =
		responseService.getResult(userService.signUp(confirmPW, data))
	@Operation(summary = "비밀번호 변경")
	@PutMapping(value = ["/passwordChange"])
	fun passwordChange(@RequestHeader(required = true) token: String,
	                   @RequestParam(required = true) id: String,
	                   @RequestParam(required = true) changePW: String) =
		responseService.getResult(userService.passwordChange(token, id, changePW))
	@Operation(summary = "로그인 토큰 새로고침")
	@PostMapping(value = ["/tokenRefresh"])
	fun tokenRefresh(@RequestHeader(required = true) token: String) =
		responseService.getResult(userService.tokenRefresh(token))
	@Operation(summary = "유저 데이터 검색")
	@GetMapping(value = ["/userData"])
	fun getUserData(@RequestHeader(required = true) token: String, @RequestParam(required = false) id: String?): IRestResult {
		if (id == null) {
			return responseService.getResult(userService.getUserDataByToken(token))
		}

		if (userService.haveRole(token, UserRole.Admin.toS())) {
			return responseService.getResult(userService.getUserData(id))
		}
		return responseService.getResult(userService.getUserDataByToken(token))
	}
	@Operation(summary = "유저 권한 변경")
	@PutMapping(value = ["/userRoleModify"])
	fun putUserRoleModify(@RequestHeader(required = true) token: String,
												@RequestParam(required = true) id: String,
												@RequestParam(required = true) roles: List<UserRole>): IRestResult {
		if (id == "mhha") throw AuthenticationEntryPointException()
		return responseService.getResult(userService.userRoleModify(token, id, roles))
	}
	@Operation(summary = "유저 부서 변경")
	@PutMapping(value = ["/userDeptModify"])
	fun putUserDeptModify(@RequestHeader(required = true) token: String,
	                      @RequestParam(required = true) id: String,
	                      @RequestParam(required = true) depts: List<UserDept>): IRestResult {
		if (id == "mhha") throw AuthenticationEntryPointException()
		return responseService.getResult(userService.userDeptModify(token, id, depts))
	}
	@Operation(summary = "유저 상태 변경")
	@PutMapping(value = ["/userStatusModify"])
	fun putUserStatusModify(@RequestHeader(required = true) token: String,
	                        @RequestParam(required = true) id: String,
	                        @RequestParam(required = true) status: UserStatus): IRestResult {
		if (id == "mhha") throw AuthenticationEntryPointException()
		return responseService.getResult(userService.userStatusModify(token, id, status))
	}
	@Operation(summary = "유저 데이터 엑셀 업로드")
	@PostMapping(value = ["/dataUploadExcel"], consumes = ["multipart/form-data"])
	fun postDataUploadExcel(@RequestHeader(required = true) token: String,
										 @RequestParam(required = true) file: MultipartFile) =
		responseService.getResult(userService.userUpload(token, file))

	@Operation(summary = "유저 데이터 엑셀 샘플 다운로드")
	@GetMapping(value = ["/sampleDownloadExcel"])
	fun getSampleDownloadExcel(): ResponseEntity<Resource> {
		val ret = FExtensions.sampleFileDownload(FExcelParserType.USER)
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(ContentsType.type_xlsx))
			.contentLength(ret.file.length())
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"excel_upload_sample.xlsx\"")
			.body(ret)
	}

	@Operation(summary = "유저 상태 목록")
	@GetMapping(value = ["/statusList"])
	fun getStatusList() = responseService.getResult(userService.getUserStatusList())
	@Operation(summary = "유저 권한 목록")
	@GetMapping(value = ["/roleList"])
	fun getRoleList(): IRestResult = responseService.getResult(userService.getUserRoleList())
	@Operation(summary = "유저 부서 목록")
	@GetMapping(value = ["/deptList"])
	fun getDeptList(): IRestResult = responseService.getResult(userService.getUserDeptList())

	@Operation(summary = "유저 child 추가")
	@PostMapping(value = ["/addChild"])
	fun postAddChild(@RequestHeader(required = true) token: String,
									 @RequestParam(required = true) motherID: String,
									 @RequestBody(required = true) childID: List<String>) =
		responseService.getResult(userService.addChild(token, motherID, childID))
	@Operation(summary = "유저 child 제거")
	@PutMapping(value = ["/delChild"])
	fun putDelChild(@RequestHeader(required = true) token: String,
									 @RequestParam(required = true) motherID: String,
									 @RequestBody(required = true) childID: List<String>) =
		responseService.getResult(userService.delChild(token, motherID, childID))
}