package sdmed.back.model.sqlCSO

data class BlobStorageInfoModel(
	var blobName: String = "",
	var blobUrl: String = "",
	var blobContainerName: String = "",
	var sasKey: String = ""
) {
}