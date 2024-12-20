package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIPharmaDueDateModel

@Repository
interface IEDIPharmaDueDateRepository: JpaRepository<EDIPharmaDueDateModel, String> {
	@Query("SELECT a FROM EDIPharmaDueDateModel a " +
			"WHERE a.year = :year " +
			"ORDER BY a.year, a.month DESC")
	fun selectAllByThisYearDueDate(year: String): List<EDIPharmaDueDateModel>
	@Query("SELECT a FROM EDIPharmaDueDateModel a " +
			"WHERE a.year = :year AND a.month = :month " +
			"ORDER BY a.year, a.month DESC")
	fun selectAllByThisYearMonthDueDate(year: String, month: String): List<EDIPharmaDueDateModel>
	@Query("SELECT a FROM EDIPharmaDueDateModel a " +
			"WHERE a.pharmaPK = :pharmaPK AND a.year = :year " +
			"ORDER BY a.year, a.month DESC")
	fun selectAllByPharmaThisYearDueDate(pharmaPK: String, year: String): List<EDIPharmaDueDateModel>
	@Query("SELECT a FROM EDIPharmaDueDateModel a " +
			"WHERE a.pharmaPK IN (:pharmaPK) AND a.year = :year AND a.month = :month " +
			"ORDER BY a.pharmaPK DESC")
	fun selectAllByPharmaInThisYearMonthDueDate(pharmaPKString: String, year: String, month: String): List<EDIPharmaDueDateModel>
	@Query("SELECT a FROM EDIPharmaDueDateModel a " +
			"WHERE a.pharmaPK = :pharmaPK AND a.year = :year AND a.month = :month")
	fun selectByPharmaThisYearMonthDueDate(pharmaPK: String, year: String, month: String): EDIPharmaDueDateModel?
	@Query("SELECT a FROM EDIPharmaDueDateModel a " +
			"WHERE a.pharmaPK = :pharmaPK AND a.year = :year AND a.month = :month")
	fun selectAllByPharmaThisYearMonthDueDate(pharmaPK: String, year: String, month: String): List<EDIPharmaDueDateModel>
}