package sdmed.back.service

import com.azure.core.util.BinaryData
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import com.azure.storage.blob.models.BlobHttpHeaders
import com.azure.storage.blob.sas.BlobSasPermission
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import sdmed.back.config.FExtensions
import sdmed.back.model.sqlCSO.BlobUploadModel
import sdmed.back.repository.sqlCSO.IBlobUploadRepository
import java.time.OffsetDateTime
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
		val blobUrl = "$today/Test?${file.originalFilename}"
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
	fun uploadString(content: String, section: String, uploaderPK: String): String {
		val blobUrl = "$section/${UUID.randomUUID()}.html"
		val containerClient = blobServiceClient.createBlobContainerIfNotExists(containerName)
		val blobClient = containerClient.getBlobClient(blobUrl)
		blobClient.upload(BinaryData.fromString(content))
		blobUploadRepository.save(BlobUploadModel().apply {
			this.blobUrl = blobUrl
			this.uploaderPK = uploaderPK
			this.originalFilename = "${section}.html"
			this.mimeType = "application/octet-stream"
		})
		return blobClient.blobUrl
	}

	fun generateSas(containerName: String, blobUrl: String): String {
		val containerClient = if (containerName.isBlank()) {
			blobServiceClient.createBlobContainerIfNotExists(this.containerName)
		} else {
			blobServiceClient.createBlobContainerIfNotExists(containerName)
		}
		val blobClient = containerClient.getBlobClient(blobUrl)
		val expiryTime = OffsetDateTime.now().plusHours(10)
		val sasPermission = BlobSasPermission().apply { setWritePermission(true) }
		return blobClient.generateSas(BlobServiceSasSignatureValues(expiryTime, sasPermission))
//		return "${blobClient.blobUrl}?$sasToken"
	}
	fun blobUploadSave(blobModel: BlobUploadModel) = blobUploadRepository.save(blobModel)
}