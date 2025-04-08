package sdmed.back.model.sqlCSO.extra

import sdmed.back.model.sqlCSO.edi.EDIState
import sdmed.back.model.sqlCSO.edi.EDIType
import sdmed.back.model.sqlCSO.edi.EDIUploadModel
import java.util.Date

data class ExtraEDIDetailResponse(
    var thisPK: String = "",
    var year: String = "",
    var month: String = "",
    var orgName: String = "",
    var tempHospitalPK: String = "",
    var tempOrgName: String = "",
    var ediState: EDIState = EDIState.None,
    var ediType: EDIType = EDIType.DEFAULT,
    var regDate: Date = Date(),
    var pharmaList: MutableList<ExtraEDIPharma> = mutableListOf(),
    var responseList: MutableList<ExtraEDIResponse> = mutableListOf()
) {
    fun parse(data: EDIUploadModel): ExtraEDIDetailResponse {
        thisPK = data.thisPK
        year = data.year
        month = data.month
        orgName = data.orgName
        tempHospitalPK = data.tempHospitalPK
        tempOrgName = data.tempOrgName
        ediState = data.ediState
        ediType = data.ediType
        regDate = data.regDate
        return this
    }
}