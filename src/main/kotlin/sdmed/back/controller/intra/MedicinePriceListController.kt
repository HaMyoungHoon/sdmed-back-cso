package sdmed.back.controller.intra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import sdmed.back.config.FControllerBase
import sdmed.back.service.MedicinePriceListService
import java.util.*

@Tag(name = "약제급여목록표")
@RestController
@RequestMapping(value = ["/intra/medicinePriceList"])
class MedicinePriceListController: FControllerBase() {
	@Autowired lateinit var medicinePriceService: MedicinePriceListService

	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(medicinePriceService.getList(token))
	@Operation(summary = "가격 변경 이력")
	@GetMapping(value = ["/list/price"])
	fun getHistoryList(@RequestHeader token: String,
										 @RequestParam kdCode: Int) =
		responseService.getResult(medicinePriceService.getMedicinePriceList(token, kdCode))
	@Operation(summary = "약가 적용 날짜")
	@GetMapping(value = ["/data/price/date"])
	fun getMedicinePriceApplyDate(@RequestHeader token: String) =
		responseService.getResult(medicinePriceService.getPriceApplyDate(token))

	@Operation(summary = "약제급여목록및급여상한금액표 업로드")
	@PostMapping(value = ["/file/priceExcel"], consumes = ["multipart/form-data"])
	fun postMedicinePriceUpload(@RequestHeader token: String,
	                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) applyDate: Date,
	                            @RequestParam file: MultipartFile) =
		responseService.getResult(medicinePriceService.medicinePriceUpload(token, applyDate, file))
	@Operation(summary = "약제급여목록및급여상한금액표 주성분 업로드")
	@PostMapping(value = ["/file/ingredientExcel"], consumes = ["multipart/form-data"])
	fun postMedicineIngredientUpload(@RequestHeader token: String,
																	 @RequestParam file: MultipartFile) =
		responseService.getResult(medicinePriceService.medicineIngredientUpload(token, file))
}