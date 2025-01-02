package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.pharma.PharmaMedicineRelationModel

@Repository
interface IPharmaMedicineRelationRepository: JpaRepository<PharmaMedicineRelationModel, String> {
	fun findAllByPharmaPK(pharmaPK: String): List<PharmaMedicineRelationModel>
	fun findAllByPharmaPKIn(pharmaPK: List<String>): List<PharmaMedicineRelationModel>
	fun findAllByMedicinePKIn(medicinePK: List<String>): List<PharmaMedicineRelationModel>
	fun findAllByPharmaPKNotAndMedicinePKIn(pharmaPK: String, medicinePK: List<String>): List<PharmaMedicineRelationModel>
}