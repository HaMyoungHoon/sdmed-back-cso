package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.qna.QnAHeaderModel
import sdmed.back.model.sqlCSO.qna.QnAState
import java.util.Date

@Repository
interface IQnAHeaderRepository: JpaRepository<QnAHeaderModel, String> {
	fun findByThisPK(thisPK: String): QnAHeaderModel?
	fun findByThisPKAndUserPK(thisPK: String, userPK: String): QnAHeaderModel?
	fun findAllByUserPK(userPK: String): List<QnAHeaderModel>
	fun findAllByUserPKIn(userPK: List<String>): List<QnAHeaderModel>

	@Query("SELECT a FROM QnAHeaderModel a " +
			"WHERE a.qnaState = :qnaState1 OR a.qnaState = :qnaState2 " +
			"ORDER BY a.regDate ASC")
	fun selectAllHaveToReply(qnaState1: QnAState = QnAState.None, qnaState2: QnAState = QnAState.Recep): List<QnAHeaderModel>

	@Query("SELECT a FROM QnAHeaderModel a " +
			"WHERE a.regDate BETWEEN :startDate AND :endDate " +
			"ORDER BY a.regDate ASC")
	fun selectAllByDate(startDate: Date, endDate: Date): List<QnAHeaderModel>
}