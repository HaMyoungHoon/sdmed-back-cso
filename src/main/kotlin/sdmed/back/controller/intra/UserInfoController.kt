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
import sdmed.back.config.*
import sdmed.back.model.common.IRestResult
import sdmed.back.model.sqlCSO.BlobUploadModel
import sdmed.back.model.sqlCSO.user.UserDataModel
import sdmed.back.service.UserInfoService

@Tag(name = "유저세팅/정보")
@RestController
@RequestMapping(value = ["/intra/userInfo"])
class UserInfoController: FControllerBase() {
	@Autowired lateinit var userInfoService: UserInfoService

	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(userInfoService.getList(token))
	@Operation(summary = "편집 버튼 누르면 보이는 거")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
							@PathVariable thisPK: String,
							@RequestParam(required = false) childView: Boolean = false,
							@RequestParam(required = false) relationView: Boolean = false,
							@RequestParam(required = false) pharmaOwnMedicineView: Boolean = false) =
		responseService.getResult(userInfoService.getData(token, thisPK, childView, relationView, pharmaOwnMedicineView))
	@Operation(summary = "자식으로 넣을 수 있는 유저 리스트")
	@GetMapping(value = ["/list/childAble/{thisPK}"])
	fun getListChildAble(@RequestHeader token: String,
											 @PathVariable thisPK: String) =
		responseService.getResult(userInfoService.getListChildAble(token, thisPK))
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

	@Operation(summary = "자식 넣기")
	@PostMapping(value = ["/data/{thisPK}"])
	fun postChildModify(@RequestHeader token: String,
											@PathVariable thisPK: String,
											@RequestBody childPK: List<String>) =
		responseService.getResult(userInfoService.childModify(token, thisPK, childPK))
	@Operation(summary = "유저 데이터 엑셀 업로드")
	@PostMapping(value = ["/file/excel"], consumes = ["multipart/form-data"])
	fun postExcel(@RequestHeader token: String,
	                        @RequestParam file: MultipartFile) =
		responseService.getResult(userInfoService.userUpload(token, file))

	@Operation(summary = "유저 데이터 모디파이")
	@PutMapping(value = ["/data"])
	fun putUser(@RequestHeader token: String,
							@RequestBody userData: UserDataModel) =
		responseService.getResult(userInfoService.userDataModify(token, userData))
	@Operation(summary = "유저 세금계산서 이미지 url 변경")
	@PutMapping(value = ["/file/{thisPK}/taxImage"])
	fun putUserTaxImageUrl(@RequestHeader token: String,
												 @PathVariable thisPK: String,
												 @RequestBody blobModel: BlobUploadModel) =
		userInfoService.userTaxImageUrlModify(token, thisPK, blobModel.blobUrl).apply {
			azureBlobService.blobUploadSave(blobModel.newSave())
		}
	@Operation(summary = "유저 통장 이미지 url 변경")
	@PutMapping(value = ["/file/{thisPK}/bankImage"])
	fun putUserBankImageUrl(@RequestHeader token: String,
	                        @PathVariable thisPK: String,
	                        @RequestBody blobModel: BlobUploadModel): IRestResult {
		val ret = userInfoService.userBankImageUrlModify(token, thisPK, blobModel.blobUrl)
		azureBlobService.blobUploadSave(blobModel.newSave())
		return responseService.getResult(ret)
	}
}