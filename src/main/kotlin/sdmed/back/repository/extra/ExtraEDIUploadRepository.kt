package sdmed.back.repository.extra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIUploadModel
import sdmed.back.model.sqlCSO.extra.ExtraEDIListResponse
import java.util.Date

@Repository
interface ExtraEDIUploadRepository: JpaRepository<EDIUploadModel, String> {
    fun findByThisPK(thisPK: String): EDIUploadModel?
    fun findByUserPKAndThisPK(userPK: String, thisPK: String): EDIUploadModel?

    @Query("SELECT a FROM EDIUploadModel a " +
            "LEFT JOIN EDIUploadPharmaModel b on a.thisPK = b.ediPK " +
            "WHERE b.thisPK = :pharmaPK")
    fun selectByPharmaPK(pharmaPK: String): EDIUploadModel?
    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraEDIListResponse(a.thisPK, a.year, a.month, a.orgName, a.tempHospitalPK, a.tempOrgName, a.ediState, a.ediType, a.regDate) " +
            "FROM EDIUploadModel a " +
            "WHERE a.userPK = :userPK AND a.regDate BETWEEN :startDate AND :endDate " +
            "ORDER BY a.regDate DESC")
    fun selectAllEDIUploadList(userPK: String, startDate: Date, endDate: Date): List<ExtraEDIListResponse>
}