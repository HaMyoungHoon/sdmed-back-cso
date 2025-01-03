package sdmed.back.controller.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.service.MedicinePriceListService

@Tag(name = "약제급여목록표")
@RestController
@RequestMapping(value = ["/extra/medicinePriceList"])
class ExtraMedicinePriceListController: FControllerBase() {
	@Autowired lateinit var medicinePriceService: MedicinePriceListService

	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(medicinePriceService.getList(token))
	@Operation(summary = "가격 변경 이력")
	@GetMapping(value = ["/list/{kdCode}"])
	fun getHistoryList(@RequestHeader token: String,
	                   @PathVariable kdCode: String) =
		responseService.getResult(medicinePriceService.getMedicinePriceList(token, kdCode))
}