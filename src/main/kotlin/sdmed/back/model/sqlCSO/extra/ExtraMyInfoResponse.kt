package sdmed.back.model.sqlCSO.extra

import sdmed.back.model.sqlCSO.user.UserDataModel
import sdmed.back.model.sqlCSO.user.UserFileModel
import sdmed.back.model.sqlCSO.user.UserTrainingModel
import java.sql.Timestamp
import java.util.Date

data class ExtraMyInfoResponse(
    var thisPK: String = "",
    var id: String = "",
    var name: String = "",
    var companyName: String = "",
    var companyNumber: String = "",
    var bankAccount: String = "",
    var csoReportNumber: String = "",
    var contractDate: Date? = null,
    var regDate: Timestamp = Timestamp(Date().time),
    var lastLoginDate: Timestamp? = null,
    var hosList: MutableList<ExtraMyInfoHospital> = mutableListOf(),
    var fileList: MutableList<UserFileModel> = mutableListOf(),
    var trainingList: MutableList<UserTrainingModel> = mutableListOf()
) {
    fun parse(data: UserDataModel?): ExtraMyInfoResponse? {
        if (data == null) {
            return null
        }
        this.thisPK = data.thisPK
        this.id = data.id
        this.name = data.name
        this.companyName = data.companyName
        this.companyNumber = data.companyNumber
        this.bankAccount = data.bankAccount
        this.csoReportNumber = data.csoReportNumber
        this.contractDate = data.contractDate
        this.regDate = data.regDate
        this.lastLoginDate = data.lastLoginDate
        return this
    }
}