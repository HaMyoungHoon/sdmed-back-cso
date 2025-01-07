package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIUploadFileModel

@Repository
interface IEDIUploadFileRepository: JpaRepository<EDIUploadFileModel, String> {
	fun findByThisPK(thisPK: String): EDIUploadFileModel?
	fun findAllByEdiPKAndInVisibleOrderByThisPK(ediPK: String, inVisible: Boolean = false): List<EDIUploadFileModel>
}