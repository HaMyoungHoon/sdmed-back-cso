package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.common.ResponseType
import sdmed.back.model.sqlCSO.request.RequestModel
import sdmed.back.model.sqlCSO.request.RequestUserCountModel
import sdmed.back.model.sqlCSO.request.ResponseCountModel
import java.util.*

@Repository
interface IRequestRepository: JpaRepository<RequestModel, String> {
	fun findByThisPK(thisPK: String): RequestModel?
	fun findAllByResponseTypeAndRequestUserPKInOrderByRequestDateDesc(responseType: ResponseType = ResponseType.None, requestUserPK: List<String>): List<RequestModel>

	@Query("SELECT a FROM RequestModel a " +
			"WHERE (a.responseType = 0 OR a.responseType = 1) AND a.requestUserPK IN :childPKInString " +
			"ORDER BY a.requestDate DESC")
	fun selectAllByMyChildNoResponse(childPKInString: String): List<RequestModel>

	@Query("SELECT a FROM RequestModel a " +
			"WHERE a.responseType = 0 OR a.responseType = 1 " +
			"ORDER BY a.requestDate DESC")
	fun selectAllByNoResponse(): List<RequestModel>
	@Query("SELECT a FROM RequestModel a " +
			"WHERE a.requestDate BETWEEN :startDate AND :endDate " +
			"ORDER BY a.requestDate DESC")
	fun selectAllByBetweenRequestDate(startDate: Date, endDate: Date): List<RequestModel>

	@Query("SELECT count(a.responseType) as count, a.responseType as responseType FROM RequestModel a " +
			"WHERE a.requestDate BETWEEN :startDate AND :endDate " +
			"GROUP BY a.responseType")
	fun selectCountOfResponseType(startDate: Date, endDate: Date): List<ResponseCountModel>

	@Query("SELECT count(a.requestUserID) as count, a.requestUserID as requestUserID FROM RequestModel a " +
			"WHERE a.requestDate BETWEEN :startDate AND :endDate " +
			"GROUP BY a.requestUserID " +
			"ORDER BY count DESC LIMIT 10")
	fun selectTop10RequestUser(startDate: Date, endDate: Date): List<RequestUserCountModel>
}