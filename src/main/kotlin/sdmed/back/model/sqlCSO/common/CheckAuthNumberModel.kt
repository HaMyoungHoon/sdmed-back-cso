package sdmed.back.model.sqlCSO.common

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class CheckAuthNumberModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(255)", updatable = false, nullable = false)
	var phoneNumber: String = "",
	@Column(columnDefinition = "nvarchar(10)", updatable = false, nullable = false)
	var authNumber: String = "",
	@Column(columnDefinition = "nvarchar(255)", updatable = false, nullable = false)
	var content: String = "",
	@Column(updatable = false, nullable = false)
	var regDate: Date = Date(),
	) {
}