package sdmed.back.controller.intra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FConstants
import sdmed.back.service.MedicineService
import sdmed.back.service.ResponseService

@Tag(name = "약제급여목록표")
@RestController
@RequestMapping(value = ["/intra/medicinePriceList"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class MedicinePriceListController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var medicineService: MedicineService

	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(medicineService.getAllMedicine(token))

	@Operation(summary = "가격 변경 이력")
	@GetMapping(value = ["/list/price"])
	fun getHistoryList(@RequestHeader token: String,
										 @RequestParam kdCode: Int) =
		responseService.getResult(medicineService.getMedicinePriceList(token, kdCode))
}