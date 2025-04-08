package sdmed.back.controller.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.model.sqlCSO.edi.EDIUploadModel
import sdmed.back.service.extra.ExtraEDIRequestService
import java.util.Date

@Tag(name = "extra EDI 요청")
@RestController
@RequestMapping(value = ["/extra/ediRequest"])
class ExtraEDIRequestController: FControllerBase() {
	@Autowired lateinit var extraEDIRequestService: ExtraEDIRequestService

	@Operation(summary = "적용일자 목록")
	@GetMapping(value = ["/list/applyDate"])
	fun getApplyDateList(@RequestHeader token: String) =
		responseService.getResult(extraEDIRequestService.getApplyDateList(token))

	@Operation(summary = "내가 가진 병원 목록")
	@GetMapping(value = ["/list/hospital"])
	fun getHospitalList(@RequestHeader token: String,
											@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) applyDate: Date) =
		responseService.getResult(extraEDIRequestService.getHospitalList(token, applyDate))

	@Operation(summary = "내가 가능한 제약사 목록")
	@GetMapping(value = ["/list/pharma"])
	fun getPharmaList(@RequestHeader token: String,
	                  @RequestParam(required = false) withMedicine: Boolean = false) =
		responseService.getResult(extraEDIRequestService.getPharmaList(token, withMedicine))
	@Operation(summary = "내가 가진 병원의 제약사 목록")
	@GetMapping(value = ["/list/pharma/{hosPK}"])
	fun getPharmaList(@RequestHeader token: String,
										@PathVariable hosPK: String,
										@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) applyDate: Date,
										@RequestParam(required = false) withMedicine: Boolean = true) =
		responseService.getResult(extraEDIRequestService.getPharmaList(token, hosPK, applyDate, withMedicine))

	@Operation(summary = "내가 가진 병원의 제약사의 약품 목록")
	@GetMapping(value = ["/list/medicine/{hosPK}"])
	fun getMedicineList(@RequestHeader token: String,
	                    @PathVariable hosPK: String,
											@RequestParam pharmaPK: List<String>) =
		responseService.getResult(extraEDIRequestService.getMedicineList(token, hosPK, pharmaPK))

	@Operation(summary = "edi 업로드")
	@PostMapping(value = ["/data"])
	fun postData(@RequestHeader token: String,
							 @RequestBody ediUploadModel: EDIUploadModel) =
		responseService.getResult(extraEDIRequestService.postEDIData(token, ediUploadModel))

	@Operation(summary = "edi 신규처 업로드")
	@PostMapping(value = ["/data/new"])
	fun postNewData(@RequestHeader token: String,
									@RequestBody ediUploadModel: EDIUploadModel) =
		responseService.getResult(extraEDIRequestService.postNewEDIData(token, ediUploadModel))
}