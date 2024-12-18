package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.medicine.MedicinePriceModel

@Repository
interface IMedicinePriceRepository: JpaRepository<MedicinePriceModel, String> {
	fun findAllByOrderByApplyDateDesc(): List<MedicinePriceModel>
	fun findAllByKdCodeOrderByApplyDateDesc(kdCode: Int): List<MedicinePriceModel>
	fun findAllByKdCodeIn(kdCode: List<Int>): List<MedicinePriceModel>

	@Query("WITH RankedMedicinePrice AS (\n" +
			"    SELECT *, ROW_NUMBER() OVER (PARTITION BY kdCode ORDER BY applyDate DESC) as RN FROM medicinePriceModel\n" +
			")\n" +
			"SELECT * FROM RankedMedicinePrice as MedicinePriceModel WHERE RN = 1", nativeQuery = true)
	fun selectAllByRecentData(): List<MedicinePriceModel>

	@Query("SELECT TOP 1 applyDate FROM MedicinePriceModel " +
			"ORDER BY applyDate DESC", nativeQuery = true)
	fun selectLatestDate(): String
}