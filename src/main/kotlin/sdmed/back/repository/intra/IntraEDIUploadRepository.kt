package sdmed.back.repository.intra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIUploadModel
import java.util.Date

@Repository
interface IntraEDIUploadRepository: JpaRepository<EDIUploadModel, String> {
    fun findByThisPK(thisPK: String): EDIUploadModel?
    fun findByThisPKIn(thisPK: List<String>): List<EDIUploadModel>

    @Query("SELECT a FROM EDIUploadModel a " +
            "LEFT JOIN EDIUploadPharmaModel b on a.thisPK = b.ediPK " +
            "WHERE b.thisPK = :pharmaPK")
    fun selectByPharmaPK(pharmaPK: String): EDIUploadModel?
    @Query("SELECT a FROM EDIUploadModel a " +
            "WHERE a.regDate BETWEEN :startDate AND :endDate " +
            "ORDER BY a.regDate DESC")
    fun selectAllByDate(startDate: Date, endDate: Date): List<EDIUploadModel>
    @Query("SELECT a FROM EDIUploadModel a " +
            "WHERE a.regDate BETWEEN :startDate AND :endDate " +
            "ORDER BY a.regDate DESC")
    fun selectAllByMyChildAndDate(startDate: Date, endDate: Date): List<EDIUploadModel>
}