package sdmed.back.repository.extra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaMedicineModel

@Repository
interface ExtraEDIUploadPharmaMedicineRepository: JpaRepository<EDIUploadPharmaMedicineModel, String> {
    fun findAllByEdiPKAndInVisibleAndPharmaPKInOrderByMedicinePK(ediPK: String, inVisible: Boolean = false, pharmaPK: List<String>): List<EDIUploadPharmaMedicineModel>
}