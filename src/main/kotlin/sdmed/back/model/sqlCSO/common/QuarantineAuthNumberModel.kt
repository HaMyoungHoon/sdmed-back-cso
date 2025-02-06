package sdmed.back.model.sqlCSO.common

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class QuarantineAuthNumberModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(255)", updatable = false, nullable = false, unique = true)
	var userPK: String = "",
	@Column
	var regDate: Date = Date()
) {
}