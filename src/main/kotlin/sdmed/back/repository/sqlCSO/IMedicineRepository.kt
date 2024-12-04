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
	fun findAllByOrderByCode(): List<MedicineModel>
	fun findAllByThisPKIn(medicinePK: List<String>): List<MedicineModel>
	fun findAllByCodeIn(code: List<Int>): List<MedicineModel>
	fun findAllByNameContainingOrPharmaContaining(name: String, pharma: String): List<MedicineModel>

	@Query("SELECT a FROM MedicineModel a " +
			"WHERE a.code LIKE %:code% OR a.kdCode LIKE %:kdCode% " +
			"ORDER BY a.code", nativeQuery = true)
	fun selectAllByCodeLikeOrKdCodeLike(code: Int, kdCode: Int): List<MedicineModel>
}