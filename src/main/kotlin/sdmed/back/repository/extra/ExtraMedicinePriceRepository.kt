package sdmed.back.repository.extra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.medicine.MedicinePriceModel

@Repository
interface ExtraMedicinePriceRepository: JpaRepository<MedicinePriceModel, String> {
    fun findAllByKdCodeOrderByApplyDateDesc(kdCode: String): List<MedicinePriceModel>

    @Query("WITH RankedMedicinePrice AS ( " +
            "SELECT *, ROW_NUMBER() OVER (PARTITION BY kdCode ORDER BY applyDate DESC) as RN FROM MedicinePriceModel) " +
            "SELECT * FROM RankedMedicinePrice as MedicinePriceModel WHERE RN = 1", nativeQuery = true)
    fun selectAllByRecentData(): List<MedicinePriceModel>
    @Query("WITH RankedMedicinePrice AS ( " +
            "SELECT *, ROW_NUMBER() OVER (PARTITION BY kdCode ORDER BY applyDate DESC) as RN FROM MedicinePriceModel) " +
            "SELECT * FROM RankedMedicinePrice as MedicinePriceModel WHERE RN = 1 AND kdCode IN (:kdCodeString)", nativeQuery = true)
    fun selectAllByRecentData(kdCodeString: List<String>): List<MedicinePriceModel>
}