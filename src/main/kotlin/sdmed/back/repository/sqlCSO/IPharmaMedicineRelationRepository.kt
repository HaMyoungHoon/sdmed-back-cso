package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.pharma.PharmaMedicineRelationModel

@Repository
interface IPharmaMedicineRelationRepository: JpaRepository<PharmaMedicineRelationModel, String> {
	fun findAllByPharmaPK(pharmaPK: String): List<PharmaMedicineRelationModel>
	fun findAllByPharmaPKIn(pharmaPK: List<String>): List<PharmaMedicineRelationModel>
	fun findAllByMedicinePK(medicinePK: String): List<PharmaMedicineRelationModel>
	fun findAllByMedicinePKIn(medicinePK: List<String>): List<PharmaMedicineRelationModel>
	fun findAllByPharmaPKNotAndMedicinePKIn(pharmaPK: String, medicinePK: List<String>): List<PharmaMedicineRelationModel>

	@Query("SELECT a FROM PharmaMedicineRelationModel a " +
			"LEFT JOIN PharmaModel b ON a.thisPK = b.thisPK " +
			"WHERE b.code IN (:code)")
	fun selectAllByCodeIn(code: List<Int>): List<PharmaMedicineRelationModel>
}