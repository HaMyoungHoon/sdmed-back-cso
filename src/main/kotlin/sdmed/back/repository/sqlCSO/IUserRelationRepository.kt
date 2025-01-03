package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.edi.EDIMedicineBuffModel
import sdmed.back.model.sqlCSO.edi.EDIPharmaBuffModel
import sdmed.back.model.sqlCSO.hospital.HospitalModel
import sdmed.back.model.sqlCSO.user.UserRelationModel

@Repository
interface IUserRelationRepository: JpaRepository<UserRelationModel, String> {
	fun findAllByUserPK(userPK: String): List<UserRelationModel>
	fun findAllByHosPK(hosPK: String): List<UserRelationModel>
	fun findAllByHosPKIn(hosPK: List<String>): List<UserRelationModel>
	fun findAllByPharmaPK(pharmaPK: String): List<UserRelationModel>
	fun findAllByPharmaPKIn(pharmaPK: List<String>): List<UserRelationModel>
	fun findAllByMedicinePK(medicinePK: String): List<UserRelationModel>
	fun findAllByMedicinePKIn(medicinePK: List<String>): List<UserRelationModel>

	@Query("SELECT a FROM HospitalModel a " +
			"LEFT JOIN UserRelationModel b ON a.thisPK = b.hosPK " +
			"WHERE a.inVisible = false AND b.userPK = :userPK " +
			"ORDER BY a.code ASC ")
	fun selectAllMyHospital(userPK: String): List<HospitalModel>

	@Query("SELECT new sdmed.back.model.sqlCSO.edi.EDIPharmaBuffModel(a.thisPK, a.code, a.orgName, a.innerName) " +
			"FROM PharmaModel a " +
			"LEFT JOIN UserRelationModel b ON a.thisPK = b.pharmaPK " +
			"WHERE a.inVisible = false AND b.userPK = :userPK AND b.hosPK = :hosPK " +
			"ORDER BY a.code ASC ")
	fun selectAllMyPharma(userPK: String, hosPK: String): List<EDIPharmaBuffModel>

	@Query("SELECT new sdmed.back.model.sqlCSO.edi.EDIMedicineBuffModel(a.thisPK, a.code, c.orgName, a.name, b.pharmaPK) FROM MedicineModel a " +
			"LEFT JOIN UserRelationModel b ON a.thisPK = b.medicinePK " +
			"LEFT JOIN PharmaModel c ON b.pharmaPK = c.thisPK " +
			"WHERE a.inVisible = false AND b.userPK = :userPK AND b.hosPK = :hosPK " +
			"ORDER BY a.code ASC ")
	fun selectAllMyMedicine(userPK: String, hosPK: String): List<EDIMedicineBuffModel>
}