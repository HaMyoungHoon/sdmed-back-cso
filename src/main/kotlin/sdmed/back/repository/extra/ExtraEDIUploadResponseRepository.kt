package sdmed.back.repository.extra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIUploadResponseModel
import sdmed.back.model.sqlCSO.extra.ExtraEDIResponse

@Repository
interface ExtraEDIUploadResponseRepository: JpaRepository<EDIUploadResponseModel, String> {
    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraEDIResponse(a.thisPK, a.ediPK, a.pharmaPK, a.pharmaName, a.etc, a.ediState, a.regDate) " +
            "FROM EDIUploadResponseModel a " +
            "WHERE a.ediPK = :ediPK " +
            "ORDER BY a.regDate")
    fun selectAllEDIPK(ediPK: String): List<ExtraEDIResponse>
}