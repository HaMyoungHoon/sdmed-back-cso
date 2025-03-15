package sdmed.back.controller.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.service.HospitalTempService

@Tag(name = "extra 임시병원")
@RestController
@RequestMapping(value = ["/extra/hospitalTemp"])
class ExtraHospitalTempController: FControllerBase() {
	@Autowired lateinit var hospitalTempService: HospitalTempService

	@Operation(summary = "임시병원 자세히")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
	            @PathVariable thisPK: String) =
		responseService.getResult(hospitalTempService.getHospitalDetail(token, thisPK))
	@Operation(summary = "임시병원 찾기")
	@GetMapping(value = ["/list/search"])
	fun getListSearch(@RequestHeader token: String,
	                  @RequestParam searchString: String) =
		responseService.getResult(hospitalTempService.getHospitalContains(token, searchString))
	@Operation(summary = "가까운 병원 찾기")
	@GetMapping(value = ["/list/nearby"])
	fun getListNearBy(@RequestHeader token: String,
	                  @RequestParam latitude: Double,
	                  @RequestParam longitude: Double,
	                  @RequestParam(required = false) distance: Int = 1) =
		responseService.getResult(hospitalTempService.getNearbyHospital(token, latitude, longitude, distance))
	@Operation(summary = "가까운 약국 찾기")
	@GetMapping(value = ["/list/nearby/pharmacy"])
	fun getPharmacyListNearBy(@RequestHeader token: String,
							  @RequestParam latitude: Double,
							  @RequestParam longitude: Double,
							  @RequestParam(required = false) distance: Int = 1000) =
		responseService.getResult(hospitalTempService.getNearbyPharmacy(token, latitude, longitude, distance))
}