package sdmed.back.model.sqlCSO.user

import sdmed.back.config.FConstants
import sdmed.back.model.sqlCSO.FExcelParseModel

data class UserMappingBuffModel(
	var id: String = "",
	var hospitalCode: String = "",
	var pharmaCode: String = "",
	var medicineCode: String = ""
): FExcelParseModel() {
	override var dataCount = FConstants.MODEL_USER_RELATION_COUNT
	override fun indexSet(data: String?, index: Int) {
		when (index) {
			0 -> id = data ?: ""
			1 -> hospitalCode = data ?: ""
			2 -> pharmaCode = data ?: ""
			3 -> medicineCode = data ?: ""
		}
	}
	override fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_USER_RELATION_ID
			1 -> FConstants.MODEL_USER_RELATION_HOSPITAL_CODE
			2 -> FConstants.MODEL_USER_RELATION_PHARMA_CODE
			3 -> FConstants.MODEL_USER_RELATION_MEDICINE_CODE
			else -> ""
		}
	}
	override fun errorCondition(): Boolean {
		if (id.isBlank()) return true
		if (hospitalCode.isBlank()) return true
		if (pharmaCode.isBlank()) return true
		if (medicineCode.isBlank()) return true

		return false
	}
	override fun errorString() = "${FConstants.MODEL_USER_RELATION_ID} : ${id}\n" +
			"${FConstants.MODEL_USER_RELATION_HOSPITAL_CODE} : ${hospitalCode}\n" +
			"${FConstants.MODEL_USER_RELATION_PHARMA_CODE} : ${pharmaCode}\n" +
			"${FConstants.MODEL_USER_RELATION_MEDICINE_CODE} : ${medicineCode}"
}