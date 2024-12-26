package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import sdmed.back.config.FServiceBase
import sdmed.back.model.sqlCSO.qna.QnAReplyFileModel
import sdmed.back.model.sqlCSO.qna.QnAReplyModel
import sdmed.back.repository.sqlCSO.*

class QnAService: FServiceBase() {
	@Autowired lateinit var qnaHeaderRepository: IQnAHeaderRepository
	@Autowired lateinit var qnaContentRepository: IQnAContentRepository
	@Autowired lateinit var qnaFileRepository: IQnAFileRepository
	@Autowired lateinit var qnaReplyRepository: IQnAReplyRepository
	@Autowired lateinit var qnaReplyFileRepository: IQnAReplyFileRepository


	protected fun mergeReply(reply: List<QnAReplyModel>, replyFile: List<QnAReplyFileModel>) {
		val replyFileMap = replyFile.associateBy { it.replyPK }
		for (buff in reply) {
			val replyFileBuff = replyFileMap[buff.thisPK]
			if (replyFileBuff != null) {
				buff.fileList.add(replyFileBuff)
			}
		}
	}
}