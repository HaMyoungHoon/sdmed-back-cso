package sdmed.back.model.sqlCSO.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class UserChildPKModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)")
	var motherPK: String = "",
	@Column(columnDefinition = "nvarchar(36)")
	var childPK: String = "",
) {

	fun insertString(): String {
		return "('$thisPK', '$motherPK', '$childPK')"
	}
}