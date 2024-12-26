package sdmed.back.controller.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.model.sqlCSO.qna.QnAContentModel
import sdmed.back.model.sqlCSO.qna.QnAReplyModel
import sdmed.back.service.QnAListService

@Tag(name = "QnA 목록")
@RestController
@RequestMapping(value = ["/extra/qnaList"])
class ExtraQnAListController: FControllerBase() {
	@Autowired lateinit var qnaListService: QnAListService

	@Operation(summary = "처음 페이지 켜면 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(qnaListService.getMyList(token))

	@Operation(summary = "qna 상세")
	@GetMapping(value = ["/data/{thisPK}"])
	fun getData(@RequestHeader token: String,
							@PathVariable thisPK: String) =
		responseService.getResult(qnaListService.getContentData(token, thisPK))

	@Operation(summary = "qna 남기기")
	@PostMapping(value = ["/data"])
	fun postData(@RequestHeader token: String,
							 @RequestParam title: String,
							 @RequestBody qnaContentModel: QnAContentModel) =
		responseService.getResult(qnaListService.postQnA(token, title, qnaContentModel))

	@Operation(summary = "qna 답변에 답변하기")
	@PostMapping(value = ["/data/{thisPK}"])
	fun postReply(@RequestHeader token: String,
								@PathVariable thisPK: String,
								@RequestBody qnaReplyModel: QnAReplyModel) =
		responseService.getResult(qnaListService.postQnAReplyQuestion(token, thisPK, qnaReplyModel))

	@Operation(summary = "qna 종료하기")
	@PostMapping(value = ["/data/{thisPK}"])
	fun putData(@RequestHeader token: String,
							@PathVariable thisPK: String) =
		responseService.getResult(qnaListService.putQnAComplete(token, thisPK))
}