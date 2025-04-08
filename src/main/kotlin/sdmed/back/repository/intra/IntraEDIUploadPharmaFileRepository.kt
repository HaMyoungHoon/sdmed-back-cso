package sdmed.back.repository.intra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaFileModel

@Repository
interface IntraEDIUploadPharmaFileRepository: JpaRepository<EDIUploadPharmaFileModel, String> {
    fun findByThisPK(thisPK: String): EDIUploadPharmaFileModel?
    fun findAllByEdiPharmaPKIn(ediPharmaPK: List<String>): List<EDIUploadPharmaFileModel>
}