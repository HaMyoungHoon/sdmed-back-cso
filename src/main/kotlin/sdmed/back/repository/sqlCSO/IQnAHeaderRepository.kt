package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.qna.QnAHeaderModel
import sdmed.back.model.sqlCSO.qna.QnAState
import java.util.Date

@Repository
interface IQnAHeaderRepository: JpaRepository<QnAHeaderModel, String> {
	fun findByThisPKOrderByRegDateDesc(thisPK: String): QnAHeaderModel?
	fun findByThisPKAndUserPKOrderByRegDateDesc(thisPK: String, userPK: String): QnAHeaderModel?
	fun findAllByUserPKOrderByRegDateDesc(userPK: String): List<QnAHeaderModel>
	fun findAllByUserPKOrderByRegDateDesc(userPK: String, pageable: Pageable): Page<QnAHeaderModel>
	fun findAllByUserPKInOrderByRegDateDesc(userPK: List<String>): List<QnAHeaderModel>

	@Query("SELECT a FROM QnAHeaderModel a " +
			"WHERE a.qnaState = :qnaState1 OR a.qnaState = :qnaState2 " +
			"ORDER BY a.regDate DESC")
	fun selectAllHaveToReply(qnaState1: QnAState = QnAState.None, qnaState2: QnAState = QnAState.Recep): List<QnAHeaderModel>

	@Query("SELECT a FROM QnAHeaderModel a " +
			"WHERE a.regDate BETWEEN :startDate AND :endDate " +
			"ORDER BY a.regDate DESC")
	fun selectAllByDate(startDate: Date, endDate: Date): List<QnAHeaderModel>

	@Query("SELECT a FROM QnAHeaderModel a " +
			"WHERE a.userPK = :userPK AND a.title LIKE %:searchString% " +
			"ORDER BY a.regDate DESC")
	fun selectAllLIKEUserPkOrderByRegDateDesc(userPK: String, searchString: String): List<QnAHeaderModel>
	@Query("SELECT a FROM QnAHeaderModel a " +
			"WHERE a.userPK = :userPK AND a.title LIKE %:searchString% " +
			"ORDER BY a.regDate DESC")
	fun selectAllLIKEUserPkOrderByRegDateDesc(userPK: String, searchString: String, pageable: Pageable): Page<QnAHeaderModel>
}