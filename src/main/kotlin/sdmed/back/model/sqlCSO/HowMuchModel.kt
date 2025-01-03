package sdmed.back.model.sqlCSO

import sdmed.back.model.sqlCSO.edi.EDIState

data class HowMuchModel(
	var hosPK: String = "",
	var hospitalName: String = "",
	var pharmaPK: String = "",
	var pharmaName: String = "",
	var medicinePK: String = "",
	var medicineName: String = "",
	var count: Int = 0,
	var charge: Int = 0,
	var price: Int = 0,
	var ediState: EDIState = EDIState.None
) {
}