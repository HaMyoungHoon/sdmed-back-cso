package sdmed.back.model.sqlCSO.edi

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class EDIUploadFileModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var ediPK: String = "",
	// mysql
	@Column(columnDefinition = "text")
//	@Column(columnDefinition = "nvarchar(max)")
	var blobUrl: String = "",
	// mysql
	@Column(columnDefinition = "text")
//	@Column(columnDefinition = "nvarchar(max)")
	var originalFilename: String = "",
	@Column(columnDefinition = "nvarchar(100)")
	var mimeType: String = "",
	@Column
	var regDate: Date = Date(),
	@Column
	var inVisible: Boolean = false
) {
}