package sdmed.back.controller.intra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.service.EDIDueDateService
import java.util.Date

@Tag(name = "EDI 마감일")
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

	@Operation(summary = "특정 제약사 마감일")
	@GetMapping(value = ["/data/{pharmaPK}/{year}"])
	fun getData(@RequestHeader token: String,
							@PathVariable pharmaPK: String,
							@PathVariable year: String) =
		responseService.getResult(ediDueDateService.getEDIPharmaDueDateList(token, pharmaPK, year))

	@Operation(summary = "특정 제약사들 마감일")
	@GetMapping(value = ["/list/pharma"])
	fun getListPharma(@RequestHeader token: String,
	                  @RequestBody pharmaPK: List<String>,
										@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date) =
		responseService.getResult(ediDueDateService.getEDIPharmaDueDateList(token, pharmaPK, date))

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
}