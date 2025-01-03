package sdmed.back.controller.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.service.ExtraDashboardService
import java.util.Date

@Tag(name = "대시보드")
@RestController
@RequestMapping(value = ["/extra/dashboard"])
class ExtraDashboardController: FControllerBase() {
	@Autowired lateinit var dashboardService: ExtraDashboardService

	@Operation(summary = "이번 달에 얼마 벌었나")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String,
							@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date) =
		responseService.getResult(dashboardService.getMoneyList(token, date))

	@Operation(summary = "이번 달에 얼마 벌었나")
	@GetMapping(value = ["/list/hos"])
	fun getListHos(@RequestHeader token: String,
	               @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date) =
		responseService.getResult(dashboardService.getMoneyHosList(token, date))

	@Operation(summary = "이번 달에 얼마 벌었나")
	@GetMapping(value = ["/list/pharma"])
	fun getListPharma(@RequestHeader token: String,
	                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date) =
		responseService.getResult(dashboardService.getMoneyPharmaList(token, date))

	@Operation(summary = "이번 달에 얼마 벌었나")
	@GetMapping(value = ["/list/medicine"])
	fun getListMedicine(@RequestHeader token: String,
	                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date) =
		responseService.getResult(dashboardService.getMoneyMedicineList(token, date))
}