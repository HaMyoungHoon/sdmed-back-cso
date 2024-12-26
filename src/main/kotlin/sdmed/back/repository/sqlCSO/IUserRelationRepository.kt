package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.hospital.HospitalModel
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import sdmed.back.model.sqlCSO.pharma.PharmaModel
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
			"WHERE b.userPK = :userPK " +
			"ORDER BY a.code ASC ")
	fun selectAllMyHospital(userPK: String): List<HospitalModel>

	@Query("SELECT a FROM PharmaModel a " +
			"LEFT JOIN UserRelationModel b ON a.thisPK = b.pharmaPK " +
			"WHERE b.userPK = :userPK AND b.hosPK = :hosPK " +
			"ORDER BY a.code ASC ")
	fun selectAllMyPharma(userPK: String, hosPK: String): List<PharmaModel>

	@Query("SELECT a FROM MedicineModel a " +
			"LEFT JOIN UserRelationModel b ON a.thisPK = b.medicinePK " +
			"WHERE b.userPK = :userPK AND b.hosPK = :hosPK AND b.pharmaPK IN (:pharmaPKString) " +
			"ORDER BY a.code ASC ")
	fun selectAllMyMedicine(userPK: String, hosPK: String, pharmaPKString: String): List<MedicineModel>
}