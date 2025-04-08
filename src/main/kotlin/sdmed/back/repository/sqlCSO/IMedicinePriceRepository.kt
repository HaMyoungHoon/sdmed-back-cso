package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.medicine.MedicinePriceModel

@Repository
interface IMedicinePriceRepository: JpaRepository<MedicinePriceModel, String> {
	fun findAllByOrderByApplyDateDesc(): List<MedicinePriceModel>
	fun findAllByKdCodeOrderByApplyDateDesc(kdCode: String): List<MedicinePriceModel>

	@Query("WITH RankedMedicinePrice AS ( " +
			"SELECT *, ROW_NUMBER() OVER (PARTITION BY kdCode ORDER BY applyDate DESC) as RN FROM MedicinePriceModel) " +
			"SELECT * FROM RankedMedicinePrice as MedicinePriceModel WHERE RN = 1", nativeQuery = true)
	fun selectAllByRecentData(): List<MedicinePriceModel>

	@Query("WITH RankedMedicinePrice AS (" +
			"SELECT *, ROW_NUMBER() OVER (PARTITION BY kdCode ORDER BY applyDate DESC) as RN FROM MedicinePriceModel " +
			"WHERE applyDate <= :yearMonthDay) " +
			"SELECT * FROM RankedMedicinePrice as MedicinePriceModel WHERE RN = 1 AND kdCode IN (:kdCodeString) ", nativeQuery = true)
	fun selectAllByRecentDataKDCodeInAndYearMonth(kdCodeString: List<String>, yearMonthDay: String): List<MedicinePriceModel>

	@Query("SELECT applyDate FROM MedicinePriceModel " +
			"ORDER BY applyDate DESC " +
			"LIMIT 1", nativeQuery = true)
	fun selectLatestDate(): String
}