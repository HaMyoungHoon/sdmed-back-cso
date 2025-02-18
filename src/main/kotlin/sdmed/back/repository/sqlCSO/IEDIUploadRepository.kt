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
	fun findByThisPKIn(thisPK: List<String>): List<EDIUploadModel>
	fun findByUserPKAndThisPK(userPK: String, thisPK: String): EDIUploadModel?

	@Query("SELECT a FROM EDIUploadModel a " +
			"WHERE a.regDate BETWEEN :startDate AND :endDate " +
			"ORDER BY a.regDate DESC")
	fun selectAllByDate(startDate: Date, endDate: Date): List<EDIUploadModel>
	@Query("SELECT a FROM EDIUploadModel a " +
			"WHERE a.regDate BETWEEN :startDate AND :endDate " +
			"ORDER BY a.regDate DESC")
	fun selectAllByMyChildAndDate(startDate: Date, endDate: Date): List<EDIUploadModel>
	@Query("SELECT a as name FROM EDIUploadModel a " +
			"WHERE a.userPK = :userPK AND a.regDate BETWEEN :startDate AND :endDate " +
			"ORDER BY a.regDate DESC")
	fun selectAllByMe(userPK: String, startDate: Date, endDate: Date): List<EDIUploadModel>


	@Query("SELECT DISTinct new sdmed.back.model.sqlCSO.edi.EDIUploadCheckModel(c.name, c.thisPK, b.thisPK, b.orgName) " +
			"FROM UserRelationModel a " +
			"LEFT JOIN HospitalModel b ON a.hosPK = b.thisPK " +
			"LEFT JOIN UserDataModel c ON a.userPK = c.thisPK " +
			"WHERE a.userPK = :userPK AND b.inVisible = false")
	fun selectCheckModelNull(userPK: String): List<EDIUploadCheckModel>
	@Query("SELECT DISTinct new sdmed.back.model.sqlCSO.edi.EDIUploadCheckModel(c.name, c.thisPK, b.thisPK, b.orgName) " +
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

	@Query("SELECT DISTINCT new sdmed.back.model.sqlCSO.edi.EDIUploadCheckSubModel(f.thisPK, d.thisPK, e.thisPK, e.orgName, b.ediState, c.regDate, c.thisPK, c.year, c.month, c.day, b.year, b.month, b.day, b.isCarriedOver)" +
			"FROM UserRelationModel a " +
			"LEFT JOIN EDIUploadPharmaModel b ON a.pharmaPK = b.pharmaPK " +
			"LEFT JOIN EDIUploadModel c ON b.ediPK = c.thisPK " +
			"LEFT JOIN HospitalModel d ON c.hospitalPK = d.thisPK " +
			"LEFT JOIN PharmaModel e ON b.pharmaPK = e.thisPK " +
			"LEFT JOIN UserDataModel f ON a.userPK = f.thisPK " +
			"WHERE a.userPK = :userPK AND d.inVisible = false AND e.inVisible = false " +
			"AND b.ediState != 2 AND c.year = :year AND c.month = :month " +
			"ORDER BY c.regDate DESC")
	fun selectCheckSubModelEDIDate(userPK: String, year: String, month: String): List<EDIUploadCheckSubModel>
	@Query("SELECT DISTINCT new sdmed.back.model.sqlCSO.edi.EDIUploadCheckSubModel(f.thisPK, d.thisPK, e.thisPK, e.orgName, b.ediState, c.regDate, c.thisPK, c.year, c.month, c.day, b.year, b.month, b.day, b.isCarriedOver)" +
			"FROM UserRelationModel a " +
			"LEFT JOIN EDIUploadPharmaModel b ON b.pharmaPK = a.pharmaPK " +
			"LEFT JOIN EDIUploadModel c ON c.thisPK = b.ediPK " +
			"LEFT JOIN HospitalModel d ON d.thisPK = c.hospitalPK " +
			"LEFT JOIN PharmaModel e ON e.thisPK = b.pharmaPK " +
			"LEFT JOIN UserDataModel f ON a.userPK = f.thisPK " +
			"WHERE a.userPK = :userPK AND d.inVisible = false AND e.inVisible = false " +
			"AND b.ediState != 2 AND b.year = :year AND b.month = :month " +
			"ORDER BY c.regDate DESC")
	fun selectCheckSubModelApplyDate(userPK: String, year: String, month: String): List<EDIUploadCheckSubModel>

	@Query("SELECT DISTINCT new sdmed.back.model.sqlCSO.edi.EDIUploadCheckSubModel(f.thisPK, d.thisPK, e.thisPK, e.orgName, b.ediState, c.regDate, c.thisPK, c.year, c.month, c.day, b.year, b.month, b.day, b.isCarriedOver)" +
			"FROM UserRelationModel a " +
			"LEFT JOIN EDIUploadPharmaModel b ON a.pharmaPK = b.pharmaPK " +
			"LEFT JOIN EDIUploadModel c ON b.ediPK = c.thisPK " +
			"LEFT JOIN HospitalModel d ON c.hospitalPK = d.thisPK " +
			"LEFT JOIN PharmaModel e ON b.pharmaPK = e.thisPK " +
			"LEFT JOIN UserDataModel f ON a.userPK = f.thisPK " +
			"WHERE a.userPK IN (:userPK) AND d.inVisible = false AND e.inVisible = false " +
			"AND b.ediState != 2 AND c.year = :year AND c.month = :month " +
			"ORDER BY c.regDate DESC")
	fun selectCheckSubModelEDIDateIn(userPK: List<String>, year: String, month: String): List<EDIUploadCheckSubModel>
	@Query("SELECT DISTINCT new sdmed.back.model.sqlCSO.edi.EDIUploadCheckSubModel(f.thisPK, d.thisPK, e.thisPK, e.orgName, b.ediState, c.regDate, c.thisPK, c.year, c.month, c.day, b.year, b.month, b.day, b.isCarriedOver)" +
			"FROM UserRelationModel a " +
			"LEFT JOIN EDIUploadPharmaModel b ON b.pharmaPK = a.pharmaPK " +
			"LEFT JOIN EDIUploadModel c ON c.thisPK = b.ediPK " +
			"LEFT JOIN HospitalModel d ON d.thisPK = c.hospitalPK " +
			"LEFT JOIN PharmaModel e ON e.thisPK = b.pharmaPK " +
			"LEFT JOIN UserDataModel f ON a.userPK = f.thisPK " +
			"WHERE a.userPK IN (:userPK) AND d.inVisible = false AND e.inVisible = false " +
			"AND b.ediState != 2 AND b.year = :year AND b.month = :month " +
			"ORDER BY c.regDate DESC")
	fun selectCheckSubModelApplyDateIn(userPK: List<String>, year: String, month: String): List<EDIUploadCheckSubModel>
}