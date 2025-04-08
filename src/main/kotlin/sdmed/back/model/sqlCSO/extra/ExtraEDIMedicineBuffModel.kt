package sdmed.back.model.sqlCSO.extra

data class ExtraEDIMedicineBuffModel(
	var thisPK: String = "",
	var code: String = "",
	var pharma: String = "",
	var name: String = "",
	var pharmaPK: String = "",
	var hosPK: String = "",
) {
}