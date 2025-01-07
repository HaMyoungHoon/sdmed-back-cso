package sdmed.back.model.sqlCSO.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class UserFileModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var userPK: String = "",
	@Column(columnDefinition = "nvarchar(max)")
	var blobUrl: String = "",
	@Column(columnDefinition = "nvarchar(max)")
	var originalFilename: String = "",
	@Column(columnDefinition = "nvarchar(100)")
	var mimeType: String = "",
	@Column
	var userFileType: UserFileType = UserFileType.Taxpayer
) {
}