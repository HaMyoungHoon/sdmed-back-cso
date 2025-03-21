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
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.config.*
import sdmed.back.model.common.IRestResult
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.BlobUploadModel
import sdmed.back.model.sqlCSO.pharma.PharmaModel
import sdmed.back.service.PharmaListService


@Tag(name = "intra 제약사목록")
@RestController
@RequestMapping(value = ["/intra/pharmaList"])
class PharmaListController: FControllerBase() {
	@Autowired lateinit var pharmaListService: PharmaListService

	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(pharmaListService.getList(token))
	@Operation(summary = "편집 버튼 누르면 보이는 거")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
	            @PathVariable thisPK: String,
				@RequestParam(required = false) pharmaOwnMedicineView: Boolean = false) =
		responseService.getResult(pharmaListService.getData(token, thisPK, pharmaOwnMedicineView))
	@Operation(summary = "약품 검색")
	@GetMapping(value = ["/medicine/list"])
	fun getMedicine(@RequestHeader token: String,
					@RequestParam thisPK: String,
	                @RequestParam searchString: String,
	                @RequestParam(required = false) isSearchTypeCode: Boolean = false) =
		responseService.getResult(pharmaListService.getMedicineSearch(token, thisPK, searchString, isSearchTypeCode))
	@Operation(summary = "엑셀 샘플 다운로드")
	@GetMapping(value = ["/file/sample"])
	fun getExcelSample(): ResponseEntity<Resource> =
		FExtensions.sampleFileDownload(FExcelParserType.PHARMA).let { x ->
			ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(ContentsType.type_xlsx))
				.contentLength(x.file.length())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"excel_upload_sample.xlsx\"")
				.body(x)
		}

	@Operation(summary = "엑셀 파일 업로드")
	@PostMapping(value = ["/file/excel"], consumes = ["multipart/form-data"])
	fun postExcel(@RequestHeader token: String,
	              @RequestParam file: MultipartFile) =
		responseService.getResult(pharmaListService.pharmaUpload(token, file))
	@Operation(summary = "제약사 정보 추가")
	@PostMapping(value = ["/data"])
	fun postData(@RequestHeader token: String,
	             @RequestBody pharmaModel: PharmaModel) =
		responseService.getResult(pharmaListService.addPharmaData(token, pharmaModel))
	@Operation(summary = "제약사 사업자 등록증 업로드")
	@PostMapping(value = ["/file/{thisPK}/image"])
	fun postImage(@RequestHeader token: String,
	              @PathVariable thisPK: String,
	              @RequestParam file: MultipartFile): IRestResult {
		pharmaListService.isValid(token)
		val tokenUser = pharmaListService.getUserDataByToken(token)
		if (!pharmaListService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaChanger))) {
			throw AuthenticationEntryPointException()
		}

		val today = FExtensions.getDateTimeString("yyyyMMdd")
		val pharmaData = pharmaListService.getData(token, thisPK)
		val blobUrl = azureBlobService.uploadFile(file, "pharma/$today", tokenUser.thisPK)
		pharmaData.imageUrl = blobUrl
		return responseService.getResult(pharmaListService.pharmaDataModify(token, pharmaData))
	}

	@Operation(summary = "제약사 정보 수정")
	@PutMapping(value = ["/data"])
	fun putData(@RequestHeader token: String,
	            @RequestBody pharmaModel: PharmaModel) =
		responseService.getResult(pharmaListService.pharmaDataModify(token, pharmaModel))
	@Operation(summary = "약품 넣기")
	@PutMapping(value = ["/data/{thisPK}/medicine/list"])
	fun putMedicine(@RequestHeader token: String,
					@PathVariable thisPK: String,
									@RequestBody medicinePKList: List<String>) =
		responseService.getResult(pharmaListService.modPharmaDrugList(token, thisPK, medicinePKList))
	@Operation(summary = "제약사 사업자 등록증 업로드")
	@PutMapping(value = ["/file/{thisPK}/image"])
	fun putImage(@RequestHeader token: String,
	             @PathVariable thisPK: String,
	             @RequestBody blobModel: BlobUploadModel): IRestResult {
		pharmaListService.isValid(token)
		val tokenUser = pharmaListService.getUserDataByToken(token)
		if (!pharmaListService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaChanger))) {
			throw AuthenticationEntryPointException()
		}

		val pharmaData = pharmaListService.getPharmaData(token, thisPK)
		azureBlobService.blobUploadSave(blobModel.newSave())
		pharmaData.imageUrl = blobModel.blobUrl
		return responseService.getResult(pharmaListService.pharmaDataModify(token, pharmaData))
	}
}