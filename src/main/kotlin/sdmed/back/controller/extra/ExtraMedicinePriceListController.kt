package sdmed.back.controller.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.service.MedicinePriceListService

@Tag(name = "extra 약제급여목록표")
@RestController
@RequestMapping(value = ["/extra/medicinePriceList"])
class ExtraMedicinePriceListController: FControllerBase() {
	@Autowired lateinit var medicinePriceService: MedicinePriceListService

	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(medicinePriceService.getList(token))
	@Operation(summary = "가격 검색")
	@GetMapping(value = ["/like"])
	fun getLike(@RequestHeader token: String,
							@RequestParam searchString: String) =
		responseService.getResult(medicinePriceService.getLike(token, searchString))
	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list/paging"])
	fun getPagingList(@RequestHeader token: String,
	                  @RequestParam(required = false) page: Int = 0,
	                  @RequestParam(required = false) size: Int = 100) =
		responseService.getResult(medicinePriceService.getPagingList(token, page, size))
	@Operation(summary = "가격 검색")
	@GetMapping(value = ["/like/paging"])
	fun getPagingLike(@RequestHeader token: String,
										@RequestParam searchString: String,
										@RequestParam(required = false) page: Int = 0,
										@RequestParam(required = false) size: Int = 100) =
		responseService.getResult(medicinePriceService.getPagingLike(token, searchString, page, size))
	@Operation(summary = "가격 변경 이력")
	@GetMapping(value = ["/list/data/{kdCode}"])
	fun getHistoryList(@RequestHeader token: String,
	                   @PathVariable kdCode: String) =
		responseService.getResult(medicinePriceService.getMedicinePriceList(token, kdCode))
}