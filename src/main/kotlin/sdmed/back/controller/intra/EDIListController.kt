package sdmed.back.controller.intra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.model.sqlCSO.edi.*
import sdmed.back.model.sqlCSO.hospital.HospitalTempModel
import sdmed.back.service.intra.IntraEDIListService
import java.util.Date

@Tag(name = "intra EDI 리스트")
@RestController
@RequestMapping(value = ["/intra/ediList"])
class EDIListController: FControllerBase() {
	@Autowired lateinit var intraEDIListService: IntraEDIListService

	@Operation(summary = "edi 업로드 데이터 리스트")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String,
				@RequestParam(required = false) myChild: Boolean = true,
	            @RequestParam(required = false) withFile: Boolean = false,
				@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: Date,
				@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: Date) = if (myChild) {
					responseService.getResult(intraEDIListService.getEDIUploadListMyChild(token, withFile, startDate, endDate))
				} else {
					responseService.getResult(intraEDIListService.getEDIUploadList(token, withFile, startDate, endDate))
				}
	@Operation(summary = "edi 업로드 데이터")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
							@PathVariable thisPK: String) =
		responseService.getResult(intraEDIListService.getEDIUploadData(token, thisPK))

	@Operation(summary = "edi 업로드 응답")
	@PostMapping(value = ["/data/{ediPharmaPK}"])
	fun postData(@RequestHeader token: String,
							 @PathVariable ediPharmaPK: String,
							 @RequestBody ediUploadResponseModel: EDIUploadResponseModel) =
		responseService.getResult(intraEDIListService.postEDIResponseData(token, ediPharmaPK, ediUploadResponseModel))
	@Operation(summary = "edi 업로드 신규처 응답")
	@PostMapping(value = ["/data/new-edi/{ediPK}"])
	fun postEDINewData(@RequestHeader token: String,
	                   @PathVariable ediPK: String,
	                   @RequestBody ediUploadResponseModel: EDIUploadResponseModel) =
		responseService.getResult(intraEDIListService.postEDINewResponseData(token, ediPK, ediUploadResponseModel))

	@Operation(summary = "edi 데이터 수정")
	@PutMapping(value = ["/data/{thisPK}"])
	fun putData(@RequestHeader token: String,
							@PathVariable thisPK: String,
							@RequestBody ediUploadModel: EDIUploadModel) =
		responseService.getResult(intraEDIListService.putEDIUpload(token, thisPK, ediUploadModel))
	@Operation(summary = "edi 신규처 데이터 수정")
	@PutMapping(value = ["/data/hospitalTemp/{thisPK}"])
	fun putData(@RequestHeader token: String,
	            @PathVariable thisPK: String,
	            @RequestBody hospitalTempModel: HospitalTempModel) =
		responseService.getResult(intraEDIListService.putEDIUpload(token, thisPK, hospitalTempModel))

	@Operation(summary = "edi 데이터-제약사 수정")
	@PutMapping(value = ["/data/pharma/{thisPK}"])
	fun putPharmaData(@RequestHeader token: String,
										@PathVariable thisPK: String,
										@RequestBody ediUploadPharmaModel: EDIUploadPharmaModel) =
		responseService.getResult(intraEDIListService.putEDIPharma(token,  thisPK, ediUploadPharmaModel))

	@Operation(summary = "edi 데이터-제약사 상태 수정")
	@PutMapping(value = ["/data/pharma/{thisPK}/state"])
	fun putEDIPharmaState(@RequestHeader token: String,
	                  @PathVariable thisPK: String,
	                  @RequestBody ediUploadPharmaModel: EDIUploadPharmaModel) =
		responseService.getResult(intraEDIListService.putEDIPharmaState(token,  thisPK, ediUploadPharmaModel))

	@Operation(summary = "edi 데이터-제약사-약품 수정")
	@PutMapping(value = ["/data/pharma/medicine/{thisPK}"])
	fun putPharmaMedicineData(@RequestHeader token: String,
														@PathVariable thisPK: String,
														@RequestBody ediUploadPharmaMedicineModel: EDIUploadPharmaMedicineModel) =
		responseService.getResult(intraEDIListService.putEDIMedicine(token, thisPK, ediUploadPharmaMedicineModel))

	@Operation(summary = "edi 데이터-제약사-약품 삭제")
	@DeleteMapping(value = ["/data/pharma/medicine/{thisPK}"])
	fun deletePharmaMedicineData(@RequestHeader token: String,
															 @PathVariable thisPK: String) =
		responseService.getResult(intraEDIListService.deleteEDIMedicine(token, thisPK))

	@Operation(summary = "edi 데이터-제약사-약품 리스트 삭제")
	@DeleteMapping(value = ["/list/pharma/medicine"])
	fun deletePharmaMedicineList(@RequestHeader token: String,
															 @RequestParam thisPK: List<String>) =
		responseService.getResult(intraEDIListService.deleteEDIMedicine(token, thisPK))

	@Operation(summary = "edi pharma file 삭제")
	@DeleteMapping(value = ["/data/pharma/file/{thisPK}"])
	fun deleteEDIPharmaFile(@RequestHeader token: String,
							@PathVariable thisPK: String) =
		responseService.getResult(intraEDIListService.deleteEDIPharmaFile(token, thisPK))
}