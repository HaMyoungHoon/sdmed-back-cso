package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaModel

@Repository
interface IEDIUploadPharmaRepository: JpaRepository<EDIUploadPharmaModel, String> {
	fun findALlByEdiPKOrderByPharmaPK(ediPK: String): List<EDIUploadPharmaModel>
}