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
	fun findAllByOrderByName(): List<MedicineModel>
	fun findAllByOrderByName(pageable: Pageable): Page<MedicineModel>
	fun findAllByOrderByPharmaName(pageable: Pageable): Page<MedicineModel>
	fun findAllByThisPKIn(medicinePK: List<String>): List<MedicineModel>
	fun findAllByKdCodeIn(kdCode: List<String>): List<MedicineModel>

	@Query("SELECT a from MedicineModel a " +
			"LEFT JOIN FETCH a.medicinePriceModel ")
	fun selectAll(): List<MedicineModel>
}