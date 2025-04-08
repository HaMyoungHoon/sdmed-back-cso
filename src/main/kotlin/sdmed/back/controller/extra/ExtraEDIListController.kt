package sdmed.back.controller.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.model.sqlCSO.edi.EDIUploadFileModel
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaFileModel
import sdmed.back.service.EDIRequestService
import sdmed.back.service.extra.ExtraEDIListService
import java.util.*

@Tag(name = "extra EDI 리스트")
@RestController
@RequestMapping(value = ["/extra/ediList"])
class ExtraEDIListController: FControllerBase() {
	@Autowired lateinit var extraEDIListService: ExtraEDIListService

	@Operation(summary = "edi 업로드 데이터 리스트")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: Date,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: Date) =
		responseService.getResult(extraEDIListService.getEDIUploadList(token, startDate, endDate))
	@Operation(summary = "edi 업로드 데이터")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
	            @PathVariable thisPK: String) =
		responseService.getResult(extraEDIListService.getEDIUploadDetail(token, thisPK))
	@Operation(summary = "edi pharma file upload")
	@PostMapping(value = ["/file/{ediPK}/pharma/{ediPharmaPK}"])
	fun postEDIPharmaUpload(@RequestHeader token: String,
							@PathVariable ediPK: String,
							@PathVariable ediPharmaPK: String,
							@RequestBody ediUploadPharmaFileModel: List<EDIUploadPharmaFileModel>) =
		responseService.getResult(extraEDIListService.postEDIPharmaFileUpload(token, ediPK, ediPharmaPK, ediUploadPharmaFileModel))
	@Operation(summary = "edi pharma file 삭제")
	@DeleteMapping(value = ["/data/pharma/file/{thisPK}"])
	fun deleteEDIPharmaFile(@RequestHeader token: String,
	                        @PathVariable thisPK: String) =
		responseService.getResult(extraEDIListService.deleteEDIPharmaFile(token, thisPK))
}