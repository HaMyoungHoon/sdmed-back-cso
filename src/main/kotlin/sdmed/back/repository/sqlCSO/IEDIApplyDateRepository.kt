package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIApplyDateModel
import sdmed.back.model.sqlCSO.edi.EDIApplyDateState

@Repository
interface IEDIApplyDateRepository: JpaRepository<EDIApplyDateModel, String> {
	fun findByThisPK(thisPK: String): EDIApplyDateModel?
	fun findAllByOrderByYearDescMonthDesc(): List<EDIApplyDateModel>

	@Query("SELECT a FROM EDIApplyDateModel a " +
			"WHERE a.applyDateState = :applyDateState " +
			"ORDER BY a.year, a.month DESC ")
	fun selectAllByUse(applyDateState: EDIApplyDateState = EDIApplyDateState.Use): List<EDIApplyDateModel>

	@Query("SELECT a FROM EDIApplyDateModel a " +
			"WHERE a.year = :year AND a.month = :month")
	fun selectByApplyDate(year: String, month: String): EDIApplyDateModel?

	@Query("SELECT a FROM EDIApplyDateModel a " +
			"WHERE a.applyDateState = :applyDateState AND a.year = :year AND a.month = :month")
	fun selectByApplyDateAndUse(year: String, month: String, applyDateState: EDIApplyDateState = EDIApplyDateState.Use): EDIApplyDateModel?
}