package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaModel

@Repository
interface IEDIUploadPharmaRepository: JpaRepository<EDIUploadPharmaModel, String> {
	fun findByThisPK(thisPK: String): EDIUploadPharmaModel?
	fun findALlByEdiPKOrderByPharmaPK(ediPK: String): List<EDIUploadPharmaModel>

	@Query("SELECT a FROM EDIUploadPharmaModel a " +
			"LEFT JOIN EDIUploadModel b ON a.ediPK = b.thisPK " +
			"WHERE b.userPK = :userPK AND a.ediState != 2 ")
	fun selectAllByMyNotReject(userPK: String): List<EDIUploadPharmaModel>
}