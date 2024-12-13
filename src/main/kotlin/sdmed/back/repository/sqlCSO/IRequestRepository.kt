package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.common.ResponseType
import sdmed.back.model.sqlCSO.RequestModel
import java.util.*

@Repository
interface IRequestRepository: JpaRepository<RequestModel, String> {
	fun findAllByResponseTypeAndRequestUserPKInOrderByRequestDateDesc(responseType: ResponseType = ResponseType.None, requestUserPK: List<String>): List<RequestModel>

	@Query("SELECT a FROM RequestModel a " +
			"WHERE a.responseType = :responseType AND a.requestUserPK IN :childPKInString " +
			"ORDER BY a.requestDate DESC")
	fun selectAllByMyChildNoResponse(childPKInString: String, responseType: ResponseType = ResponseType.None): List<RequestModel>

	@Query("SELECT a FROM RequestModel a " +
			"WHERE a.responseType = :responseType " +
			"ORDER BY a.requestDate DESC")
	fun selectAllByNoResponse(responseType: ResponseType = ResponseType.None): List<RequestModel>
	@Query("SELECT a FROM RequestModel a " +
			"WHERE a.requestDate BETWEEN :startDate AND :endDate " +
			"ORDER BY a.requestDate DESC")
	fun selectAllByBetweenRequestDate(startDate: Date, endDate: Date): List<RequestModel>
}