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
import sdmed.back.model.common.IRestResult
import sdmed.back.model.common.UserRole
import sdmed.back.model.common.UserRoles
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
	fun getPharmaAll(@RequestHeader(required = true) token: String) =
		responseService.getResult(pharmaService.getAllPharma(token))
	@Operation(summary = "제약사 정보 조회")
	@GetMapping(value = ["/all/{page}/{size}"])
	fun getPharmaAllPage(@RequestHeader(required = true) token: String,
	                     @PathVariable("page") page: Int,
	                     @PathVariable("size") size: Int) =
		responseService.getResult(pharmaService.getPagePharma(token, page, size))

	@Operation(summary = "제약사 조회 with medicine")
	@GetMapping(value = ["/{pharmaPK}/withMedicine"])
	fun getPharmaWithMedicine(@RequestHeader(required = true) token: String,
	                          @PathVariable("pharmaPK") pharmaPK: String) =
		responseService.getResult(pharmaService.getPharmaWithDrug(token, pharmaPK))
	@Operation(summary = "제약사 조회 with medicine")
	@GetMapping(value = ["/{pharmaPK}"])
	fun getPharma(@RequestHeader(required = true) token: String,
	              @PathVariable("pharmaPK") pharmaPK: String) =
		responseService.getResult(pharmaService.getPharma(token, pharmaPK))
	@Operation(summary = "제약사 medicine list add")
	@PostMapping(value = ["/{pharmaPK}/addMedicine"])
	fun postPharmaAddMedicine(@RequestHeader(required = true) token: String,
	                          @PathVariable("pharmaPK") pharmaPK: String,
	                          @RequestBody(required = true) medicinePKList: List<String>) =
		responseService.getResult(pharmaService.addPharmaDrugList(token, pharmaPK, medicinePKList))
	@Operation(summary = "제약사 medicine list modify")
	@PutMapping(value = ["/{pharmaPK}/modMedicine"])
	fun putPharmaModMedicine(@RequestHeader(required = true) token: String,
													 @PathVariable("pharmaPK") pharmaPK: String,
													 @RequestBody(required = true) medicinePKList: List<String>) =
		responseService.getResult(pharmaService.modPharmaDrugList(token, pharmaPK, medicinePKList))

	@Operation(summary = "제약사 사업자등록증 업로드")
	@PutMapping(value = ["/{pharmaPK}/taxImageUpload"], consumes = ["multiphart/form-data"])
	fun putPharmaTaxImageUpload(@RequestHeader(required = true) token: String,
															@PathVariable("pharmaPK") pharmaPK: String,
															@RequestParam file: MultipartFile): IRestResult {
		pharmaService.isValid(token)
		val tokenUser = pharmaService.getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		if (!pharmaService.haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaFileUploader))) {
			throw AuthenticationEntryPointException()
		}

		val pharmaData = pharmaService.getPharma(token, pharmaPK) ?: throw UserNotFoundException()
		val blobUrl = azureBlobService.uploadFile(file, pharmaData.orgName, tokenUser.thisPK)
		pharmaData.imageUrl = blobUrl
		return responseService.getResult(pharmaService.pharmaDataModify(token, pharmaData))
	}

	@Operation(summary = "제약사 데이터 엑셀 업로드")
	@PostMapping(value = ["/dataUploadExcel"], consumes = ["multipart/form-data"])
	fun postDataUploadExcel(@RequestHeader(required = true) token: String,
	                        @RequestParam(required = true) file: MultipartFile) =
		responseService.getResult(pharmaService.pharmaUpload(token, file))

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
}