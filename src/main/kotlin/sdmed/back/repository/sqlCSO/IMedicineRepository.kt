package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.MedicineModel
import sdmed.back.model.sqlCSO.PharmaModel
import java.util.Date

@Repository
interface IMedicineRepository: JpaRepository<MedicineModel, String> {
	fun findByThisPK(thisPK: String): MedicineModel?
	fun findByCode(code: Int): MedicineModel?
	fun findAllByOrderByCode(): List<MedicineModel>
	fun findAllByThisPKIn(medicinePK: List<String>): List<MedicineModel>
	fun findAllByCodeIn(code: List<Int>): List<MedicineModel>

	@Query("SELECT a FROM MedicineModel a " +
			"WHERE a.inVisible = :inVisible AND (a.code LIKE %:code% OR a.kdCode LIKE %:kdCode%) " +
			"ORDER BY a.code ASC", nativeQuery = true)
	fun selectAllByCodeLikeOrKdCodeLike(code: Int, kdCode: Int, inVisible: Boolean = false): List<MedicineModel>

	@Query("SELECT a FROM MedicineModel a " +
			"WHERE a.inVisible = :inVisible AND (a.name LIKE %:name% OR a.pharma LIKE %:pharma%) " +
			"ORDER BY a.code ASC")
	fun selectAllByNameContainingOrPharmaContaining(name: String, pharma: String, inVisible: Boolean = false): List<MedicineModel>
}