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
import sdmed.back.service.HospitalService
import sdmed.back.service.ResponseService

@Tag(name = "HospitalController")
@RestController
@RequestMapping(value = ["/v1/hospital"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class HospitalController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var hospitalService: HospitalService

	@Operation(summary = "병원 정보 조회")
	@GetMapping(value = ["/all"])
	fun getHospitalAll(@RequestHeader(required = true) token: String) =
		responseService.getResult(hospitalService.getAllHospital(token))
	@Operation(summary = "병원 정보 조회")
	@GetMapping(value = ["/all/{page}/{size}"])
	fun getHospitalAllPage(@RequestHeader(required = true) token: String,
												 @PathVariable("page") page: Int,
												 @PathVariable("size") size: Int) =
		responseService.getResult(hospitalService.getPageHospital(token, page, size))

	@Operation(summary = "병원 데이터 엑셀 업로드")
	@PostMapping(value = ["/dataUploadExcel"], consumes = ["multipart/form-data"])
	fun postDataUploadExcel(@RequestHeader(required = true) token: String,
	                        @RequestParam(required = true) file: MultipartFile) =
		responseService.getResult(hospitalService.hospitalUpload(token, file))

	@Operation(summary = "병원 데이터 엑셀 샘플 다운로드")
	@GetMapping(value = ["/sampleDownloadExcel"])
	fun getSampleDownloadExcel(): ResponseEntity<Resource> {
		val ret = FExtensions.sampleFileDownload(FExcelParserType.HOSPITAL)
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(ContentsType.type_xlsx))
			.contentLength(ret.file.length())
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"excel_upload_sample.xlsx\"")
			.body(ret)
	}
}