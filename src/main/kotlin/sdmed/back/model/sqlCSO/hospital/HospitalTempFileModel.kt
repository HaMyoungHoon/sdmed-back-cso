package sdmed.back.model.sqlCSO.hospital

import jakarta.persistence.*
import java.util.*

@Entity
data class HospitalTempFileModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var hospitalTempPK: String = "",
	@Column(columnDefinition = "text")
	var blobUrl: String = "",
	@Column(columnDefinition = "text")
	var originalFilename: String = "",
	@Column(columnDefinition = "nvarchar(100)")
	var mimeType: String = "",
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "nvarchar(20)")
	var fileType: HospitalTempFileType = HospitalTempFileType.TAXPAYER,
	@Column
	var regDate: Date = Date(),
) {
}