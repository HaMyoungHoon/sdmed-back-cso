package sdmed.back.model.sqlCSO.edi

data class EDIPharmaBuffModel(
	var thisPK: String = "",
	var hosPK: String = "",
	var code: String = "",
	var orgName: String = "",
) {
	var medicineList: MutableList<EDIMedicineBuffModel> = mutableListOf()
}