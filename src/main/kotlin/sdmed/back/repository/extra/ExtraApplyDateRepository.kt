package sdmed.back.repository.extra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIApplyDateModel
import sdmed.back.model.sqlCSO.edi.EDIApplyDateState
import sdmed.back.model.sqlCSO.extra.ExtraEDIApplyDateResponse

@Repository
interface ExtraApplyDateRepository: JpaRepository<EDIApplyDateModel, String> {
    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraEDIApplyDateResponse(a.year, a.month)" +
            "FROM EDIApplyDateModel a " +
            "WHERE a.applyDateState = :applyDateState " +
            "ORDER BY a.year DESC, a.month DESC")
    fun selectAllByUse(applyDateState: EDIApplyDateState = EDIApplyDateState.Use): List<ExtraEDIApplyDateResponse>

    @Query("SELECT a FROM EDIApplyDateModel a " +
            "WHERE a.applyDateState = :applyDateState AND a.year = :year AND a.month = :month")
    fun selectByApplyDateAndUse(year: String, month: String, applyDateState: EDIApplyDateState = EDIApplyDateState.Use): EDIApplyDateModel?
}