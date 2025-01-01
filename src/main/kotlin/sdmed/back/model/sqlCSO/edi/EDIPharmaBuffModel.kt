package sdmed.back.model.sqlCSO.edi

data class EDIPharmaBuffModel(
	var thisPK: String = "",
	var code: Int = 0,
	var orgName: String = "",
	var innerName: String = "",
) {
	var medicineList: MutableList<EDIMedicineBuffModel> = mutableListOf()
}