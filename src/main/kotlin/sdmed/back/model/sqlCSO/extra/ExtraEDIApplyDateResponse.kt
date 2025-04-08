package sdmed.back.model.sqlCSO.extra

import java.util.UUID

data class ExtraEDIApplyDateResponse(
    var thisPK: String = UUID.randomUUID().toString(),
    var year: String = "",
    var month: String = "",
) {
}