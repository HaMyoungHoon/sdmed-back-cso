package sdmed.back.model.sqlCSO.edi

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class EDIUploadResponseModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var ediPK: String = "",
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var pharmaPK: String = "",
	@Column(columnDefinition = "nvarchar(255)", updatable = false, nullable = false)
	var pharmaName: String = "",
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var userPK: String = "",
	@Column(columnDefinition = "nvarchar(255)")
	var userName: String = "",
	// mysql
	@Column(columnDefinition = "text")
//	@Column(columnDefinition = "nvarchar(max)")
	var etc: String = "",
	@Column(updatable = false, nullable = false)
	var ediState: EDIState = EDIState.None,
	@Column(updatable = false, nullable = false)
	var regDate: Date = Date(),
	) {

	fun safeCopy(data: EDIUploadResponseModel): EDIUploadResponseModel {
		this.etc = data.etc
		this.ediState = data.ediState
		return this
	}
}