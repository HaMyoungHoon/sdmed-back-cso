package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.IPBlockModel

@Repository
interface IIPBlockRepository: JpaRepository<IPBlockModel, Long> {
	fun findByIpAddr(ipAddr: String): IPBlockModel?
	@Query("SELECT a FROM IPBlockModel a " +
			"WHERE a.ipAddr LIKE :ipAddr% " +
			"ORDER BY a.regDate DESC")
	fun selectLikeIpAddr(ipAddr: String): List<IPBlockModel>
}