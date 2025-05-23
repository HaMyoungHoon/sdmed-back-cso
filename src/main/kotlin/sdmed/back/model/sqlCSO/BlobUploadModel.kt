package sdmed.back.model.sqlCSO

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class BlobUploadModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	// mysql
	@Column(columnDefinition = "text")
//	@Column(columnDefinition = "nvarchar(max)")
	var blobUrl: String = "",
	@Column(columnDefinition = "nvarchar(36)", updatable = false)
	var uploaderPK: String = "",
	// mysql
	@Column(columnDefinition = "text")
//	@Column(columnDefinition = "nvarchar(max)")
	var originalFilename: String = "",
	@Column(columnDefinition = "nvarchar(100)")
	var mimeType: String = "",
	@Column
	var regDate: Date = Date()
	) {

	fun newSave(): BlobUploadModel {
		thisPK = UUID.randomUUID().toString()
		return this
	}
}