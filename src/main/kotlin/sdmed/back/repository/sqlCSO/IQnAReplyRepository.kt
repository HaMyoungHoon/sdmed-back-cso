package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.qna.QnAReplyModel

@Repository
interface IQnAReplyRepository: JpaRepository<QnAReplyModel, String> {
	fun findAllByHeaderPKOrderByRegDateAsc(headerPK: String): List<QnAReplyModel>
}