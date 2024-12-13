package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.medicine.MedicineSubModel

@Repository
interface IMedicineSubRepository: JpaRepository<MedicineSubModel, String> {
	fun findByCode(code: Int): MedicineSubModel?
	fun findAllByOrderByCode(): List<MedicineSubModel>
	fun findALlByCodeInOrderByCode(code: List<Int>): List<MedicineSubModel>
}