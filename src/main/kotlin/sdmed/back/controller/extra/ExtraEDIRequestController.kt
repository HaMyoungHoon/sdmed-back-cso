package sdmed.back.controller.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.model.sqlCSO.edi.EDIUploadModel
import sdmed.back.service.EDIRequestService

@Tag(name = "EDI 요청")
@RestController
@RequestMapping(value = ["/extra/ediRequest"])
class ExtraEDIRequestController: FControllerBase() {
	@Autowired lateinit var ediRequestService: EDIRequestService

	@Operation(summary = "적용일자 목록")
	@GetMapping(value = ["/list/applyDate"])
	fun getApplyDateList(@RequestHeader token: String) =
		responseService.getResult(ediRequestService.getApplyDateList(token))

	@Operation(summary = "내가 가진 병원 목록")
	@GetMapping(value = ["/list/hospital"])
	fun getHospitalList(@RequestHeader token: String) =
		responseService.getResult(ediRequestService.getHospitalList(token))

	@Operation(summary = "내가 가진 병원의 제약사 목록")
	@GetMapping(value = ["/list/pharma/{hosPK}"])
	fun getPharmaList(@RequestHeader token: String,
										@PathVariable hosPK: String) =
		responseService.getResult(ediRequestService.getPharmaList(token, hosPK))

	@Operation(summary = "내가 가진 병원의 제약사의 약품 목록")
	@GetMapping(value = ["/list/medicine/{hosPK}"])
	fun getMedicineList(@RequestHeader token: String,
	                    @PathVariable hosPK: String,
											@RequestParam pharmaPK: List<String>) =
		responseService.getResult(ediRequestService.getMedicineList(token, hosPK, pharmaPK))

	@Operation(summary = "edi 업로드")
	@PostMapping(value = ["/data"])
	fun postData(@RequestHeader token: String,
							 @RequestBody ediUploadModel: EDIUploadModel) =
		responseService.getResult(ediRequestService.postEDIData(token, ediUploadModel))
}