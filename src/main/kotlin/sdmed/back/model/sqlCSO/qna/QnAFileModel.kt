package sdmed.back.model.sqlCSO.qna

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class QnAFileModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var headerPK: String = "",
	@Column(columnDefinition = "nvarchar(max)")
	var blobUrl: String = "",
	@Column(columnDefinition = "nvarchar(max)")
	var originalFilename: String = "",
	@Column(columnDefinition = "nvarchar(100)")
	var mimeType: String = "",
) {
}