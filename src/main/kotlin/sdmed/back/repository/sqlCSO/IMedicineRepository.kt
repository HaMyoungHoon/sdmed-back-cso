package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.medicine.MedicineModel

@Repository
interface IMedicineRepository: JpaRepository<MedicineModel, String> {
	fun findByThisPK(thisPK: String): MedicineModel?
	fun findByCode(code: String): MedicineModel?
	fun findAllByOrderByCode(): List<MedicineModel>
	fun findAllByThisPKIn(medicinePK: List<String>): List<MedicineModel>
	fun findAllByCodeIn(code: List<String>): List<MedicineModel>

	@Query("SELECT a.* FROM MedicineModel a " +
			"WHERE a.inVisible = :inVisible AND (a.code LIKE %:code% OR a.kdCode LIKE %:kdCode%) " +
			"ORDER BY a.code ASC", nativeQuery = true)
	fun selectAllByCodeLikeOrKdCodeLike(code: String, kdCode: String, inVisible: Boolean = false): List<MedicineModel>

	@Query("SELECT a FROM MedicineModel a " +
			"WHERE a.inVisible = :inVisible AND (a.name LIKE %:name% OR a.makerName LIKE %:pharma%) " +
			"ORDER BY a.code ASC")
	fun selectAllByNameContainingOrPharmaContaining(name: String, pharma: String, inVisible: Boolean = false): List<MedicineModel>

	@Query("SELECT a FROM MedicineModel a " +
			"WHERE a.inVisible = :inVisible " +
			"ORDER BY a.code ASC")
	fun selectAllByInVisibleOrderByCode(inVisible: Boolean = false): List<MedicineModel>
}