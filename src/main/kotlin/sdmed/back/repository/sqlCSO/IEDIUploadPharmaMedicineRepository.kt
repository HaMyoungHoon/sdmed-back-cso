package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaMedicineModel

@Repository
interface IEDIUploadPharmaMedicineRepository: JpaRepository<EDIUploadPharmaMedicineModel, String> {
	fun findByThisPK(thisPK: String): EDIUploadPharmaMedicineModel?
	fun findByThisPKIn(thisPK: List<String>): List<EDIUploadPharmaMedicineModel>
	fun findAllByEdiPKAndInVisibleAndPharmaPKInOrderByMedicinePK(ediPK: String, inVisible: Boolean = false, pharmaPK: List<String>): List<EDIUploadPharmaMedicineModel>
}