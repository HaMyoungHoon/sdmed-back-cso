package sdmed.back.repository.extra

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.common.medicine.MedicineDiv
import sdmed.back.model.sqlCSO.extra.ExtraMedicinePriceResponse
import sdmed.back.model.sqlCSO.medicine.MedicineModel

@Repository
interface ExtraMedicineRepository: JpaRepository<MedicineModel, String> {
    fun findByThisPK(thisPK: String): MedicineModel?
    fun findAllByThisPKIn(medicinePK: List<String>): List<MedicineModel>

    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraMedicinePriceResponse(a, b.orgName, c.orgName) " +
            "FROM MedicineModel a " +
            "LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
            "LEFT JOIN PharmaModel c ON a.clientCode = c.code " +
            "WHERE a.inVisible = :inVisible AND a.medicineDiv = :medicineDiv " +
            "ORDER BY a.code ASC")
    fun selectAllByInvisibleOpenOrderByCode(inVisible: Boolean = false, medicineDiv: MedicineDiv = MedicineDiv.Open): List<ExtraMedicinePriceResponse>

    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraMedicinePriceResponse(a, b.orgName, c.orgName) " +
            "FROM MedicineModel a " +
            "LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
            "LEFT JOIN PharmaModel c ON a.clientCode = c.code " +
            "LEFT JOIN MedicineIngredientModel f ON a.mainIngredientCode = f.mainIngredientCode " +
            "WHERE a.inVisible = :inVisible AND a.medicineDiv = :medicineDiv " +
            "AND (a.orgName LIKE %:searchString% OR a.kdCode LIKE %:searchString% OR b.orgName LIKE %:searchString% OR c.orgName LIKE %:searchString% OR f.mainIngredientName LIKE %:searchString%) " +
            "ORDER BY a.code ASC")
    fun selectAllByInvisibleOpenLikeOrderByCode(searchString: String, inVisible: Boolean = false, medicineDiv: MedicineDiv = MedicineDiv.Open): List<ExtraMedicinePriceResponse>

    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraMedicinePriceResponse(a, b.orgName, c.orgName) " +
            "FROM MedicineModel a " +
            "LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
            "LEFT JOIN PharmaModel c ON a.clientCode = c.code " +
            "WHERE a.inVisible = :inVisible AND a.medicineDiv = :medicineDiv " +
            "ORDER BY a.code ASC")
    fun selectPagingByInvisibleOpenOrderByCode(pageable: Pageable, inVisible: Boolean = false, medicineDiv: MedicineDiv = MedicineDiv.Open): Page<ExtraMedicinePriceResponse>

    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraMedicinePriceResponse(a, b.orgName, c.orgName) " +
            "FROM MedicineModel a " +
            "LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
            "LEFT JOIN PharmaModel c ON a.clientCode = c.code " +
            "LEFT JOIN MedicineIngredientModel f ON a.mainIngredientCode = f.mainIngredientCode " +
            "WHERE a.inVisible = :inVisible AND a.medicineDiv = :medicineDiv " +
            "AND (a.orgName LIKE %:searchString% OR a.kdCode LIKE %:searchString% OR b.orgName LIKE %:searchString% OR c.orgName LIKE %:searchString% OR f.mainIngredientName LIKE %:searchString%) " +
            "ORDER BY a.code ASC")
    fun selectPagingByInvisibleOpenLikeOrderByCode(searchString: String, pageable: Pageable, inVisible: Boolean = false, medicineDiv: MedicineDiv = MedicineDiv.Open): Page<ExtraMedicinePriceResponse>
}