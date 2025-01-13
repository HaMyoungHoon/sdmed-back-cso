package sdmed.back.model.sqlCSO.medicine

import jakarta.persistence.*
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import java.util.*

@Entity
data class MedicinePriceModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(20)")
	var kdCode: String = "",
	@Column
	var maxPrice: Int = 0,
	// mysql
	@Column(columnDefinition = "text")
//	@Column(columnDefinition = "nvarchar(max)")
	var ancestorCode: String = "",
	@Column
	var applyDate: Date = Date(),
) {
	fun findHeader(data: List<String>): Boolean {
		if (data.size < FConstants.MODEL_MEDICINE_PRICE_COUNT) {
			return false
		}

		for (index in 0 until FConstants.MODEL_MEDICINE_PRICE_COUNT) {
			if (data[index] != titleGet(index)) {
				return false
			}
		}

		return true
	}
	fun rowSet(data: List<String>): Boolean? {
		if (data.size <= 1) {
			return false
		}

		return try {
			for ((index, value) in data.withIndex()) {
				indexSet(value, index)
			}
			if (errorCondition()) {
				return false
			}
			true
		} catch (_: Exception) {
			null
		}
	}
	fun indexSet(data: String?, index: Int) {
		when (index) {
			4 -> kdCode = data ?: ""
			9 -> maxPrice = data?.toIntOrNull() ?: 0
			12 -> ancestorCode = data ?: ""
		}
	}
	fun titleGet(index: Int): String {
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
	fun errorCondition(): Boolean {
		if (kdCode.toIntOrNull() == null) {
			return true
		}

		return false
	}
	fun errorString() = "${FConstants.MODEL_MEDICINE_PRICE_KD_CODE} : ${kdCode}\n${FConstants.MODEL_MEDICINE_PRICE_MAX_PRICE} : ${maxPrice}"

	fun insertString(): String {
		val ancestorCode = FExtensions.escapeString(ancestorCode)
		val applyDate = FExtensions.parseDateTimeString(applyDate, "yyyy-MM-dd")
		return "('$thisPK', '$kdCode', '$maxPrice', '$ancestorCode', '$applyDate')"
	}
}