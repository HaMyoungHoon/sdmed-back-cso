package sdmed.back.model.sqlCSO.pharma

import sdmed.back.config.FConstants
import sdmed.back.model.sqlCSO.FExcelParseModel
import java.util.UUID

data class PharmaMedicineExcelParsingModel(
	var thisPK: String = UUID.randomUUID().toString(),
	var pharmaCode: String = "",
	var medicineCodeList: MutableList<String> = mutableListOf()
): FExcelParseModel() {
	override var dataCount = FConstants.MODEL_PHARMA_MEDICINE_PARSE_COUNT
	override fun indexSet(data: String?, index: Int) {
		when (index) {
			2 -> pharmaCode = data ?: ""
			3 -> medicineCodeList.add(data ?: "")
		}
	}
	override fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_PHARMA_MEDICINE_PARSE_PHARMA_NAME
			1 -> FConstants.MODEL_PHARMA_MEDICINE_PARSE_MEDICINE_NAME
			2 -> FConstants.MODEL_PHARMA_MEDICINE_PARSE_PHARMA_CODE
			3 -> FConstants.MODEL_PHARMA_MEDICINE_PARSE_MEDICINE_CODE
			else -> ""
		}
	}
	override fun errorCondition(): Boolean {
		if (pharmaCode.isBlank()) {
			return true
		} else if(medicineCodeList.isEmpty() || medicineCodeList.find { it.isBlank() } != null) {
			return true
		}

		return false
	}
	override fun errorString() = "${FConstants.MODEL_PHARMA_MEDICINE_PARSE_PHARMA_CODE} : ${pharmaCode}\n${FConstants.MODEL_PHARMA_MEDICINE_PARSE_MEDICINE_CODE} : ${medicineCodeList.joinToString(",")}"
}