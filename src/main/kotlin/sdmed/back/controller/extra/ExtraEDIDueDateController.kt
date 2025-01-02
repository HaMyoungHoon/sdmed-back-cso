package sdmed.back.controller.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.service.EDIDueDateService
import java.util.*

@Tag(name = "EDI 마감일")
@RestController
@RequestMapping(value = ["/extra/ediDueDate"])
class ExtraEDIDueDateController: FControllerBase() {
	@Autowired lateinit var ediDueDateService: EDIDueDateService

	@Operation(summary = "페이지 처음 켜면 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date,
	            @RequestParam(required = false) isYear: Boolean = false) =
		responseService.getResult(ediDueDateService.getEDIDueDateMyList(token, date, isYear))

	@Operation(summary = "")
	@GetMapping(value = ["/list/range"])
	fun getListRange(@RequestHeader token: String,
	                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: Date,
	                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: Date) =
		responseService.getResult(ediDueDateService.getEDIDueDateRangeMyList(token, startDate, endDate))
}