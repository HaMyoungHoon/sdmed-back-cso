package sdmed.back.model.sqlCSO.edi

data class EDIUploadCheckModel(
	var name: String = "",
	var userPK: String = "",
	var hospitalPK: String = "",
	var hospitalName: String = ""
) {
	var subModel: MutableList<EDIUploadCheckSubModel> = mutableListOf()
}