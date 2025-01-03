package sdmed.back.model.sqlCSO.edi

data class EDIPharmaBuffModel(
	var thisPK: String = "",
	var code: String = "",
	var orgName: String = "",
	var innerName: String = "",
) {
	var medicineList: MutableList<EDIMedicineBuffModel> = mutableListOf()
}