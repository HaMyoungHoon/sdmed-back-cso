package sdmed.back.model.sqlCSO.common

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class VersionCheckModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(updatable = false, nullable = false)
	var versionCheckType: VersionCheckType = VersionCheckType.PC,
	@Column(updatable = false, nullable = false)
	var latestVersion: String = "",
	@Column(updatable = false, nullable = false)
	var minorVersion: String = "",
	@Column(updatable = true, nullable = false)
	var able: Boolean = true,
	@Column(updatable = false, nullable = false)
	var regDate: Date = Date(),
) {
}