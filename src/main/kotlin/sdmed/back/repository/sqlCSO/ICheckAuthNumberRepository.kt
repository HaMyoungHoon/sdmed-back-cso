package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.common.CheckAuthNumberModel
import java.util.Date

@Repository
interface ICheckAuthNumberRepository: JpaRepository<CheckAuthNumberModel, String> {
	@Query("SELECT a FROM CheckAuthNumberModel a " +
			"WHERE a.regDate BETWEEN :checkTimeBefore AND :checkTimeAfter AND " +
			"a.authNumber = :authNumber AND a.phoneNumber = :phoneNumber")
	fun selectCheckAuthNumber(authNumber: String, phoneNumber: String, checkTimeBefore: Date, checkTimeAfter: Date): List<CheckAuthNumberModel>

	@Query("SELECT a FROM CheckAuthNumberModel a " +
			"WHERE a.regDate BETWEEN :checkTimeBefore AND :checkTimeAfter AND " +
			"a.phoneNumber = :phoneNumber")
	fun selectCheckAuthNumberCount(phoneNumber: String, checkTimeBefore: Date, checkTimeAfter: Date): List<CheckAuthNumberModel>
}