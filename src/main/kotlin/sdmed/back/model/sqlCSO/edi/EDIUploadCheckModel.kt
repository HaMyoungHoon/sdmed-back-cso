package sdmed.back.model.sqlCSO.edi

data class EDIUploadCheckModel(
	var id: String = "",
	var name: String = "",
	var userPK: String = "",
	var hospitalPK: String = "",
	var orgName: String = "",
	var innerName: String = ""
) {
	var subModel: MutableList<EDIUploadCheckSubModel> = mutableListOf()
}