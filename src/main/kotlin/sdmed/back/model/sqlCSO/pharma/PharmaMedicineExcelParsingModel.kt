package sdmed.back.model.sqlCSO.pharma

import sdmed.back.config.FConstants
import java.util.UUID

data class PharmaMedicineExcelParsingModel(
	var thisPK: String = UUID.randomUUID().toString(),
	var pharmaCode: String = "",
	var medicineCodeList: MutableList<String> = mutableListOf()
) {
	fun findHeader(data: List<String>): Boolean {
		if (data.size < FConstants.MODEL_PHARMA_MEDICINE_PARSE_COUNT) {
			return false
		}

		for (index in 0 until FConstants.MODEL_PHARMA_MEDICINE_PARSE_COUNT) {
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
			2 -> pharmaCode = data ?: ""
			3 -> medicineCodeList.add(data ?: "")
		}
	}
	fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_PHARMA_MEDICINE_PARSE_PHARMA_NAME
			1 -> FConstants.MODEL_PHARMA_MEDICINE_PARSE_MEDICINE_NAME
			2 -> FConstants.MODEL_PHARMA_MEDICINE_PARSE_PHARMA_CODE
			3 -> FConstants.MODEL_PHARMA_MEDICINE_PARSE_MEDICINE_CODE
			else -> ""
		}
	}
	fun errorCondition(): Boolean {
		if (pharmaCode.isBlank()) {
			return true
		} else if(medicineCodeList.isEmpty() || medicineCodeList.find { it.isBlank() } != null) {
			return true
		}

		return false
	}
	fun errorString() = "${FConstants.MODEL_PHARMA_MEDICINE_PARSE_PHARMA_CODE} : ${pharmaCode}\n${FConstants.MODEL_PHARMA_MEDICINE_PARSE_MEDICINE_CODE} : ${medicineCodeList.joinToString(",")}"
}