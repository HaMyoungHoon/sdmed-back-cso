package sdmed.back.model.sqlCSO.user

import sdmed.back.config.FConstants
import sdmed.back.model.sqlCSO.FExcelExportModel

data class UserMappingExcelModel(
    var userID: String = "",
    var companyInnerName: String = "",
    var hospitalName: String = "",
    var pharmaName: String = "",
    var medicineName: String = "",
) {
    var dataCount = FConstants.MODEL_USER_RELATION_COUNT + 1
    fun indexGet(index: Int): String {
        return when (index) {
            0 -> userID
            1 -> companyInnerName
            2 -> hospitalName
            3 -> pharmaName
            4 -> medicineName
            else -> ""
        }
    }
    fun titleGet(index: Int): String {
        return when (index) {
            0 -> FConstants.MODEL_USER_ID
            1 -> FConstants.MODEL_USER_RELATION_COMPANY_INNER_NAME
            2 -> FConstants.MODEL_USER_RELATION_HOSPITAL_NAME
            3 -> FConstants.MODEL_USER_RELATION_PHARMA_NAME
            4 -> FConstants.MODEL_USER_RELATION_MEDICINE_NAME
            else -> ""
        }
    }
}