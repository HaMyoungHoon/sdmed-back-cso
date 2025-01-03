package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.HowMuchModel
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaMedicineModel

@Repository
interface IEDIUploadPharmaMedicineRepository: JpaRepository<EDIUploadPharmaMedicineModel, String> {
	fun findByThisPK(thisPK: String): EDIUploadPharmaMedicineModel?
	fun findByThisPKIn(thisPK: List<String>): List<EDIUploadPharmaMedicineModel>
	fun findAllByEdiPKAndInVisibleAndPharmaPKInOrderByMedicinePK(ediPK: String, inVisible: Boolean = false, pharmaPK: List<String>): List<EDIUploadPharmaMedicineModel>

	@Query("SELECT new sdmed.back.model.sqlCSO.HowMuchModel(c.hospitalPK, c.orgName, b.pharmaPK, b.orgName, a.medicinePK, a.name, a.count, a.charge, a.price, c.ediState) FROM EDIUploadPharmaMedicineModel a " +
			"LEFT JOIN EDIUploadPharmaModel b ON a.pharmaPK = b.pharmaPK " +
			"LEFT JOIN EDIUploadModel c ON a.ediPK = c.thisPK " +
			"WHERE a.inVisible = false AND b.ediState != 2 AND c.userPK = :userPK AND b.year = :year AND b.month = :month " +
			"ORDER BY a.name ASC ")
	fun selectAllByUserYearMonth(userPK: String, year: String, month: String): List<HowMuchModel>

	@Query("SELECT new sdmed.back.model.sqlCSO.HowMuchModel(c.hospitalPK, c.orgName, b.pharmaPK, b.orgName, a.medicinePK, a.name, a.count, a.charge, a.price, c.ediState) FROM EDIUploadPharmaMedicineModel a " +
			"LEFT JOIN EDIUploadPharmaModel b ON a.pharmaPK = b.pharmaPK " +
			"LEFT JOIN EDIUploadModel c ON a.ediPK = c.thisPK " +
			"WHERE a.inVisible = false AND b.ediState = 1 AND c.userPK = :userPK AND b.year = :year AND b.month = :month " +
			"ORDER BY a.name ASC ")
	fun selectAllByUserYearMonthOK(userPK: String, year: String, month: String): List<HowMuchModel>
}