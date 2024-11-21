package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.MedicineModel
import java.util.Date

@Repository
interface IMedicineRepository: JpaRepository<MedicineModel, Long> {
	fun findAllByOrderByName(pageable: Pageable): Page<MedicineModel>
	fun findAllByOrderByPharmaName(pageable: Pageable): Page<MedicineModel>

	fun findAllByApplyDate(applyDate: Date): List<MedicineModel>

	@Query("SELECT * FROM medicineModel " +
			"WHERE applyDate = :applyDate", nativeQuery = true)
	fun selectAllByApplyDate(applyDate: String): List<MedicineModel>

	@Query("WITH RankedMedicine AS (\n" +
			"    SELECT *, ROW_NUMBER() OVER (PARTITION BY kdCode ORDER BY applyDate DESC) as RN FROM MedicineModel\n" +
			")\n" +
			"SELECT * FROM RankedMedicine as MedicineModel WHERE RN = 1", nativeQuery = true)
	fun selectAllByRecentData(): List<MedicineModel>
}