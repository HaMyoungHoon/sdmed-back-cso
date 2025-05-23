package sdmed.back.controller.intra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import sdmed.back.config.*
import sdmed.back.model.sqlCSO.BlobUploadModel
import sdmed.back.model.sqlCSO.user.UserDataModel
import sdmed.back.model.sqlCSO.user.UserFileType
import sdmed.back.service.UserInfoService
import java.util.Date

@Tag(name = "intra 유저세팅/정보")
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
				@RequestParam(required = false) pharmaOwnMedicineView: Boolean = false,
				@RequestParam(required = false) relationMedicineView: Boolean = false) =
		responseService.getResult(userInfoService.getData(token, thisPK, childView, relationView, pharmaOwnMedicineView, relationMedicineView))
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
	@Operation(summary = "유저 파일 이미지 url 변경")
	@PutMapping(value = ["/file/{thisPK}"])
	fun putUserFileImageUrl(@RequestHeader token: String,
							@PathVariable thisPK: String,
	                        @RequestParam userFileType: UserFileType,
							@RequestBody blobModel: BlobUploadModel) =
		responseService.getResult(userInfoService.userFileUrlModify(token, thisPK, blobModel, userFileType).apply {
			azureBlobService.blobUploadSave(blobModel.newSave())
		})
	@Operation(summary = "유저 교육수료증 등록")
	@PostMapping(value = ["/file/training/{thisPK}"])
	fun postUserTrainingData(@RequestHeader token: String,
							 @PathVariable thisPK: String,
							 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) trainingDate: Date,
							 @RequestBody blobModel: BlobUploadModel) =
		responseService.getResult(userInfoService.addUserTrainingModel(token, thisPK, trainingDate, blobModel))

	@Operation(summary = "비밀번호 초기화")
	@PutMapping(value = ["/passwordInit"])
	fun putPasswordInit(@RequestHeader token: String,
						@RequestParam userPK: String) =
		responseService.getResult(userInfoService.passwordInit(token, userPK))
}