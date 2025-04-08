package sdmed.back.model.sqlCSO.extra

data class ExtraEDIPharmaBuffModel(
	var thisPK: String = "",
	var hosPK: String = "",
	var code: String = "",
	var orgName: String = "",
) {
	var medicineList: MutableList<ExtraEDIMedicineBuffModel> = mutableListOf()
}