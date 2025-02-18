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
import sdmed.back.config.ContentsType
import sdmed.back.config.FControllerBase
import sdmed.back.config.FExcelParserType
import sdmed.back.config.FExtensions
import sdmed.back.service.EDIDueDateService
import java.util.Date

@Tag(name = "intra EDI 마감일")
@RestController
@RequestMapping(value = ["/intra/ediDueDate"])
class EDIDueDateController: FControllerBase() {
	@Autowired lateinit var ediDueDateService: EDIDueDateService

	@Operation(summary = "페이지 처음 켜면 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String,
							@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date,
							@RequestParam(required = false) isYear: Boolean = false) =
		responseService.getResult(ediDueDateService.getEDIDueDateList(token, date, isYear))

	@Operation(summary = "")
	@GetMapping(value = ["/list/range"])
	fun getListRange(@RequestHeader token: String,
									 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: Date,
									 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: Date) =
		responseService.getResult(ediDueDateService.getEDIDueDateRangeList(token, startDate, endDate))

	@Operation(summary = "특정 제약사 마감일")
	@GetMapping(value = ["/data/{pharmaPK}/{year}"])
	fun getData(@RequestHeader token: String,
							@PathVariable pharmaPK: String,
							@PathVariable year: String) =
		responseService.getResult(ediDueDateService.getEDIPharmaDueDateList(token, pharmaPK, year))

	@Operation(summary = "특정 제약사들 마감일")
	@GetMapping(value = ["/list/pharma"])
	fun getListPharma(@RequestHeader token: String,
	                  @RequestParam pharmaPK: List<String>,
										@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date) =
		responseService.getResult(ediDueDateService.getEDIPharmaDueDateList(token, pharmaPK, date))

	@Operation(summary = "이번 달 등록 가능 제약사 목록")
	@GetMapping(value = ["/list/pharma/date"])
	fun getListPharmaAble(@RequestHeader token: String,
												@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date) =
		responseService.getResult(ediDueDateService.getEDIPharmaAble(token, date))
	@Operation(summary = "이번 달 등록 가능 제약사 목록")
	@GetMapping(value = ["/list/pharma/date/{year}/{month}"])
	fun getListPharmaAble(@RequestHeader token: String,
												@PathVariable year: String,
												@PathVariable month: String) =
		responseService.getResult(ediDueDateService.getEDIPharmaAble(token, year, month))

	@Operation(summary = "제약사 마감일 등록")
	@PostMapping(value = [ "/data/{pharmaPK}"])
	fun postData(@RequestHeader token: String,
							 @PathVariable pharmaPK: String,
							 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date) =
		responseService.getResult(ediDueDateService.postEDIPharmaDueDate(token, pharmaPK, date))
	@Operation(summary = "제약사 마감일 등록")
	@PostMapping(value = ["/data/{pharmaPK}/{year}/{month}/{day}"])
	fun postData(@RequestHeader token: String,
							 @PathVariable pharmaPK: String,
							 @PathVariable year: String,
							 @PathVariable month: String,
							 @PathVariable day: String) =
		responseService.getResult(ediDueDateService.postEDIPharmaDueDate(token, pharmaPK, year, month, day))
	@Operation(summary = "제약사들 마감일 등록")
	@PostMapping(value = ["/list"])
	fun postData(@RequestHeader token: String,
							 @RequestBody pharmaPK: List<String>,
							 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date) =
		responseService.getResult(ediDueDateService.postEDIPharmaDueDate(token, pharmaPK, date))
	@Operation(summary = "제약사 마감일 수정")
	@PutMapping(value = ["/data/{pharmaPK}"])
	fun putData(@RequestHeader token: String,
							@PathVariable pharmaPK: String,
							@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date) =
		responseService.getResult(ediDueDateService.putEDIPharmaDueDate(token, pharmaPK, date))
	@Operation(summary = "제약사 마감일 수정")
	@PutMapping(value = ["/data/{pharmaPK}/{year}/{month}/{day}"])
	fun putData(@RequestHeader token: String,
							@PathVariable pharmaPK: String,
							@PathVariable year: String,
							@PathVariable month: String,
							@PathVariable day: String) =
		responseService.getResult(ediDueDateService.putEDIPharmaDueDate(token, pharmaPK, year, month, day))
	@Operation(summary = "제약사 마감일 제거")
	@DeleteMapping(value = ["/data/{pharmaPK}"])
	fun deleteData(@RequestHeader token: String,
	               @PathVariable pharmaPK: String,
	               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date) =
		responseService.getResult(ediDueDateService.deleteEDIPharmaDueDate(token, pharmaPK, date))
	@Operation(summary = "제약사 마감일 제거")
	@DeleteMapping(value = ["/data/{pharmaPK}/{year}/{month}/{day}"])
	fun deleteData(@RequestHeader token: String,
	               @PathVariable pharmaPK: String,
	               @PathVariable year: String,
	               @PathVariable month: String,
	               @PathVariable day: String) =
		responseService.getResult(ediDueDateService.deleteEDIPharmaDueDate(token, pharmaPK, year, month))
	@Operation(summary = "마감일 캘린더 엑셀 샘플 다운로드")
	@GetMapping(value = ["/file/sample"])
	fun getExcelSample(): ResponseEntity<Resource> =
		FExtensions.sampleFileDownload(FExcelParserType.EDI_DUE_DATE).let { x ->
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
		responseService.getResult(ediDueDateService.ediDueDateUpload(token, file))
}