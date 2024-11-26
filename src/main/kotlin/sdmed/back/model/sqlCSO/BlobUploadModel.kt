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
	@Column(columnDefinition = "nvarchar(500)")
	var blobUrl: String = "",
	@Column(columnDefinition = "nvarchar(36)", updatable = false)
	var uploaderPK: String = "",
	@Column(columnDefinition = "nvarchar(255)")
	var originalFilename: String = "",
	@Column(columnDefinition = "nvarchar(100)")
	var mimeType: String = ""
	) {
}