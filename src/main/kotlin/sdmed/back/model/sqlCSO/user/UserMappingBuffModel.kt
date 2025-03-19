package sdmed.back.model.sqlCSO.user

import sdmed.back.config.FConstants
import sdmed.back.model.sqlCSO.FExcelParseModel

data class UserMappingBuffModel(
	var companyInnerName: String = "",
	var hospitalName: String = "",
	var pharmaName: String = "",
	var medicineName: String = "",
	var id: String = "",
	var hospitalCode: String = "",
	var pharmaCode: String = "",
	var medicineCode: String = ""
): FExcelParseModel() {
	override var dataCount = FConstants.MODEL_USER_RELATION_COUNT
	override fun indexSet(data: String?, index: Int) {
		when (index) {
			0 -> companyInnerName = data ?: ""
			1 -> hospitalName = data ?: ""
			2 -> pharmaName = data ?: ""
			3 -> medicineName = data ?: ""
		}
	}
	override fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_USER_RELATION_COMPANY_INNER_NAME
			1 -> FConstants.MODEL_USER_RELATION_HOSPITAL_NAME
			2 -> FConstants.MODEL_USER_RELATION_PHARMA_NAME
			3 -> FConstants.MODEL_USER_RELATION_MEDICINE_NAME
			else -> ""
		}
	}
	override fun errorCondition(): Boolean {
		if (companyInnerName.isBlank()) return true
		if (hospitalName.isBlank()) return true
		if (pharmaName.isBlank()) return true
		if (medicineName.isBlank()) return true

		return false
	}
	override fun errorString() = "${FConstants.MODEL_USER_RELATION_COMPANY_INNER_NAME} : ${companyInnerName}\n" +
			"${FConstants.MODEL_USER_RELATION_HOSPITAL_NAME} : ${hospitalName}\n" +
			"${FConstants.MODEL_USER_RELATION_PHARMA_NAME} : ${pharmaName}\n" +
			"${FConstants.MODEL_USER_RELATION_MEDICINE_NAME} : ${medicineName}"
}