package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.qna.QnAFileModel

@Repository
interface IQnAFileRepository: JpaRepository<QnAFileModel, String> {
	fun findAllByHeaderPK(headerPK: String): List<QnAFileModel>
}