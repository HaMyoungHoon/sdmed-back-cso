package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.common.medicine.MedicineDiv
import sdmed.back.model.sqlCSO.medicine.MedicineModel

@Repository
interface IMedicineRepository: JpaRepository<MedicineModel, String> {
	fun findByThisPK(thisPK: String): MedicineModel?
	fun findByCode(code: String): MedicineModel?
	fun findAllByThisPKIn(medicinePK: List<String>): List<MedicineModel>
	fun findAllByCodeIn(code: List<String>): List<MedicineModel>
	fun findAllByClientCodeOrderByOrgNameAsc(code: String): List<MedicineModel>

	@Query("SELECT a.* FROM MedicineModel a " +
			"WHERE a.inVisible = :inVisible AND (a.code LIKE %:code% OR a.kdCode LIKE %:kdCode%) " +
			"ORDER BY a.code ASC", nativeQuery = true)
	fun selectAllByCodeLikeOrKdCodeLike(code: String, kdCode: String, inVisible: Boolean = false): List<MedicineModel>

	@Query("SELECT a FROM MedicineModel a " +
			"LEFT JOIN PharmaModel b on a.makerCode = b.code " +
			"WHERE a.inVisible = :inVisible AND (a.innerName LIKE %:name% OR b.innerName LIKE %:pharma%) " +
			"ORDER BY a.code ASC")
	fun selectAllByNameContainingOrPharmaContaining(name: String, pharma: String, inVisible: Boolean = false): List<MedicineModel>

	@Query("SELECT new sdmed.back.model.sqlCSO.medicine.MedicineModel(a, b.orgName, c.orgName) " +
			"FROM MedicineModel a " +
			"LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
			"LEFT JOIN PharmaModel c ON a.clientCode = c.code " +
			"WHERE a.orgName IN (:orgNames) " +
			"ORDER BY a.code ASC")
	fun selectAllOrgNameInWithPharmaName(orgNames: List<String>): List<MedicineModel>

	@Query("SELECT new sdmed.back.model.sqlCSO.medicine.MedicineModel(a, b.orgName, c.orgName) " +
			"FROM MedicineModel a " +
			"LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
			"LEFT JOIN PharmaModel c ON a.clientCode = c.code " +
			"WHERE a.inVisible = :inVisible " +
			"ORDER BY a.code ASC")
	fun selectAllByInvisibleOrderByCode(inVisible: Boolean = false): List<MedicineModel>

	@Query("SELECT new sdmed.back.model.sqlCSO.medicine.MedicineModel(a, b.orgName, c.orgName) " +
			"FROM MedicineModel a " +
			"LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
			"LEFT JOIN PharmaModel c ON a.clientCode = c.code " +
			"WHERE a.inVisible = :inVisible AND a.medicineDiv = :medicineDiv " +
			"ORDER BY a.code ASC")
	fun selectAllByInvisibleOpenOrderByCode(inVisible: Boolean = false, medicineDiv: MedicineDiv = MedicineDiv.Open): List<MedicineModel>
}