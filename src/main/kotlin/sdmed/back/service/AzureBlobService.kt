package sdmed.back.service

import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import com.azure.storage.blob.models.BlobHttpHeaders
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import sdmed.back.config.FExtensions
import sdmed.back.model.sqlCSO.BlobUploadModel
import sdmed.back.repository.sqlCSO.IBlobUploadRepository
import java.util.*

@Service
class AzureBlobService {
	@Value(value = "\${str.blob.containerName}") var containerName: String = ""
	@Value(value = "\${str.blob.connectionString}") var connectionString: String = ""
	@Autowired lateinit var blobUploadRepository: IBlobUploadRepository

	val blobServiceClient: BlobServiceClient by lazy {
		BlobServiceClientBuilder()
			.connectionString(connectionString)
			.buildClient()
	}

	fun uploadTest(file: MultipartFile): String {
		val today = FExtensions.getDateTimeString("yyyyMMdd")
		val blobUrl = "$today/${UUID.randomUUID()}_${file.originalFilename}"
		val containerClient = blobServiceClient.createBlobContainerIfNotExists(containerName)
		val blobClient = containerClient.getBlobClient(blobUrl)
		blobClient.upload(file.inputStream, file.size, true)
		blobClient.setHttpHeaders(BlobHttpHeaders().setContentType(FExtensions.detectFileMimeType(file)))
		return blobClient.blobUrl
	}

	fun uploadFile(file: MultipartFile, section: String, uploaderPK: String): String {
		val blobUrl = "$section/${UUID.randomUUID()}.${FExtensions.getFileExt(file)}"
		val mimeType = FExtensions.detectFileMimeType(file)
		val containerClient = blobServiceClient.createBlobContainerIfNotExists(containerName)
		val blobClient = containerClient.getBlobClient(blobUrl)
		blobClient.upload(file.inputStream, file.size, true)
		blobClient.setHttpHeaders(BlobHttpHeaders().setContentType(mimeType))
		blobUploadRepository.save(BlobUploadModel().apply {
			this.blobUrl = blobUrl
			this.uploaderPK = uploaderPK
			this.originalFilename = file.originalFilename ?: ""
			this.mimeType = mimeType
		})
		return blobClient.blobUrl
	}
}