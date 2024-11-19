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
import sdmed.back.config.ContentsType
import sdmed.back.config.FConstants
import sdmed.back.config.FExcelParserType
import sdmed.back.config.FExtensions
import sdmed.back.service.PharmaceuticalService
import sdmed.back.service.ResponseService

@Tag(name = "PharmaController")
@RestController
@RequestMapping(value = ["/v1/pharma"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class PharmaController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var pharmaService: PharmaceuticalService

	@Operation(summary = "제약사 정보 조회")
	@GetMapping(value = ["/all"])
	fun getPharmaceutical(@RequestHeader(required = true) token: String) =
		responseService.getResult(pharmaService.getAllPharma(token))
	@Operation(summary = "제약사 정보 조회")
	@GetMapping(value = ["/all/{page}/{size}"])
	fun getPharmaceuticalAllPage(@RequestHeader(required = true) token: String,
	                       @PathVariable("page") page: Int,
	                       @PathVariable("size") size: Int) =
		responseService.getResult(pharmaService.getPagePharma(token, page, size))

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