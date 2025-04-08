package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.user.UserMappingExcelModel
import sdmed.back.model.sqlCSO.user.UserRelationModel

@Repository
interface IUserRelationRepository: JpaRepository<UserRelationModel, String> {
	fun findAllByUserPK(userPK: String): List<UserRelationModel>
	fun findAllByUserPKAndHosPKAndPharmaPK(userPK: String, hosPK: String, pharmaPK: String): List<UserRelationModel>

	@Query("SELECT new sdmed.back.model.sqlCSO.user.UserMappingExcelModel(b.id, b.companyInnerName, c.orgName, d.orgName, e.orgName) FROM UserRelationModel a " +
			"LEFT JOIN UserDataModel b ON a.userPK = b.thisPK " +
			"LEFT JOIN HospitalModel c ON a.hosPK = c.thisPK " +
			"LEFT JOIN PharmaModel d ON a.pharmaPK = d.thisPK " +
			"LEFT JOIN MedicineModel e ON a.medicinePK = e.thisPK " +
			"WHERE b.companyInnerName IS NOT NULL AND c.orgName IS NOT NULL AND d.orgName IS NOT NULL AND e.orgName IS NOT NULL " +
			"ORDER BY b.id, b.companyInnerName, c.orgName, d.orgName, e.orgName ASC")
	fun selectAllExcelModel(): List<UserMappingExcelModel>
}