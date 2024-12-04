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
import sdmed.back.model.common.UserRole
import sdmed.back.model.common.UserRoles
import sdmed.back.model.sqlCSO.PharmaModel
import sdmed.back.service.AzureBlobService
import sdmed.back.service.PharmaService
import sdmed.back.service.ResponseService

@Tag(name = "PharmaController")
@RestController
	@RequestMapping(value = ["/v1/pharma"])
	@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class PharmaController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var pharmaService: PharmaService
	@Autowired lateinit var azureBlobService: AzureBlobService

	@Operation(summary = "제약사 정보 조회")
	@GetMapping(value = ["/all"])
	fun getPharmaAll(@RequestHeader token: String) =
		responseService.getResult(pharmaService.getAllPharma(token))
	@Operation(summary = "제약사 정보 조회")
	@GetMapping(value = ["/all/{page}/{size}"])
	fun getPharmaAllPage(@RequestHeader token: String,
	                     @PathVariable("page") page: Int,
	                     @PathVariable("size") size: Int) =
		responseService.getResult(pharmaService.getPagePharma(token, page, size))
	@Operation(summary = "제약사 조회 like code, innerName, orgName")
	@GetMapping(value = ["/all/search"])
	fun getPharmaAllSearch(@RequestHeader token: String,
	                       @RequestParam searchString: String,
	                       @RequestParam(required = false) isSearchTypeCode: Boolean = false) =
		responseService.getResult(pharmaService.getPharmaAllSearch(token, searchString, isSearchTypeCode))

	@Operation(summary = "제약사 조회")
	@GetMapping(value = ["/{pharmaPK}"])
	fun getPharmaData(@RequestHeader token: String,
	                  @PathVariable("pharmaPK") pharmaPK: String,
	                  @RequestParam(required = false) pharmaOwnMedicineView: Boolean = false) =
		responseService.getResult(pharmaService.getPharmaData(token, pharmaPK, pharmaOwnMedicineView))
	@Operation(summary = "제약사 medicine list modify")
	@PutMapping(value = ["/{pharmaPK}/modMedicine"])
	fun putPharmaModMedicine(@RequestHeader token: String,
													 @PathVariable("pharmaPK") pharmaPK: String,
													 @RequestBody medicinePKList: List<String>) =
		responseService.getResult(pharmaService.modPharmaDrugList(token, pharmaPK, medicinePKList))

	@Operation(summary = "제약사 정보 추가")
	@PostMapping(value = ["/add"])
	fun postPharmaData(@RequestHeader token: String,
										 @RequestBody pharmaData: PharmaModel) =
		responseService.getResult(pharmaService.addPharmaData(token, pharmaData))
	@Operation(summary = "제약사 데이터 엑셀 업로드")
	@PostMapping(value = ["/dataUploadExcel"], consumes = ["multipart/form-data"])
	fun postDataUploadExcel(@RequestHeader token: String,
	                        @RequestParam file: MultipartFile) =
		responseService.getResult(pharmaService.pharmaUpload(token, file))
	@Operation(summary = "제약사 사업자등록증 업로드")
	@PostMapping(value = ["/imageUpload"], consumes = ["multipart/form-data"])
	fun postImageUpload(@RequestHeader token: String,
											@RequestParam thisPK: String,
											@RequestParam file: MultipartFile): IRestResult {
		pharmaService.isValid(token)
		val tokenUser = pharmaService.getUserDataByToken(token)
		if (!pharmaService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val today = FExtensions.getDateTimeString("yyyyMMdd")
		val pharmaData = pharmaService.getPharmaData(token, thisPK)
		val blobUrl = azureBlobService.uploadFile(file, today, tokenUser.thisPK)
		pharmaData.imageUrl = blobUrl
		return responseService.getResult(pharmaService.pharmaDataModify(token, pharmaData))
	}

	@Operation(summary = "제약사 데이터 엑셀 샘플 다운로드")
	@GetMapping(value = ["/sampleDownloadExcel"])
	fun getSampleDownloadExcel(): ResponseEntity<Resource> {
		val ret = FExtensions.sampleFileDownload(FExcelParserType.PHARMA)
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(ContentsType.type_xlsx))
			.contentLength(ret.file.length())
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"excel_upload_sample.xlsx\"")
			.body(ret)
	}
	@Operation(summary = "제약사 수정")
	@PutMapping(value = ["/modify"])
	fun putPharmaDataModify(@RequestHeader token: String,
													@RequestBody pharmaData: PharmaModel) =
		responseService.getResult(pharmaService.pharmaDataModify(token, pharmaData))
}