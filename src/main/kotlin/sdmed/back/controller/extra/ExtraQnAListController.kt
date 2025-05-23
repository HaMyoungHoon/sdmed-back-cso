package sdmed.back.controller.extra

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import sdmed.back.config.FControllerBase
import sdmed.back.model.sqlCSO.qna.QnAContentModel
import sdmed.back.model.sqlCSO.qna.QnAReplyModel
import sdmed.back.service.QnAListService

@Tag(name = "extra QnA 목록")
@RestController
@RequestMapping(value = ["/extra/qnaList"])
class ExtraQnAListController: FControllerBase() {
	@Autowired lateinit var qnaListService: QnAListService

	@Operation(summary = "처음 페이지 켜면 보이는 거")
	@GetMapping(value = ["/list"])
	fun getList(@RequestHeader token: String) =
		responseService.getResult(qnaListService.getMyList(token))
	@Operation(summary = "타이틀 검색")
	@GetMapping(value = ["/like"])
	fun getLike(@RequestHeader token: String,
	            @RequestParam searchString: String) =
		responseService.getResult(qnaListService.getMyLike(token, searchString))
	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/list/paging"])
	fun getPagingList(@RequestHeader token: String,
	                  @RequestParam(required = false) page: Int = 0,
	                  @RequestParam(required = false) size: Int = 100) =
		responseService.getResult(qnaListService.getMyPagingList(token, page, size))
	@Operation(summary = "페이지 켜면 처음 보이는 거")
	@GetMapping(value = ["/like/paging"])
	fun getPagingLike(@RequestHeader token: String,
	                  @RequestParam searchString: String,
	                  @RequestParam(required = false) page: Int = 0,
	                  @RequestParam(required = false) size: Int = 100) =
		responseService.getResult(qnaListService.getMyPagingLike(token, searchString, page, size))

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
	@PutMapping(value = ["/data/{thisPK}"])
	fun putData(@RequestHeader token: String,
							@PathVariable thisPK: String) =
		responseService.getResult(qnaListService.putQnAComplete(token, thisPK))
}