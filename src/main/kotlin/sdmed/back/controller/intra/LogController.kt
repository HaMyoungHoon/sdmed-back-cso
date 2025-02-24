package sdmed.back.controller.intra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import sdmed.back.config.FControllerBase
import sdmed.back.service.CommonService

@Tag(name = "intra log")
@RestController
@RequestMapping(value = ["/intra/log"])
class LogController: FControllerBase() {
	@Autowired lateinit var commonService: CommonService

	@Operation(summary = "get log view model")
	@GetMapping(value = ["/list/log"])
	fun getLogList(@RequestHeader token: String,
	               @RequestParam(required = false) page: Int = 0,
	               @RequestParam(required = false) size: Int = 1000) =
		responseService.getResult(commonService.getLogViewModel(token, page, size))
	@Operation(summary = "get ip log model")
	@GetMapping(value = ["/list/ipLog"])
	fun getIPLogList(@RequestHeader token: String,
	                 @RequestParam(required = false) page: Int = 0,
	                 @RequestParam(required = false) size: Int = 1000) =
		responseService.getResult(commonService.getIPLogModel(token, page, size))
}