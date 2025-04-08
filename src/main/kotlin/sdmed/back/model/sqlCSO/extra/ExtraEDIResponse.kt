package sdmed.back.model.sqlCSO.extra

import sdmed.back.model.sqlCSO.edi.EDIState
import java.util.Date

data class ExtraEDIResponse(
    var thisPK: String = "",
    var ediPK: String = "",
    var pharmaPK: String = "",
    var pharmaName: String = "",
    var etc: String = "",
    var ediState: EDIState = EDIState.None,
    var regDate: Date = Date(),
)