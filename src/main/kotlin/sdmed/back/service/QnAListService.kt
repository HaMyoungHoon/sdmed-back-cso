package sdmed.back.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.advice.exception.QnAContentNotExistException
import sdmed.back.advice.exception.QnAHeaderNotExistException
import sdmed.back.config.FExtensions
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.RequestType
import sdmed.back.model.common.ResponseType
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.qna.QnAContentModel
import sdmed.back.model.sqlCSO.qna.QnAHeaderModel
import sdmed.back.model.sqlCSO.qna.QnAReplyModel
import sdmed.back.model.sqlCSO.qna.QnAState
import sdmed.back.model.sqlCSO.request.RequestModel
import java.util.*

class QnAListService: QnAService() {
	fun getMyList(token: String): List<QnAHeaderModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		return qnaHeaderRepository.findAllByUserPKOrderByRegDateDesc(tokenUser.thisPK)
	}
	fun getMyLike(token: String, searchString: String): List<QnAHeaderModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		return qnaHeaderRepository.selectAllLIKEUserPkOrderByRegDateDesc(tokenUser.thisPK, searchString)
	}
	fun getMyPagingList(token: String, page: Int = 0, size: Int = 100): Page<QnAHeaderModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		val pageable = PageRequest.of(page, size)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		return qnaHeaderRepository.findAllByUserPKOrderByRegDateDesc(tokenUser.thisPK, pageable)
	}
	fun getMyPagingLike(token: String, searchString: String, page: Int = 0, size: Int = 100): Page<QnAHeaderModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		val pageable = PageRequest.of(page, size)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		return qnaHeaderRepository.selectAllLIKEUserPkOrderByRegDateDesc(tokenUser.thisPK, searchString, pageable)
	}
	fun getHaveToReplyList(token: String): List<QnAHeaderModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		return qnaHeaderRepository.selectAllHaveToReply()
	}
	fun getMyChildList(token: String): List<QnAHeaderModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val childPK = userChildPKRepository.selectAllByMotherPK(tokenUser.thisPK)
		return qnaHeaderRepository.findAllByUserPKInOrderByRegDateDesc(childPK)
	}
	fun getListDate(token: String, startDate: Date, endDate: Date): List<QnAHeaderModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}
		val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)

		return qnaHeaderRepository.selectAllByDate(queryDate.first, queryDate.second)
	}

	fun getHeaderData(token: String, thisPK: String): QnAHeaderModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		return if (haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			qnaHeaderRepository.findByThisPKOrderByRegDateDesc(thisPK) ?: throw QnAHeaderNotExistException()
		} else if (haveRole(tokenUser, UserRole.BusinessMan.toS())) {
			qnaHeaderRepository.findByThisPKAndUserPKOrderByRegDateDesc(thisPK, tokenUser.thisPK) ?: throw QnAHeaderNotExistException()
		} else throw AuthenticationEntryPointException()
	}
	fun getContentData(token: String, thisPK: String): QnAContentModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		val data = if (haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			qnaHeaderRepository.findByThisPKOrderByRegDateDesc(thisPK) ?: throw QnAHeaderNotExistException()
		} else if (haveRole(tokenUser, UserRole.BusinessMan.toS())) {
			qnaHeaderRepository.findByThisPKAndUserPKOrderByRegDateDesc(thisPK, tokenUser.thisPK) ?: throw QnAHeaderNotExistException()
		} else throw AuthenticationEntryPointException()

		val content = qnaContentRepository.findByHeaderPK(data.thisPK) ?: throw QnAContentNotExistException()
		val contentFile = qnaFileRepository.findAllByHeaderPK(data.thisPK)
		val reply = qnaReplyRepository.findAllByHeaderPKOrderByRegDateAsc(data.thisPK)
		val replyFile = qnaReplyFileRepository.findAllByReplyPKIn(reply.map { it.thisPK })
		mergeReply(reply, replyFile)
		content.fileList = contentFile.toMutableList()
		content.replyList = reply.toMutableList()
		return content
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun postQnA(token: String, title: String, qnaContentModel: QnAContentModel): QnAHeaderModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		val header = QnAHeaderModel().apply {
			this.userPK = tokenUser.thisPK
			this.id = tokenUser.id
			this.name = tokenUser.name
			this.title = title
		}
		val content = QnAContentModel().apply {
			this.headerPK = header.thisPK
			this.userPK = header.userPK
			this.content = qnaContentModel.content
			this.fileList = qnaContentModel.fileList
		}
		content.fileList.onEach {
			it.thisPK = UUID.randomUUID().toString()
			it.headerPK = header.thisPK
		}
		qnaHeaderRepository.save(header)
		qnaContentRepository.save(content)
		if (content.fileList.isNotEmpty()) {
			qnaFileRepository.saveAll(content.fileList)
		}

		requestRepository.save(RequestModel().apply {
			this.requestUserPK = tokenUser.thisPK
			this.requestUserName = tokenUser.name
			this.requestItemPK = header.thisPK
			this.requestType = RequestType.QnA
		})

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add qna : $header")
		logRepository.save(logModel)
		return header
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun postQnAReplyAnswer(token: String, thisPK: String, qnaReplyModel: QnAReplyModel): QnAReplyModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val header = qnaHeaderRepository.findByThisPKOrderByRegDateDesc(thisPK) ?: throw QnAHeaderNotExistException()
		header.qnaState = QnAState.Reply
		val reply = QnAReplyModel().apply {
			this.headerPK = thisPK
			this.userPK = tokenUser.thisPK
			this.name = tokenUser.name
			this.content = qnaReplyModel.content
			this.fileList = qnaReplyModel.fileList
		}
		reply.fileList.onEach {
			it.thisPK = UUID.randomUUID().toString()
			it.replyPK = reply.thisPK
		}

		qnaHeaderRepository.save(header)
		val ret = qnaReplyRepository.save(reply)
		if (reply.fileList.isNotEmpty()) {
			qnaReplyFileRepository.saveAll(reply.fileList)
		}

		val request = requestRepository.findByRequestItemPK(header.thisPK)
		if (request != null) {
			requestRepository.save(request.apply {
				this.responseUserPK = tokenUser.thisPK
				this.responseUserName = tokenUser.name
				this.responseType = ResponseType.OK
				this.responseDate = Date()
			})
		}

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add qna reply : $reply")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun postQnAReplyQuestion(token: String, thisPK: String, qnaReplyModel: QnAReplyModel): QnAReplyModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		val header = qnaHeaderRepository.findByThisPKOrderByRegDateDesc(thisPK) ?: throw QnAHeaderNotExistException()
		if (header.qnaState != QnAState.Reply || header.qnaState == QnAState.OK) {
			throw NotValidOperationException()
		}

		header.qnaState = QnAState.Recep
		val reply = QnAReplyModel().apply {
			this.headerPK = thisPK
			this.userPK = tokenUser.thisPK
			this.name = tokenUser.name
			this.content = qnaReplyModel.content
			this.fileList = qnaReplyModel.fileList
		}
		reply.fileList.onEach {
			it.thisPK = UUID.randomUUID().toString()
			it.replyPK = reply.thisPK
		}

		qnaHeaderRepository.save(header)
		val ret = qnaReplyRepository.save(reply)
		if (reply.fileList.isNotEmpty()) {
			qnaReplyFileRepository.saveAll(reply.fileList)
		}

		val request = requestRepository.findByRequestItemPK(header.thisPK)
		if (request != null) {
			requestRepository.save(request.apply {
				this.requestDate = Date()
				this.responseType = ResponseType.Pending
			})
		} else {
			requestRepository.save(RequestModel().apply {
				this.requestUserPK = tokenUser.thisPK
				this.requestUserName = tokenUser.id
				this.requestItemPK = header.thisPK
				this.requestType = RequestType.QnA
			})
		}

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add qna reply : $reply")
		logRepository.save(logModel)
		return ret
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun putQnAComplete(token: String, thisPK: String): QnAHeaderModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		val header = if (haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			qnaHeaderRepository.findByThisPKOrderByRegDateDesc(thisPK) ?: throw QnAHeaderNotExistException()
		} else if (haveRole(tokenUser, UserRole.BusinessMan.toS())) {
			qnaHeaderRepository.findByThisPKAndUserPKOrderByRegDateDesc(thisPK, tokenUser.thisPK) ?: throw QnAHeaderNotExistException()
		} else {
			throw AuthenticationEntryPointException()
		}

		header.qnaState = QnAState.OK
		val ret = qnaHeaderRepository.save(header)
		val request = requestRepository.findByRequestItemPK(header.thisPK)
		if (request != null) {
			requestRepository.save(request.apply {
				this.responseDate = Date()
				this.responseType = ResponseType.OK
				this.responseUserPK = tokenUser.thisPK
				this.responseUserName = tokenUser.name
			})
		}

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "qna complete : $header")
		logRepository.save(logModel)

		return ret
	}
}