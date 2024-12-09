package sdmed.back.model.sqlCSO

import sdmed.back.config.FConstants
import java.util.UUID

data class PharmaMedicineExcelParsingModel(
	var thisPK: String = UUID.randomUUID().toString(),
	var pharmaCode: Int = 0,
	var medicineCodeList: MutableList<Int> = mutableListOf()
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
			0 -> pharmaCode = data?.toIntOrNull() ?: 0
			1 -> medicineCodeList.add(data?.toIntOrNull() ?: 0)
		}
	}
	fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_PHARMA_MEDICINE_PARSE_PHARMA_CODE
			1 -> FConstants.MODEL_PHARMA_MEDICINE_PARSE_MEDICINE_CODE
			else -> ""
		}
	}
	fun errorCondition(): Boolean {
		if (pharmaCode == 0) {
			return true
		} else if(medicineCodeList.isEmpty() || medicineCodeList.find { it == 0 } != null) {
			return true
		}

		return false
	}
	fun errorString() = "${FConstants.MODEL_PHARMA_MEDICINE_PARSE_PHARMA_CODE} : ${pharmaCode}\n${FConstants.MODEL_PHARMA_MEDICINE_PARSE_MEDICINE_CODE} : ${medicineCodeList.joinToString(",")}"
}