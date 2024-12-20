package sdmed.back.model.sqlCSO.edi

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class EDIPharmaDueDateModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var pharmaPK: String = "",
	@Column(columnDefinition = "nvarchar(255)", updatable = false, nullable = false)
	var orgName: String = "",
	@Column(columnDefinition = "nvarchar(4)", updatable = false, nullable = false)
	var year: String = "",
	@Column(columnDefinition = "nvarchar(2)", updatable = false, nullable = false)
	var month: String = "",
	@Column(columnDefinition = "nvarchar(2)", updatable = false, nullable = false)
	var day: String = "",
	@Column
	var regDate: Date = Date()
) {
}