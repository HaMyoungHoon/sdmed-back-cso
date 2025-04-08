package sdmed.back.model.sqlCSO.extra

import sdmed.back.model.sqlCSO.edi.EDIState
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaFileModel

data class ExtraEDIPharma(
    var thisPK: String = "",
    var ediPK: String = "",
    var pharmaPK: String = "",
    var orgName: String = "",
    var year: String = "",
    var month: String = "",
    var day: String = "",
    var isCarriedOver: Boolean = false,
    var ediState: EDIState = EDIState.None,
    var fileList: MutableList<EDIUploadPharmaFileModel> = mutableListOf()
) {
    constructor(thisPK: String, ediPK: String, pharmaPK: String, orgName: String, year: String, month: String, day: String, isCarriedOver: Boolean, ediState: EDIState) : this() {
        this.thisPK = thisPK
        this.ediPK = ediPK
        this.pharmaPK = pharmaPK
        this.orgName = orgName
        this.year = year
        this.month = month
        this.day = day
        this.isCarriedOver = isCarriedOver
        this.ediState = ediState
    }
}