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
	fun findAllByOrderByCode(): List<MedicineModel>
	fun findAllByThisPKIn(medicinePK: List<String>): List<MedicineModel>
	fun findAllByCodeIn(code: List<String>): List<MedicineModel>
	fun findAllByOrgNameIn(innerNames: List<String>): List<MedicineModel>
	fun findAllByInnerNameIn(innerNames: List<String>): List<MedicineModel>

	@Query("SELECT a.* FROM MedicineModel a " +
			"WHERE a.inVisible = :inVisible AND (a.code LIKE %:code% OR a.kdCode LIKE %:kdCode%) " +
			"ORDER BY a.code ASC", nativeQuery = true)
	fun selectAllByCodeLikeOrKdCodeLike(code: String, kdCode: String, inVisible: Boolean = false): List<MedicineModel>

	@Query("SELECT a FROM MedicineModel a " +
			"LEFT JOIN PharmaModel b on a.makerCode = b.code " +
			"WHERE a.inVisible = :inVisible AND (a.innerName LIKE %:name% OR b.innerName LIKE %:pharma%) " +
			"ORDER BY a.code ASC")
	fun selectAllByNameContainingOrPharmaContaining(name: String, pharma: String, inVisible: Boolean = false): List<MedicineModel>

	@Query("SELECT a FROM MedicineModel a " +
			"WHERE a.inVisible = :inVisible " +
			"ORDER BY a.code ASC")
	fun selectAllByInVisibleOrderByCode(inVisible: Boolean = false): List<MedicineModel>


	@Query("SELECT new sdmed.back.model.sqlCSO.medicine.MedicineModel(a, b.orgName, d.orgName) " +
			"FROM MedicineModel a " +
			"LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
			"LEFT JOIN PharmaMedicineRelationModel c ON a.thisPK = c.medicinePK " +
			"LEFT JOIN PharmaModel d ON c.pharmaPK = d.thisPK " +
			"WHERE a.inVisible = :inVisible " +
			"ORDER BY a.code ASC")
	fun selectAllByInvisibleOrderByCode(inVisible: Boolean = false): List<MedicineModel>

	@Query("SELECT new sdmed.back.model.sqlCSO.medicine.MedicineModel(a, b.orgName, d.orgName) " +
			"FROM MedicineModel a " +
			"LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
			"LEFT JOIN PharmaMedicineRelationModel c ON a.thisPK = c.medicinePK " +
			"LEFT JOIN PharmaModel d ON c.pharmaPK = d.thisPK " +
			"WHERE a.inVisible = :inVisible AND a.medicineDiv = :medicineDiv " +
			"ORDER BY a.code ASC")
	fun selectAllByInvisibleOpenOrderByCode(inVisible: Boolean = false, medicineDiv: MedicineDiv = MedicineDiv.Open): List<MedicineModel>

	@Query("SELECT new sdmed.back.model.sqlCSO.medicine.MedicineModel(a, b.orgName, d.orgName) " +
			"FROM MedicineModel a " +
			"LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
			"LEFT JOIN PharmaMedicineRelationModel c ON a.thisPK = c.medicinePK " +
			"LEFT JOIN PharmaModel d ON c.pharmaPK = d.thisPK " +
			"LEFT JOIN MedicineIngredientModel e ON a.mainIngredientCode = e.mainIngredientCode " +
			"WHERE a.inVisible = :inVisible " +
			"AND (a.orgName LIKE %:searchString% OR a.kdCode LIKE %:searchString% OR b.orgName LIKE %:searchString% OR d.orgName LIKE %:searchString% OR e.mainIngredientName LIKE %:searchString%) " +
			"ORDER BY a.code ASC")
	fun selectAllByInvisibleLikeOrderByCode(searchString: String, inVisible: Boolean = false): List<MedicineModel>

	@Query("SELECT new sdmed.back.model.sqlCSO.medicine.MedicineModel(a, b.orgName, d.orgName) " +
			"FROM MedicineModel a " +
			"LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
			"LEFT JOIN PharmaMedicineRelationModel c ON a.thisPK = c.medicinePK " +
			"LEFT JOIN PharmaModel d ON c.pharmaPK = d.thisPK " +
			"LEFT JOIN MedicineIngredientModel f ON a.mainIngredientCode = f.mainIngredientCode " +
			"WHERE a.inVisible = :inVisible AND a.medicineDiv = :medicineDiv " +
			"AND (a.orgName LIKE %:searchString% OR a.kdCode LIKE %:searchString% OR b.orgName LIKE %:searchString% OR d.orgName LIKE %:searchString% OR f.mainIngredientName LIKE %:searchString%) " +
			"ORDER BY a.code ASC")
	fun selectAllByInvisibleOpenLikeOrderByCode(searchString: String, inVisible: Boolean = false, medicineDiv: MedicineDiv = MedicineDiv.Open): List<MedicineModel>

	@Query("SELECT new sdmed.back.model.sqlCSO.medicine.MedicineModel(a, b.orgName, d.orgName) " +
			"FROM MedicineModel a " +
			"LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
			"LEFT JOIN PharmaMedicineRelationModel c ON a.thisPK = c.medicinePK " +
			"LEFT JOIN PharmaModel d ON c.pharmaPK = d.thisPK " +
			"WHERE a.inVisible = :inVisible " +
			"ORDER BY a.code ASC")
	fun selectPagingByInvisibleOrderByCode(pageable: Pageable, inVisible: Boolean = false): Page<MedicineModel>

	@Query("SELECT new sdmed.back.model.sqlCSO.medicine.MedicineModel(a, b.orgName, d.orgName) " +
			"FROM MedicineModel a " +
			"LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
			"LEFT JOIN PharmaMedicineRelationModel c ON a.thisPK = c.medicinePK " +
			"LEFT JOIN PharmaModel d ON c.pharmaPK = d.thisPK " +
			"WHERE a.inVisible = :inVisible AND a.medicineDiv = :medicineDiv " +
			"ORDER BY a.code ASC")
	fun selectPagingByInvisibleOpenOrderByCode(pageable: Pageable, inVisible: Boolean = false, medicineDiv: MedicineDiv = MedicineDiv.Open): Page<MedicineModel>

	@Query("SELECT new sdmed.back.model.sqlCSO.medicine.MedicineModel(a, b.orgName, d.orgName) " +
			"FROM MedicineModel a " +
			"LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
			"LEFT JOIN PharmaMedicineRelationModel c ON a.thisPK = c.medicinePK " +
			"LEFT JOIN PharmaModel d ON c.pharmaPK = d.thisPK " +
			"LEFT JOIN MedicineIngredientModel e ON a.mainIngredientCode = e.mainIngredientCode " +
			"WHERE a.inVisible = :inVisible " +
			"AND (a.orgName LIKE %:searchString% OR a.kdCode LIKE %:searchString% OR b.orgName LIKE %:searchString% OR d.orgName LIKE %:searchString% OR e.mainIngredientName LIKE %:searchString%) " +
			"ORDER BY a.code ASC")
	fun selectPagingByInvisibleLikeOrderByCode(searchString: String, pageable: Pageable, inVisible: Boolean = false): Page<MedicineModel>
	@Query("SELECT new sdmed.back.model.sqlCSO.medicine.MedicineModel(a, b.orgName, d.orgName) " +
			"FROM MedicineModel a " +
			"LEFT JOIN PharmaModel b ON a.makerCode = b.code " +
			"LEFT JOIN PharmaMedicineRelationModel c ON a.thisPK = c.medicinePK " +
			"LEFT JOIN PharmaModel d ON c.pharmaPK = d.thisPK " +
			"LEFT JOIN MedicineIngredientModel f ON a.mainIngredientCode = f.mainIngredientCode " +
			"WHERE a.inVisible = :inVisible AND a.medicineDiv = :medicineDiv " +
			"AND (a.orgName LIKE %:searchString% OR a.kdCode LIKE %:searchString% OR b.orgName LIKE %:searchString% OR d.orgName LIKE %:searchString% OR f.mainIngredientName LIKE %:searchString%) " +
			"ORDER BY a.code ASC")
	fun selectPagingByInvisibleOpenLikeOrderByCode(searchString: String, pageable: Pageable, inVisible: Boolean = false, medicineDiv: MedicineDiv = MedicineDiv.Open): Page<MedicineModel>
}