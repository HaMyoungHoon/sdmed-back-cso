package sdmed.back.controller.intra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.model.sqlCSO.edi.EDIApplyDateState
import sdmed.back.service.EDIApplyDateService
import java.util.*

@Tag(name = "EDI 등록 적용일")
@RestController
@RequestMapping(value = ["/intra/ediApplyDate"])
class EDIApplyDateController: FControllerBase() {
	@Autowired lateinit var ediApplyDateService: EDIApplyDateService

	@Operation(summary = "edi 업로드 할 때 적용일자 선택하는 그거 전체 리스트")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(ediApplyDateService.getAllApplyDate(token))
	@Operation(summary = "edi 업로드 할 때 적용일자 일반 유저가 선택 할 때 보이는 그 리스트")
	@GetMapping(value = ["/list/use"])
	fun getUseApplyDate() =
		responseService.getResult(ediApplyDateService.getUseApplyDate())
	@Operation(summary = "edi 업로드 할 때 필요한 적용일자 추가")
	@PostMapping(value = ["/data"])
	fun postData(@RequestHeader token: String,
	             @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) applyDate: Date) =
		responseService.getResult(ediApplyDateService.postApplyDate(token, applyDate))
	@Operation(summary = "edi 업로드 할 때 필요한 적용일자 쓸지말지 수정")
	@PutMapping(value = ["/data/{thisPK}"])
	fun putData(@RequestHeader token: String,
	            @PathVariable thisPK: String,
	            @RequestParam ediApplyDateState: EDIApplyDateState) =
		responseService.getResult(ediApplyDateService.putApplyDateModify(token, thisPK, ediApplyDateState))
}