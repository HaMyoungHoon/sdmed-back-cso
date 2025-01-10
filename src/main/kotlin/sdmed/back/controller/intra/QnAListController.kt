package sdmed.back.controller.intra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.model.sqlCSO.qna.QnAReplyModel
import sdmed.back.service.QnAListService
import java.util.Date

@Tag(name = "intra QnA 목록")
@RestController
@RequestMapping(value = ["/intra/qnaList"])
class QnAListController: FControllerBase() {
	@Autowired lateinit var qnaListService: QnAListService

	@Operation(summary = "페이지 처음 켜면 보이는 거")
	@GetMapping(value = ["/list/myChild"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(qnaListService.getMyChildList(token))

	@Operation(summary = "페이지 처음 켜면 보이는 거")
	@GetMapping(value = ["/list/all"])
	fun getListByNoResponse(@RequestHeader token: String) =
		responseService.getResult(qnaListService.getHaveToReplyList(token))

	@Operation(summary = "날짜별 검색")
	@GetMapping(value = ["/list/date"])
	fun getListByDate(@RequestHeader token: String,
										@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: Date,
										@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: Date) =
		responseService.getResult(qnaListService.getListDate(token, startDate, endDate))

	@Operation(summary = "qna 헤더")
	@GetMapping(value = ["/data/header/{thisPK}"])
	fun getHeaderData(@RequestHeader token: String,
	                  @PathVariable thisPK: String) =
		responseService.getResult(qnaListService.getHeaderData(token, thisPK))

	@Operation(summary = "qna 상세")
	@GetMapping(value = ["/data/content/{thisPK}"])
	fun getContentData(@RequestHeader token: String,
	                   @PathVariable thisPK: String) =
		responseService.getResult(qnaListService.getContentData(token, thisPK))

	@Operation(summary = "답변 넣기")
	@PostMapping(value = ["/data/{thisPK}"])
	fun postReply(@RequestHeader token: String,
								@PathVariable thisPK: String,
								@RequestBody qnaReplyModel: QnAReplyModel) =
		responseService.getResult(qnaListService.postQnAReplyAnswer(token, thisPK, qnaReplyModel))

	@Operation(summary = "qna 종료하기")
	@PutMapping(value = ["/data/{thisPK}"])
	fun putData(@RequestHeader token: String,
	            @PathVariable thisPK: String) =
		responseService.getResult(qnaListService.putQnAComplete(token, thisPK))
}