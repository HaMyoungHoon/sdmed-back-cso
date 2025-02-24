package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.IPLogModel
import java.sql.Timestamp

@Repository
interface IIPLogRepository: JpaRepository<IPLogModel, Long> {
	fun findByLocalAddrAndRequestUriAndDateTimeGreaterThan(localAddr: String, requestUri: String, dateTime: Timestamp): IPLogModel?


	@Query("SELECT a FROM IPLogModel a " +
			"ORDER BY a.dateTime Desc")
	fun selectIPLogModel(pageable: Pageable): Page<IPLogModel>
}