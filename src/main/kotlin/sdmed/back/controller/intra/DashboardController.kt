package sdmed.back.controller.intra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sdmed.back.config.FControllerBase
import sdmed.back.model.sqlCSO.request.RequestModel
import sdmed.back.service.DashboardService
import java.util.*

@Tag(name = "대시보드")
@RestController
@RequestMapping(value = ["/intra/dashboard"])
class DashboardController: FControllerBase() {
	@Autowired lateinit var dashboardService: DashboardService

	@Operation(summary = "페이지 처음 켜면 보이는 거")
	@GetMapping(value = ["/list/myChild"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(dashboardService.getListByMyChildNoResponse(token))

	@Operation(summary = "페이지 처음 켜면 보이는 거")
	@GetMapping(value = ["/list/all"])
	fun getListByNoResponse(@RequestHeader token: String) =
		responseService.getResult(dashboardService.getListByNoResponse(token))

	@Operation(summary = "날짜별 검색")
	@GetMapping(value = ["/list/date"])
	fun getListByDate(@RequestHeader token: String,
	                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: Date,
	                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: Date) =
		responseService.getResult(dashboardService.getListByDate(token, startDate, endDate))

	@Operation(summary = "얘 누구지 하고 찾는 거")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getUserData(@RequestHeader token: String,
									@PathVariable thisPK: String) =
		responseService.getResult(dashboardService.getUserData(token, thisPK))

	@Operation(summary = "날짜별 응답 개수")
	@GetMapping(value = ["/list/responseType/date"])
	fun getCountOfResponseType(@RequestHeader token: String,
														 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: Date,
														 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: Date) =
		responseService.getResult(dashboardService.getCountOfResponseType(token, startDate, endDate))

	@Operation(summary = "날짜별 탑10 요청 유저")
	@GetMapping(value = ["/list/requestUser/date"])
	fun getTop10RequestUser(@RequestHeader token: String,
	                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: Date,
	                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: Date) =
		responseService.getResult(dashboardService.getTop10RequestUser(token, startDate, endDate))

	@Operation(summary = "요청 수신")
	@PutMapping(value = ["/data/{thisPK}/recep"])
	fun putRequestRecep(@RequestHeader token: String,
											@PathVariable thisPK: String) =
		responseService.getResult(dashboardService.putRequestRecep(token, thisPK))

	@Operation(summary = "요청 처리")
	@PutMapping(value = ["/data/{thisPK}"])
	fun putRequestModelResponseData(@RequestHeader token: String,
																	@RequestBody data: RequestModel) =
		responseService.getResult(dashboardService.putRequestModelResponseData(token, data))
}