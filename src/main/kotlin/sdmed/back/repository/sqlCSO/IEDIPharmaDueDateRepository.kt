package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIPharmaDueDateModel
import sdmed.back.model.sqlCSO.pharma.PharmaModel
import java.util.Date

@Repository
interface IEDIPharmaDueDateRepository: JpaRepository<EDIPharmaDueDateModel, String> {
	fun findByThisPK(thisPK: String): EDIPharmaDueDateModel?

	@Query("SELECT a FROM EDIPharmaDueDateModel a " +
			"WHERE a.year = :year " +
			"ORDER BY a.year, a.month DESC")
	fun selectAllByThisYearDueDate(year: String): List<EDIPharmaDueDateModel>
	@Query("SELECT a FROM EDIPharmaDueDateModel a " +
			"WHERE a.year = :year AND a.month = :month " +
			"ORDER BY a.year, a.month DESC")
	fun selectAllByThisYearMonthDueDate(year: String, month: String): List<EDIPharmaDueDateModel>
	@Query("SELECT a.* FROM EDIPharmaDueDateModel a " +
			"WHERE STR_TO_DATE(CONCAT(a.year, '-', a.month, '-', a.day), '%Y-%m-%d') BETWEEN :startDate AND :endDate " +
			"ORDER BY a.year, a.month DESC", nativeQuery = true)
	fun selectAllByThisYearMonthRangeDueDate(startDate: Date, endDate: Date): List<EDIPharmaDueDateModel>
	@Query("SELECT a FROM PharmaModel a " +
			"WHERE a.inVisible = :inVisible AND a.thisPK NOT IN (" +
			"SELECT b.pharmaPK FROM EDIPharmaDueDateModel b " +
			"WHERE b.year = :year AND b.month = :month) " +
			"ORDER BY a.code DESC")
	fun selectPharmaListByThisYearMonthDueDate(year: String, month: String, inVisible: Boolean = false): List<PharmaModel>
	@Query("SELECT a FROM EDIPharmaDueDateModel a " +
			"WHERE a.pharmaPK = :pharmaPK AND a.year = :year " +
			"ORDER BY a.year, a.month DESC")
	fun selectAllByPharmaThisYearDueDate(pharmaPK: String, year: String): List<EDIPharmaDueDateModel>
	@Query("SELECT a FROM EDIPharmaDueDateModel a " +
			"WHERE a.year = :year AND a.month = :month " +
			"ORDER BY a.pharmaPK DESC")
	fun selectAllByPharmaInThisYearMonthDueDate(year: String, month: String): List<EDIPharmaDueDateModel>
	@Query("SELECT a FROM EDIPharmaDueDateModel a " +
			"WHERE a.pharmaPK = :pharmaPK AND a.year = :year AND a.month = :month")
	fun selectByPharmaThisYearMonthDueDate(pharmaPK: String, year: String, month: String): EDIPharmaDueDateModel?
	@Query("SELECT a FROM EDIPharmaDueDateModel a " +
			"WHERE a.pharmaPK = :pharmaPK AND a.year = :year AND a.month = :month")
	fun selectAllByPharmaThisYearMonthDueDate(pharmaPK: String, year: String, month: String): List<EDIPharmaDueDateModel>
}