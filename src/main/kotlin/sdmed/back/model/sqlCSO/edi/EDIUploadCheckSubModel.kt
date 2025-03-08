package sdmed.back.model.sqlCSO.edi

import java.util.*

data class EDIUploadCheckSubModel(
	var userPK: String = "",
	var hospitalPK: String = "",
	var pharmaPK: String? = null,
	var pharmaName: String? = null,
	var ediState: EDIState? = null,
	var regDate: Date? = null,
	var ediPK: String = "",
	var reqApplyYear: String = "",
	var reqApplyMonth: String = "",
	var reqApplyDay: String = "",
	var actualApplyYear: String? = null,
	var actualApplyMonth: String? = null,
	var actualApplyDay: String? = null,
	var isCarriedOver: Boolean? = null,
) {
	fun isNullValue(): Boolean {
		return pharmaPK == null || pharmaName == null || ediState == null || actualApplyYear == null || actualApplyMonth == null || actualApplyDay == null || isCarriedOver == null
	}
}