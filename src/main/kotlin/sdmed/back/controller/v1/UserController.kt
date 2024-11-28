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
import sdmed.back.advice.exception.UserNotFoundException
import sdmed.back.config.ContentsType
import sdmed.back.config.FConstants
import sdmed.back.config.FExcelParserType
import sdmed.back.config.FExtensions
import sdmed.back.model.common.*
import sdmed.back.model.sqlCSO.HosPharmaMedicinePairModel
import sdmed.back.model.sqlCSO.UserDataModel
import sdmed.back.service.AzureBlobService
import sdmed.back.service.ResponseService
import sdmed.back.service.UserService

@Tag(name = "UserController")
@RestController
@RequestMapping(value = ["/v1/user"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class UserController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var userService: UserService
	@Autowired lateinit var azureBlobService: AzureBlobService

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
	fun signIn(@RequestParam(required = true) id: String,
	           @RequestParam(required = true) pw: String): IRestResult =
		responseService.getResult(userService.signIn(id, pw))
	@Operation(summary = "회원가입")
	@PostMapping(value = ["/signUp"])
	fun signUp(@RequestParam(required = true) confirmPW: String,
	           @RequestBody data: UserDataModel) =
		responseService.getResult(userService.signUp(confirmPW, data))

	@Operation(summary = "비밀번호 변경")
	@PutMapping(value = ["/passwordChange/id"])
	fun putPasswordChangeByID(@RequestHeader(required = true) token: String,
	                          @RequestParam(required = true) id: String,
	                          @RequestParam(required = true) changePW: String) =
		responseService.getResult(userService.passwordChangeByID(token, id, changePW))
	@Operation(summary = "비밀번호 변경")
	@PutMapping(value = ["/passwordChange/pk"])
	fun putPasswordChangeByPK(@RequestHeader(required = true) token: String,
	                          @RequestParam(required = true) userPK: String,
	                          @RequestParam(required = true) changePW: String) =
		responseService.getResult(userService.passwordChangeByPK(token, userPK, changePW))

	@Operation(summary = "로그인 토큰 새로고침")
	@PostMapping(value = ["/tokenRefresh"])
	fun tokenRefresh(@RequestHeader(required = true) token: String) =
		responseService.getResult(userService.tokenRefresh(token))
	@Operation(summary = "유저 데이터 검색")
	@GetMapping(value = ["/userData/id"])
	fun getUserDataByID(@RequestHeader(required = true) token: String,
	                    @RequestParam(required = false) id: String?,
	                    @RequestParam(required = false) childView: Boolean = false,
	                    @RequestParam(required = false) relationView: Boolean = false,
	                    @RequestParam(required = false) pharmaOwnMedicineView: Boolean = false): IRestResult {
		if (id == null) {
			return responseService.getResult(userService.getUserDataByToken(token))
		}

		if (userService.haveRole(token, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChildChanger, UserRole.UserFileUploader))) {
			return responseService.getResult(userService.getUserDataByPK(id, childView, relationView, pharmaOwnMedicineView))
		}
		return responseService.getResult(userService.getUserDataByPK(id, false, false, false))
	}
	@Operation(summary = "유저 데이터 검색")
	@GetMapping(value = ["/userData/pk"])
	fun getUserDataByPK(@RequestHeader(required = true) token: String,
	                    @RequestParam(required = false) userPK: String,
	                    @RequestParam(required = false) childView: Boolean = false,
	                    @RequestParam(required = false) relationView: Boolean = false,
	                    @RequestParam(required = false) pharmaOwnMedicineView: Boolean = false): IRestResult {
		if (userService.haveRole(token, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.UserChildChanger, UserRole.UserFileUploader))) {
			return responseService.getResult(userService.getUserDataByPK(userPK, childView, relationView, pharmaOwnMedicineView))
		}

		return responseService.getResult(userService.getUserDataByToken(token))
	}
	@Operation(summary = "유저-병원-제약사-약품 관계 변경")
	@PutMapping(value = ["/userRelModify/pk"])
	fun putUserRelationModifyByPK(@RequestHeader(required = true) token: String,
	                              @RequestParam(required = true) userPK: String,
	                              @RequestBody hosPharmaMedicinePairModel: List<HosPharmaMedicinePairModel>): IRestResult {
		return responseService.getResult(userService.userRelationModify(token, userPK, hosPharmaMedicinePairModel))
	}

	@Operation(summary = "내 권한 얻기")
	@PutMapping(value = ["/myRole"])
	fun getMyRole(@RequestHeader(required = true) token: String) =
		responseService.getResult(userService.getUserDataByToken(token).role)
	@Operation(summary = "유저 권한 변경")
	@PutMapping(value = ["/userRoleModify/id"])
	fun putUserRoleModifyByID(@RequestHeader(required = true) token: String,
	                          @RequestParam(required = true) id: String,
	                          @RequestBody(required = true) roles: List<UserRole>): IRestResult {
		if (id == "mhha") throw AuthenticationEntryPointException()
		return responseService.getResult(userService.userRoleModifyByID(token, id, roles))
	}
	@Operation(summary = "유저 권한 변경")
	@PutMapping(value = ["/userRoleModify/pk"])
	fun putUserRoleModifyByPK(@RequestHeader(required = true) token: String,
	                          @RequestParam(required = true) userPK: String,
	                          @RequestBody(required = true) roles: List<UserRole>): IRestResult {
		return responseService.getResult(userService.userRoleModifyByPK(token, userPK, roles))
	}

	@Operation(summary = "유저 부서 변경")
	@PutMapping(value = ["/userDeptModify/id"])
	fun putUserDeptModifyByID(@RequestHeader(required = true) token: String,
	                      @RequestParam(required = true) id: String,
	                      @RequestBody(required = true) depts: List<UserDept>): IRestResult {
		if (id == "mhha") throw AuthenticationEntryPointException()
		return responseService.getResult(userService.userDeptModifyByID(token, id, depts))
	}
	@Operation(summary = "유저 부서 변경")
	@PutMapping(value = ["/userDeptModify/pk"])
	fun putUserDeptModifyByPK(@RequestHeader(required = true) token: String,
	                          @RequestParam(required = true) userPK: String,
	                          @RequestBody(required = true) depts: List<UserDept>): IRestResult {
		return responseService.getResult(userService.userDeptModifyByPK(token, userPK, depts))
	}

	@Operation(summary = "유저 상태 변경")
	@PutMapping(value = ["/userStatusModify/id"])
	fun putUserStatusModifyByID(@RequestHeader(required = true) token: String,
	                            @RequestParam(required = true) id: String,
	                            @RequestParam(required = true) status: UserStatus): IRestResult {
		if (id == "mhha") throw AuthenticationEntryPointException()
		return responseService.getResult(userService.userStatusModifyByID(token, id, status))
	}
	@Operation(summary = "유저 상태 변경")
	@PutMapping(value = ["/userStatusModify/pk"])
	fun putUserStatusModifyByPK(@RequestHeader(required = true) token: String,
	                            @RequestParam(required = true) userPK: String,
	                            @RequestParam(required = true) status: UserStatus): IRestResult {
		return responseService.getResult(userService.userStatusModifyByPK(token, userPK, status))
	}

	@Operation(summary = "유저 계좌이미지 업로드")
	@PutMapping(value = ["/userBankImageUpload/id"], consumes = ["multipart/form-data"])
	fun putUserBankImageUploadByID(@RequestHeader(required = true) token: String,
	                               @RequestParam(required = true) id: String,
	                               @RequestParam file: MultipartFile): IRestResult {
		userService.isValid(token)
		val tokenUser = userService.getUserDataByToken(token)
		if (!userService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.StatusChanger))) {
			throw AuthenticationEntryPointException()
		}

		val today = FExtensions.getDateTimeString("yyyyMMdd")
		val userData = userService.getUserDataByID(id)
		val blobUrl = azureBlobService.uploadFile(file, "${userData.id}/${today}", tokenUser.thisPK)
		userData.bankAccountImageUrl = blobUrl
		return responseService.getResult(userService.userDataModify(token, userData))
	}
	@Operation(summary = "유저 계좌이미지 업로드")
	@PutMapping(value = ["/userBankImageUpload/pk"], consumes = ["multipart/form-data"])
	fun putUserBankImageUploadByPK(@RequestHeader(required = true) token: String,
	                               @RequestParam(required = true) userPK: String,
	                               @RequestParam file: MultipartFile): IRestResult {
		userService.isValid(token)
		val tokenUser = userService.getUserDataByToken(token)
		if (!userService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.StatusChanger))) {
			throw AuthenticationEntryPointException()
		}

		val today = FExtensions.getDateTimeString("yyyyMMdd")
		val userData = userService.getUserDataByID(userPK)
		val blobUrl = azureBlobService.uploadFile(file, "${userData.id}/${today}", tokenUser.thisPK)
		userData.bankAccountImageUrl = blobUrl
		return responseService.getResult(userService.userDataModify(token, userData))
	}

	@Operation(summary = "유저 사업자등록증 업로드")
	@PutMapping(value = ["/userTaxImageUpload/id"], consumes = ["multipart/form-data"])
	fun putUserTaxImageUploadByID(@RequestHeader(required = true) token: String,
	                              @RequestParam(required = true) id: String,
	                              @RequestParam file: MultipartFile): IRestResult {
		userService.isValid(token)
		val tokenUser = userService.getUserDataByToken(token)
		if (!userService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.StatusChanger))) {
			throw AuthenticationEntryPointException()
		}

		val today = FExtensions.getDateTimeString("yyyyMMdd")
		val userData = userService.getUserDataByID(id)
		val blobUrl = azureBlobService.uploadFile(file, "${userData.id}/${today}", tokenUser.thisPK)
		userData.taxpayerImageUrl = blobUrl
		return responseService.getResult(userService.userDataModify(token, userData))
	}
	@Operation(summary = "유저 사업자등록증 업로드")
	@PutMapping(value = ["/userTaxImageUpload/pk"], consumes = ["multipart/form-data"])
	fun putUserTaxImageUploadByPK(@RequestHeader(required = true) token: String,
	                              @RequestParam(required = true) userPK: String,
	                              @RequestParam file: MultipartFile): IRestResult {
		userService.isValid(token)
		val tokenUser = userService.getUserDataByToken(token)
		if (!userService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.StatusChanger))) {
			throw AuthenticationEntryPointException()
		}

		val today = FExtensions.getDateTimeString("yyyyMMdd")
		val userData = userService.getUserDataByID(userPK)
		val blobUrl = azureBlobService.uploadFile(file, "${userData.id}/${today}", tokenUser.thisPK)
		userData.taxpayerImageUrl = blobUrl
		return responseService.getResult(userService.userDataModify(token, userData))
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