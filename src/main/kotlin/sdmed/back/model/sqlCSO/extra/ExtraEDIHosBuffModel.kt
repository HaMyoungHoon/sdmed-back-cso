package sdmed.back.model.sqlCSO.extra

data class ExtraEDIHosBuffModel(
	var thisPK: String = "",
	var orgName: String = "",
) {
	var pharmaList: MutableList<ExtraEDIPharmaBuffModel> = mutableListOf()
}