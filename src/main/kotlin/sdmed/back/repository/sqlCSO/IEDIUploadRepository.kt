package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIUploadListModel
import sdmed.back.model.sqlCSO.edi.EDIUploadModel
import java.util.Date

@Repository
interface IEDIUploadRepository: JpaRepository<EDIUploadModel, String> {
	fun findByThisPK(thisPK: String): EDIUploadModel?
	fun findByThisPKIn(thisPK: List<String>): List<EDIUploadModel>
	fun findByUserPKAndThisPK(userPK: String, thisPK: String): EDIUploadModel?

	@Query("SELECT a, b.name FROM EDIUploadModel a " +
			"LEFT JOIN UserDataModel b ON a.userPK = b.thisPK " +
			"WHERE a.regDate BETWEEN :startDate AND :endDate " +
			"ORDER BY a.regDate DESC")
	fun selectAllByDate(startDate: Date, endDate: Date): List<EDIUploadListModel>
	@Query("SELECT a, b.name FROM EDIUploadModel a " +
			"LEFT JOIN UserDataModel b ON a.userPK = b.thisPK " +
			"WHERE a.userPK IN (:childPKString) AND a.regDate BETWEEN :startDate AND :endDate " +
			"ORDER BY a.regDate DESC")
	fun selectAllByMyChildAndDate(childPKString: String, startDate: Date, endDate: Date): List<EDIUploadListModel>
	@Query("SELECT a, '' as name FROM EDIUploadModel a " +
			"WHERE a.userPK = :userPK AND a.regDate BETWEEN :startDate AND :endDate " +
			"ORDER BY a.regDate DESC")
	fun selectAllByMe(userPK: String, startDate: Date, endDate: Date): List<EDIUploadListModel>
}