package sdmed.back.model.sqlCSO.medicine

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Transient
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.model.sqlCSO.FExcelParseModel
import java.util.*

@Entity
data class MedicineIngredientModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(255)", unique = true)
	var mainIngredientCode: String = "",
	// mysql
	@Column(columnDefinition = "text")
//	@Column(columnDefinition = "nvarchar(max)")
	var mainIngredientName: String = "",
	@JsonIgnore
	@Transient
	var name: String = ""
): FExcelParseModel() {
	@Transient
	override var dataCount = FConstants.MODEL_MEDICINE_PRICE_COUNT
	override fun indexSet(data: String?, index: Int) {
		when (index) {
			3 -> mainIngredientCode = data ?: ""
			4 -> mainIngredientName = data ?: ""
			5 -> name = data ?: ""
		}
	}
	override fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_MEDICINE_PRICE_INDEX
			1 -> FConstants.MODEL_MEDICINE_PRICE_METHOD
			2 -> FConstants.MODEL_MEDICINE_PRICE_CLASSIFY
			3 -> FConstants.MODEL_MEDICINE_PRICE_INGREDIENT_CODE
			4 -> FConstants.MODEL_MEDICINE_PRICE_KD_CODE
			5 -> FConstants.MODEL_MEDICINE_PRICE_NAME
			6 -> FConstants.MODEL_MEDICINE_PRICE_PHARMA_NAME
			7 -> FConstants.MODEL_MEDICINE_PRICE_STANDARD
			8 -> FConstants.MODEL_MEDICINE_PRICE_UNIT
			9 -> FConstants.MODEL_MEDICINE_PRICE_MAX_PRICE
			10 -> FConstants.MODEL_MEDICINE_PRICE_GENERAL
			11 -> FConstants.MODEL_MEDICINE_PRICE_ETC
			12 -> FConstants.MODEL_MEDICINE_PRICE_ANCESTOR_CODE
			else -> ""
		}
	}
	override fun errorCondition(): Boolean {
		if (mainIngredientCode.isBlank()) {
			return true
		} else if(mainIngredientName.isBlank()) {
			return true
		} else if (name.isNotEmpty()) {
			return true
		}

		return false
	}
	override fun errorString() = "${FConstants.MODEL_MEDICINE_PRICE_KD_CODE} : ${mainIngredientCode}\n${FConstants.MODEL_MEDICINE_PRICE_MAX_PRICE} : ${mainIngredientName}"

	fun insertString(): String {
		val mainIngredientCode = FExtensions.escapeString(mainIngredientCode)
		val mainIngredientName = FExtensions.escapeString(mainIngredientName)
		return "('$thisPK', '${mainIngredientCode}', '${mainIngredientName}')"
	}
}