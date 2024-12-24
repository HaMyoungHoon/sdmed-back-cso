package sdmed.back.model.sqlCSO.edi

import java.util.*

data class EDIUploadListModel(
	var thisPK: String = UUID.randomUUID().toString(),
	var userPK: String = "",
	var year: String = "",
	var month: String = "",
	var day: String = "",
	var hospitalPK: String = "",
	var orgName: String = "",
	var ediState: EDIState = EDIState.None,
	var regDate: Date = Date(),
	var name: String = "",
	var pharmaList: MutableList<EDIUploadPharmaModel> = mutableListOf(),
	var fileList: MutableList<EDIUploadFileModel> = mutableListOf(),
	var responseList: MutableList<EDIUploadResponseModel> = mutableListOf(),
) {
}