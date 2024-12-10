package sdmed.back.controller.intra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FConstants
import sdmed.back.model.sqlCSO.HosPharmaMedicinePairModel
import sdmed.back.service.HospitalService
import sdmed.back.service.PharmaService
import sdmed.back.service.ResponseService
import sdmed.back.service.UserService

@Tag(name = "유저매핑")
@RestController
@RequestMapping(value = ["/intra/userMapping"])
@CrossOrigin(origins = [FConstants.HTTP_MHHA, FConstants.HTTPS_MHHA], allowedHeaders = ["*"])
class UserMappingController {
	@Autowired lateinit var responseService: ResponseService
	@Autowired lateinit var userService: UserService
	@Autowired lateinit var hospitalService: HospitalService
	@Autowired lateinit var pharmaService: PharmaService

	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/all/business"])
	fun getUserAllBusiness(@RequestHeader token: String) =
		responseService.getResult(userService.getAllUserBusiness(token))
	@Operation(summary = "유저 누르면 보이는 거")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
	            @PathVariable thisPK: String,
	            @RequestParam(required = false) childView: Boolean = false,
	            @RequestParam(required = false) relationView: Boolean = false,
	            @RequestParam(required = false) pharmaOwnMedicineView: Boolean = false) =
		responseService.getResult(userService.getUserData(token, thisPK, childView, relationView, pharmaOwnMedicineView))
	@Operation(summary = "병원 조회 like code, innerName, orgName")
	@GetMapping(value = ["/all/hospitalSearch"])
	fun getHospitalAllSearch(@RequestHeader token: String,
	                         @RequestParam searchString: String,
	                         @RequestParam(required = false) isSearchTypeCode: Boolean = false) =
		responseService.getResult(hospitalService.getHospitalAllSearch(token, searchString, isSearchTypeCode))
	@Operation(summary = "제약사 조회 like code, innerName, orgName")
	@GetMapping(value = ["/all/pharmaSearch"])
	fun getPharmaAllSearch(@RequestHeader token: String,
	                       @RequestParam searchString: String,
	                       @RequestParam(required = false) isSearchTypeCode: Boolean = false) =
		responseService.getResult(pharmaService.getPharmaAllSearch(token, searchString, isSearchTypeCode))
	@Operation(summary = "제약사 조회")
	@GetMapping(value = ["/{pharmaPK}"])
	fun getPharmaData(@RequestHeader token: String,
	                  @PathVariable("pharmaPK") pharmaPK: String,
	                  @RequestParam(required = false) pharmaOwnMedicineView: Boolean = false) =
		responseService.getResult(pharmaService.getPharmaData(token, pharmaPK, pharmaOwnMedicineView))

	@Operation(summary = "유저-병원-제약사-약품 관계 변경")
	@PutMapping(value = ["/userRelModify/pk"])
	fun putUserRelationModifyByPK(@RequestHeader token: String,
	                              @RequestParam userPK: String,
	                              @RequestBody hosPharmaMedicinePairModel: List<HosPharmaMedicinePairModel>) =
		responseService.getResult(userService.userRelationModify(token, userPK, hosPharmaMedicinePairModel))
}