package sdmed.back.controller.intra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import sdmed.back.config.ContentsType
import sdmed.back.config.FConstants
import sdmed.back.config.FExcelParserType
import sdmed.back.config.FExtensions
import sdmed.back.model.common.IRestResult
import sdmed.back.model.common.RoleDeptStatusModel
import sdmed.back.model.sqlCSO.BlobUploadModel
import sdmed.back.service.AzureBlobService
import sdmed.back.service.ResponseService
import sdmed.back.service.UserService

@Tag(name = "유저세팅/정보")
@RestController
@RequestMapping(value = ["/intra/userInfo"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class UserInfoController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var userService: UserService
	@Autowired lateinit var azureBlobService: AzureBlobService

	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(userService.getAllUser(token))
	@Operation(summary = "편집 버튼 누르면 보이는 거")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
							@PathVariable thisPK: String,
							@RequestParam(required = false) childView: Boolean = false,
							@RequestParam(required = false) relationView: Boolean = false,
							@RequestParam(required = false) pharmaOwnMedicineView: Boolean = false) =
		responseService.getResult(userService.getUserData(token, thisPK, childView, relationView, pharmaOwnMedicineView))
	@Operation(summary = "영업 유저 전체")
	@GetMapping(value = ["/all/business"])
	fun getUserAllBusiness(@RequestHeader token: String) =
		responseService.getResult(userService.getAllUserBusiness(token))

	@Operation(summary = "유저 데이터 엑셀 업로드")
	@PostMapping(value = ["/file/excel"], consumes = ["multipart/form-data"])
	fun postExcel(@RequestHeader token: String,
	                        @RequestParam file: MultipartFile) =
		responseService.getResult(userService.userUpload(token, file))

	@Operation(summary = "유저 데이터 엑셀 샘플 다운로드")
	@GetMapping(value = ["/file/sample"])
	fun getExcelSample(): ResponseEntity<Resource> {
		val ret = FExtensions.sampleFileDownload(FExcelParserType.USER)
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(ContentsType.type_xlsx))
			.contentLength(ret.file.length())
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"excel_upload_sample.xlsx\"")
			.body(ret)
	}

	@Operation(summary = "유저 이름, 메일 변경")
	@PutMapping(value = ["/userNameMailPhoneModify/pk"])
	fun putUserNameMailPhoneModifyByPK(@RequestHeader token: String,
	                                   @RequestParam userPK: String,
	                                   @RequestParam name: String,
	                                   @RequestParam mail: String,
	                                   @RequestParam phoneNumber: String) =
		responseService.getResult(userService.userNameMailPhoneModifyByPK(token, userPK, name, mail, phoneNumber))
	@Operation(summary = "유저 권한,부서,상태 변경")
	@PutMapping(value = ["/userRoleDeptStatusModify/pk"])
	fun putUserRoleDeptStatusModifyByPK(@RequestHeader token: String,
	                                    @RequestParam userPK: String,
	                                    @RequestBody data: RoleDeptStatusModel) =
		responseService.getResult(userService.userRoleDeptStatusModifyByPK(token, userPK, data))

	@Operation(summary = "유저 세금계산서 이미지 url 변경")
	@PutMapping(value = ["/file/{thisPK}/taxImage"])
	fun putUserTaxImageUrl(@RequestHeader token: String,
												 @PathVariable thisPK: String,
												 @RequestBody blobModel: BlobUploadModel): IRestResult {
		val ret = userService.userTaxImageUrlModify(token, thisPK, blobModel.blobUrl)
		azureBlobService.blobUploadSave(blobModel.newSave())
		return responseService.getResult(ret)
	}
	@Operation(summary = "유저 통장 이미지 url 변경")
	@PutMapping(value = ["/file/{thisPK}/bankImage"])
	fun putUserBankImageUrl(@RequestHeader token: String,
	                        @PathVariable thisPK: String,
	                        @RequestBody blobModel: BlobUploadModel): IRestResult {
		val ret = userService.userBankImageUrlModify(token, thisPK, blobModel.blobUrl)
		azureBlobService.blobUploadSave(blobModel.newSave())
		return responseService.getResult(ret)
	}
}