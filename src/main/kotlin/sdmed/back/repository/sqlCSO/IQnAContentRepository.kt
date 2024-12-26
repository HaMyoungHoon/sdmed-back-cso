package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.qna.QnAContentModel

@Repository
interface IQnAContentRepository: JpaRepository<QnAContentModel, String> {
	fun findByHeaderPK(headerPK: String): QnAContentModel?


}