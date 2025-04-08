package sdmed.back.repository.extra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.medicine.MedicineIngredientModel

@Repository
interface ExtraMedicineIngredientRepository: JpaRepository<MedicineIngredientModel, String> {
    fun findAllByOrderByMainIngredientCode(): List<MedicineIngredientModel>
    fun findAllByMainIngredientCodeIn(mainIngredientCode: List<String>): List<MedicineIngredientModel>
}