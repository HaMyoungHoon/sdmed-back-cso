package sdmed.back.repository.extra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaModel
import sdmed.back.model.sqlCSO.extra.ExtraEDIPharma
import sdmed.back.model.sqlCSO.extra.ExtraEDIListPharmaNames

@Repository
interface ExtraEDIUploadPharmaRepository: JpaRepository<EDIUploadPharmaModel, String> {
    fun findAllByEdiPKOrderByPharmaPK(ediPK: String): List<EDIUploadPharmaModel>

    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraEDIPharma(a.thisPK, a.ediPK, a.pharmaPK, a.orgName, a.year, a.month, a.day, a.isCarriedOver, a.ediState) " +
            "FROM EDIUploadPharmaModel a " +
            "WHERE a.ediPK = :ediPK")
    fun selectAllByEDIPK(ediPK: String): List<ExtraEDIPharma>
    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraEDIListPharmaNames(a.ediPK, a.orgName) " +
            "FROM EDIUploadPharmaModel a " +
            "WHERE a.ediPK IN (:ediPK)")
    fun selectAllEDIPKIn(ediPK: List<String>): List<ExtraEDIListPharmaNames>
}