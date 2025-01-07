package sdmed.back.model.sqlCSO.edi

data class EDIHosBuffModel(
	var thisPK: String = "",
	var orgName: String = "",
) {
	var pharmaList: MutableList<EDIPharmaBuffModel> = mutableListOf()
}