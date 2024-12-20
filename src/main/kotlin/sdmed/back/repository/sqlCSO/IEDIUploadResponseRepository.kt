package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIUploadResponseModel

@Repository
interface IEDIUploadResponseRepository: JpaRepository<EDIUploadResponseModel, String> {
	fun findAllByEdiPKOrderByRegDate(ediPK: String): List<EDIUploadResponseModel>
}