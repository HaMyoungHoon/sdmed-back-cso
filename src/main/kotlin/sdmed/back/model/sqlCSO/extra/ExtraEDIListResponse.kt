package sdmed.back.model.sqlCSO.extra

import sdmed.back.model.sqlCSO.edi.EDIState
import sdmed.back.model.sqlCSO.edi.EDIType
import java.util.Date

data class ExtraEDIListResponse(
    var thisPK: String = "",
    var year: String = "",
    var month: String = "",
    var orgName: String = "",
    var tempHospitalPK: String = "",
    var tempOrgName: String = "",
    var ediState: EDIState = EDIState.None,
    var ediType: EDIType = EDIType.DEFAULT,
    var regDate: Date = Date(),
    var pharmaList: MutableList<String> = mutableListOf()
) {
    constructor(thisPK: String, year: String, month: String, orgName: String, tempHospitalPK: String, tempOrgName: String, ediState: EDIState, ediType: EDIType, regDate: Date) : this() {
        this.thisPK = thisPK
        this.year = year
        this.month = month
        this.orgName = orgName
        this.tempHospitalPK = tempHospitalPK
        this.tempOrgName = tempOrgName
        this.ediState = ediState
        this.ediType = ediType
        this.regDate = regDate
    }
}