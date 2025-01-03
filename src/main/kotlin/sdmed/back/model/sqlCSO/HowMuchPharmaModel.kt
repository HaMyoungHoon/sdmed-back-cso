package sdmed.back.model.sqlCSO

import sdmed.back.model.sqlCSO.edi.EDIState

data class HowMuchPharmaModel(
	var thisPK: String = "",
	var name: String = "",
	var count: Int = 0,
	var price: Double = 0.0,
	var ediState: EDIState = EDIState.None
) {
}