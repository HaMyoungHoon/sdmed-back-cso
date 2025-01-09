package sdmed.back.model.sqlCSO

data class BlobStorageInfoModel(
	var blobUrl: String = "",
	var blobContainerName: String = "",
	var sasKey: String = ""
) {
}