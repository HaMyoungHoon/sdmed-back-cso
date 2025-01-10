package sdmed.back.controller.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.model.sqlCSO.edi.EDIUploadFileModel
import sdmed.back.service.EDIListService
import sdmed.back.service.EDIRequestService
import java.util.*

@Tag(name = "extra EDI 리스트")
@RestController
@RequestMapping(value = ["/extra/ediList"])
class ExtraEDIListController: FControllerBase() {
	@Autowired lateinit var ediRequestService: EDIRequestService

	@Operation(summary = "edi 업로드 데이터 리스트")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: Date,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: Date) =
		responseService.getResult(ediRequestService.getEDIUploadMyList(token, startDate, endDate))
	@Operation(summary = "edi 업로드 데이터")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
	            @PathVariable thisPK: String) =
		responseService.getResult(ediRequestService.getEDIUploadMyData(token, thisPK))

	@Operation(summary = "edi file upload")
	@PostMapping(value = ["/file/{thisPK}"])
	fun postEDIUpload(@RequestHeader token: String,
										@PathVariable thisPK: String,
										@RequestBody ediUploadFileModel: List<EDIUploadFileModel>) =
		responseService.getResult(ediRequestService.postEDIFileUpload(token, thisPK, ediUploadFileModel))

	@Operation(summary = "edi file 삭제")
	@DeleteMapping(value = ["/data/file/{thisPK}"])
	fun deleteEDIFile(@RequestHeader token: String,
	                  @PathVariable thisPK: String) =
		responseService.getResult(ediRequestService.deleteEDIFile(token, thisPK))
}