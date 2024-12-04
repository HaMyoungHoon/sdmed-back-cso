package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.MedicineIngredientModel

@Repository
interface IMedicineIngredientRepository: JpaRepository<MedicineIngredientModel, String> {
	fun findAllByOrderByMainIngredientCode(): List<MedicineIngredientModel>
	fun findAllByMainIngredientCodeIn(mainIngredientCode: List<String>): List<MedicineIngredientModel>
}