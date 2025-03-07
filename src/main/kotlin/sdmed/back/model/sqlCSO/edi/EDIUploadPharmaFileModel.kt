package sdmed.back.model.sqlCSO.edi

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class EDIUploadPharmaFileModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var ediPharmaPK: String = "",
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var pharmaPK: String = "",
	@Column(columnDefinition = "text")
	var blobUrl: String = "",
	@Column(columnDefinition = "text")
	var originalFilename: String = "",
	@Column(columnDefinition = "nvarchar(100)")
	var mimeType: String = "",
	@Column
	var regDate: Date = Date(),
	@Column
	var inVisible: Boolean = false
	) {
	fun initThisPK(ediPharmaPK: String) {
		thisPK = UUID.randomUUID().toString()
		this.ediPharmaPK = ediPharmaPK
	}
}