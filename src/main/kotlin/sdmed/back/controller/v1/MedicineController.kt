package sdmed.back.controller.v1

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import sdmed.back.config.ContentsType
import sdmed.back.config.FConstants
import sdmed.back.config.FExcelParserType
import sdmed.back.config.FExtensions
import sdmed.back.service.MedicineService
import sdmed.back.service.ResponseService
import java.util.Date

@Tag(name = "MedicineController")
@RestController
@RequestMapping(value = ["/v1/medicine"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class MedicineController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var medicineService: MedicineService

	@Operation(summary = "약제급여목록")
	@GetMapping(value = ["/all"])
	fun getMedicineAll(@RequestHeader token: String,
										 @RequestParam(required = false) withPrice: Boolean = false) =
		responseService.getResult(medicineService.getMedicine(token, withPrice))
	@Operation(summary = "약품 검색")
	@GetMapping(value = ["/all/search"])
	fun getMedicineAllSearch(@RequestHeader token: String,
													 @RequestParam searchString: String,
													 @RequestParam(required = false) isSearchTypeCode: Boolean = false) =
		responseService.getResult(medicineService.getMedicineSearch(token, searchString, isSearchTypeCode))

	@Operation(summary = "약품 목록 업로드")
	@PostMapping(value = ["/dataUploadExcel"], consumes = ["multipart/form-data"])
	fun postDataUploadExcel(@RequestHeader token: String,
													@RequestParam file: MultipartFile) =
		responseService.getResult(medicineService.medicineUpload(token, file))
	@Operation(summary = "약제급여목록및급여상한금액표 업로드")
	@PostMapping(value = ["/priceDataUploadExcel"], consumes = ["multipart/form-data"])
	fun postPriceDataUploadExcel(@RequestHeader token: String,
	                             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) applyDate: Date,
	                             @RequestParam file: MultipartFile) =
		responseService.getResult(medicineService.medicinePriceUpload(token, applyDate, file))
	@Operation(summary = "약제급여목록및급여상한금액표 주성분 업로드")
	@PostMapping(value = ["/ingredientDataUploadExcel"], consumes = ["multipart/form-data"])
	fun postIngredientDataUploadExcel(@RequestHeader token: String,
	                                  @RequestParam file: MultipartFile) =
		responseService.getResult(medicineService.medicineIngredientUpload(token, file))

	@Operation(summary = "약품 목록 엑셀 샘플 다운로드")
	@GetMapping(value = ["/sampleDownloadExcel"])
	fun getSampleDownloadExcel(): ResponseEntity<Resource> {
		val ret = FExtensions.sampleFileDownload(FExcelParserType.MEDICINE)
		return ResponseEntity.ok()
			.contentType(MediaType.parseMediaType(ContentsType.type_xlsx))
			.contentLength(ret.file.length())
			.header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=\"excel_upload_sample.xlsx\"")
			.body(ret)
	}
}