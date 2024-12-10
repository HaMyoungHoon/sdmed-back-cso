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
import sdmed.back.config.ContentsType
import sdmed.back.config.FConstants
import sdmed.back.config.FExcelParserType
import sdmed.back.config.FExtensions
import sdmed.back.model.common.IRestResult
import sdmed.back.model.common.UserRole
import sdmed.back.model.common.UserRoles
import sdmed.back.model.sqlCSO.BlobUploadModel
import sdmed.back.model.sqlCSO.PharmaModel
import sdmed.back.service.AzureBlobService
import sdmed.back.service.MedicineService
import sdmed.back.service.PharmaService
import sdmed.back.service.ResponseService


@Tag(name = "제약사목록")
@RestController
@RequestMapping(value = ["/intra/pharmaList"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class PharmaListController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var pharmaService: PharmaService
	@Autowired lateinit var medicineService: MedicineService
	@Autowired lateinit var azureBlobService: AzureBlobService

	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(pharmaService.getAllPharma(token))

	@Operation(summary = "편집 버튼 누르면 보이는 거")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
	            @PathVariable thisPK: String,
							@RequestParam(required = false) pharmaOwnMedicineView: Boolean = false) =
		responseService.getResult(pharmaService.getPharmaData(token, thisPK, pharmaOwnMedicineView))

	@Operation(summary = "엑셀 파일 업로드")
	@PostMapping(value = ["/file/excel"], consumes = ["multipart/form-data"])
	fun postExcel(@RequestHeader token: String,
	              @RequestParam file: MultipartFile) =
		responseService.getResult(pharmaService.pharmaUpload(token, file))

	@Operation(summary = "제약사 정보 추가")
	@PostMapping(value = ["/data"])
	fun postData(@RequestHeader token: String,
	             @RequestBody pharmaModel: PharmaModel) =
		responseService.getResult(pharmaService.addPharmaData(token, pharmaModel))

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

	@Operation(summary = "제약사 정보 수정")
	@PutMapping(value = ["/data"])
	fun putData(@RequestHeader token: String,
	            @RequestBody pharmaModel: PharmaModel) =
		responseService.getResult(pharmaService.pharmaDataModify(token, pharmaModel))

	@Operation(summary = "제약사 사업자 등록증 업로드")
	@PostMapping(value = ["/file/{thisPK}/image"])
	fun postImage(@RequestHeader token: String,
	              @PathVariable thisPK: String,
	              @RequestParam file: MultipartFile): IRestResult {
		pharmaService.isValid(token)
		val tokenUser = pharmaService.getUserDataByToken(token)
		if (!pharmaService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaChanger))) {
			throw AuthenticationEntryPointException()
		}

		val today = FExtensions.getDateTimeString("yyyyMMdd")
		val pharmaData = pharmaService.getPharmaData(token, thisPK)
		val blobUrl = azureBlobService.uploadFile(file, "pharma/$today", tokenUser.thisPK)
		pharmaData.imageUrl = blobUrl
		return responseService.getResult(pharmaService.pharmaDataModify(token, pharmaData))
	}

	@Operation(summary = "약품 넣기")
	@PutMapping(value = ["/data/{thisPK}/medicine/list"])
	fun putMedicine(@RequestHeader token: String,
									@PathVariable thisPK: String,
									@RequestBody medicinePKList: List<String>) =
		responseService.getResult(pharmaService.modPharmaDrugList(token, thisPK, medicinePKList))

	@Operation(summary = "약품 검색")
	@GetMapping(value = ["/medicine/list"])
	fun getMedicine(@RequestHeader token: String,
									@RequestParam searchString: String,
									@RequestParam(required = false) isSearchTypeCode: Boolean = false) =
		responseService.getResult(medicineService.getMedicineSearch(token, searchString, isSearchTypeCode))

	@Operation(summary = "제약사-약품 엑셀 샘플 다운로드")
	@GetMapping(value = ["/file/sample/pharmaMedicine"])
	fun getPharmaMedicineExcelSample(): ResponseEntity<Resource> =
		FExtensions.sampleFileDownload(FExcelParserType.PHARMA_MEDICINE).let { x ->
			ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(ContentsType.type_xlsx))
				.contentLength(x.file.length())
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"pharma-medicine_excel_upload_sample.xlsx\"")
				.body(x)
		}
	@Operation(summary = "제약사-약품 엑셀 파일 업로드")
	@PostMapping(value = ["/file/excel/pharmaMedicine"], consumes = ["multipart/form-data"])
	fun postPharmaMedicineExcel(@RequestHeader token: String,
	                            @RequestParam file: MultipartFile) =
		responseService.getResult(pharmaService.pharmaMedicineUpload(token, file))

	@Operation(summary = "제약사 사업자 등록증 업로드")
	@PutMapping(value = ["/file/{thisPK}/image"])
	fun putImage(@RequestHeader token: String,
	             @PathVariable thisPK: String,
	             @RequestBody blobModel: BlobUploadModel): IRestResult {
		pharmaService.isValid(token)
		val tokenUser = pharmaService.getUserDataByToken(token)
		if (!pharmaService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaChanger))) {
			throw AuthenticationEntryPointException()
		}

		val pharmaData = pharmaService.getPharmaData(token, thisPK)
		azureBlobService.blobUploadSave(blobModel.newSave())
		pharmaData.imageUrl = blobModel.blobUrl
		return responseService.getResult(pharmaService.pharmaDataModify(token, pharmaData))
	}
}