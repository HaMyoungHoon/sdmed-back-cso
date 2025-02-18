package sdmed.back.controller.intra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sdmed.back.config.FControllerBase
import sdmed.back.service.EDIUploadCheckService
import java.util.*

@Tag(name = "intra EDI 체크리스트")
@RestController
@RequestMapping(value = ["/intra/ediCheck"])
class EDIUploadCheckController: FControllerBase() {
	@Autowired lateinit var ediUploadCheckService: EDIUploadCheckService

	@Operation(summary = "user's check data")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date,
	            @RequestParam(required = false) isEDIDate: Boolean = true,
	            @RequestParam(required = false) isMyChild: Boolean = true) =
		responseService.getResult(ediUploadCheckService.getList(token, date,isEDIDate, isMyChild))
	@Operation(summary = "user's check data")
	@GetMapping(value = ["/data/{userPK}"])
	fun getData(@RequestHeader token: String,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) date: Date,
							@PathVariable userPK: String,
							@RequestParam(required = false) isEDIDate: Boolean = true) =
		responseService.getResult(ediUploadCheckService.getData(token, date, userPK, isEDIDate))
	@Operation(summary = "user list")
	@GetMapping(value = ["/list/user"])
	fun getUserList(@RequestHeader token: String,
	                @RequestParam(required = false) isMyChild: Boolean = true) =
		responseService.getResult(ediUploadCheckService.getUserList(token, isMyChild))
}