package sdmed.back.repository.intra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaModel

@Repository
interface IntraEDIUploadPharmaRepository: JpaRepository<EDIUploadPharmaModel, String> {
    fun findByThisPK(thisPK: String): EDIUploadPharmaModel?
    fun findAllByEdiPKOrderByPharmaPK(ediPK: String): List<EDIUploadPharmaModel>
    fun findAllByEdiPKIn(ediPK: List<String>): List<EDIUploadPharmaModel>
}