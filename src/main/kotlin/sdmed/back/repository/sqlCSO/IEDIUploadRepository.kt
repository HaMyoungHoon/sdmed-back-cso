package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIUploadCheckSubModel
import sdmed.back.model.sqlCSO.edi.EDIUploadCheckModel
import sdmed.back.model.sqlCSO.edi.EDIUploadModel
import java.util.Date

@Repository
interface IEDIUploadRepository: JpaRepository<EDIUploadModel, String> {
	fun findByThisPK(thisPK: String): EDIUploadModel?

	@Query("SELECT DISTINCT new sdmed.back.model.sqlCSO.edi.EDIUploadCheckModel(c.id, c.name, c.thisPK, b.thisPK, b.orgName, b.innerName) " +
			"FROM UserRelationModel a " +
			"LEFT JOIN HospitalModel b ON a.hosPK = b.thisPK " +
			"LEFT JOIN UserDataModel c ON a.userPK = c.thisPK " +
			"WHERE a.userPK = :userPK AND b.inVisible = false")
	fun selectCheckModelNull(userPK: String): List<EDIUploadCheckModel>
	@Query("SELECT DISTINCT new sdmed.back.model.sqlCSO.edi.EDIUploadCheckModel(c.id, c.name, c.thisPK, b.thisPK, b.orgName, b.innerName) " +
			"FROM UserRelationModel a " +
			"LEFT JOIN HospitalModel b ON a.hosPK = b.thisPK " +
			"LEFT JOIN UserDataModel c ON a.userPK = c.thisPK " +
			"WHERE a.userPK IN (:userPK) AND b.inVisible = false")
	fun selectCheckModelNullIn(userPK: List<String>): List<EDIUploadCheckModel>

	@Query("SELECT DISTINCT new sdmed.back.model.sqlCSO.edi.EDIUploadCheckSubModel(d.thisPK, b.thisPK, c.thisPK, c.orgName, sdmed.back.model.sqlCSO.edi.EDIState.None, CAST(null as date), '', '', '', '', '', '', '', false)" +
			"FROM UserRelationModel a " +
			"LEFT JOIN HospitalModel b ON a.hosPK = b.thisPK " +
			"LEFT JOIN PharmaModel c ON a.pharmaPK = c.thisPK " +
			"LEFT JOIN UserDataModel d ON a.userPK = d.thisPK " +
			"WHERE a.userPK = :userPK AND b.inVisible = false AND c.inVisible = false")
	fun selectCheckSubModelNull(userPK: String): List<EDIUploadCheckSubModel>
	@Query("SELECT DISTINCT new sdmed.back.model.sqlCSO.edi.EDIUploadCheckSubModel(d.thisPK, b.thisPK, c.thisPK, c.orgName, sdmed.back.model.sqlCSO.edi.EDIState.None, CAST(null as date), '', '', '', '', '', '', '', false)" +
			"FROM UserRelationModel a " +
			"LEFT JOIN HospitalModel b ON a.hosPK = b.thisPK " +
			"LEFT JOIN PharmaModel c ON a.pharmaPK = c.thisPK " +
			"LEFT JOIN UserDataModel d ON a.userPK = d.thisPK " +
			"WHERE a.userPK IN (:userPK) AND b.inVisible = false AND c.inVisible = false")
	fun selectCheckSubModelNullIn(userPK: List<String>): List<EDIUploadCheckSubModel>

	@Query("SELECT DISTINCT new sdmed.back.model.sqlCSO.edi.EDIUploadCheckSubModel(e.thisPK, c.thisPK, d.thisPK, d.orgName, b.ediState, a.regDate, a.thisPK, a.year, a.month, a.day, b.year, b.month, b.day, b.isCarriedOver) " +
			"FROM EDIUploadModel a " +
			"LEFT JOIN EDIUploadPharmaModel b ON a.thisPK = b.ediPK " +
			"LEFT JOIN HospitalModel c ON a.hospitalPK = c.thisPK " +
			"LEFT JOIN PharmaModel d ON b.pharmaPK = d.thisPK " +
			"LEFT JOIN UserDataModel e ON a.userPK = e.thisPK " +
			"WHERE a.userPK = :userPK AND a.ediState != 2 AND a.year = :year AND a.month = :month AND a.hospitalPK IN (:hospitalPKs)")
	fun selectCheckSubModelEDIDate2(userPK: String, year: String, month: String, hospitalPKs: List<String>): List<EDIUploadCheckSubModel>
	@Query("SELECT DISTINCT new sdmed.back.model.sqlCSO.edi.EDIUploadCheckSubModel(e.thisPK, c.thisPK, d.thisPK, d.orgName, b.ediState, a.regDate, a.thisPK, a.year, a.month, a.day, b.year, b.month, b.day, b.isCarriedOver) " +
			"FROM EDIUploadModel a " +
			"LEFT JOIN EDIUploadPharmaModel b ON a.thisPK = b.ediPK " +
			"LEFT JOIN HospitalModel c ON a.hospitalPK = c.thisPK " +
			"LEFT JOIN PharmaModel d ON b.pharmaPK = d.thisPK " +
			"LEFT JOIN UserDataModel e ON a.userPK = e.thisPK " +
			"WHERE a.userPK = :userPK AND a.ediState != 2 AND b.year = :year AND b.month = :month AND a.hospitalPK IN (:hospitalPKs)")
	fun selectCheckSubModelApplyDate2(userPK: String, year: String, month: String, hospitalPKs: List<String>): List<EDIUploadCheckSubModel>

	@Query("SELECT DISTINCT new sdmed.back.model.sqlCSO.edi.EDIUploadCheckSubModel(e.thisPK, c.thisPK, d.thisPK, d.orgName, b.ediState, a.regDate, a.thisPK, a.year, a.month, a.day, b.year, b.month, b.day, b.isCarriedOver) " +
			"FROM EDIUploadModel a " +
			"LEFT JOIN EDIUploadPharmaModel b ON a.thisPK = b.ediPK " +
			"LEFT JOIN HospitalModel c ON a.hospitalPK = c.thisPK " +
			"LEFT JOIN PharmaModel d ON b.pharmaPK = d.thisPK " +
			"LEFT JOIN UserDataModel e ON a.userPK = e.thisPK " +
			"WHERE a.userPK IN (:userPK) AND a.ediState != 2 AND a.year = :year AND a.month = :month AND a.hospitalPK IN (:hospitalPKs)")
	fun selectCheckSubModelEDIDateIn2(userPK: List<String>, year: String, month: String, hospitalPKs: List<String>): List<EDIUploadCheckSubModel>
	@Query("SELECT DISTINCT new sdmed.back.model.sqlCSO.edi.EDIUploadCheckSubModel(e.thisPK, c.thisPK, d.thisPK, d.orgName, b.ediState, a.regDate, a.thisPK, a.year, a.month, a.day, b.year, b.month, b.day, b.isCarriedOver) " +
			"FROM EDIUploadModel a " +
			"LEFT JOIN EDIUploadPharmaModel b ON a.thisPK = b.ediPK " +
			"LEFT JOIN HospitalModel c ON a.hospitalPK = c.thisPK " +
			"LEFT JOIN PharmaModel d ON b.pharmaPK = d.thisPK " +
			"LEFT JOIN UserDataModel e ON a.userPK = e.thisPK " +
			"WHERE a.userPK = (:userPK) AND a.ediState != 2 AND b.year = :year AND b.month = :month AND a.hospitalPK IN (:hospitalPKs)")
	fun selectCheckSubModelApplyDateIn2(userPK: List<String>, year: String, month: String, hospitalPKs: List<String>): List<EDIUploadCheckSubModel>
}