package sdmed.back.model.sqlCSO.edi

import java.util.*

data class EDIUploadCheckSubModel(
	var userPK: String = "",
	var hospitalPK: String = "",
	var pharmaPK: String = "",
	var pharmaName: String = "",
	var ediState: EDIState = EDIState.None,
	var regDate: Date? = null,
	var ediPK: String = "",
	var reqApplyYear: String = "",
	var reqApplyMonth: String = "",
	var reqApplyDay: String = "",
	var actualApplyYear: String = "",
	var actualApplyMonth: String = "",
	var actualApplyDay: String = "",
	var isCarriedOver: Boolean = false,
) {
}