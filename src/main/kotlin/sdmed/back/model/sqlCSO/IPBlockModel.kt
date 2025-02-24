package sdmed.back.model.sqlCSO

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.Date

@Entity
data class IPBlockModel(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	var thisPK: Long = 0,
	@Column(columnDefinition = "nvarchar(100)")
	var ipAddr: String = "255.255.255.255",
	@Column
	var isBlock: Boolean = false,
	@Column
	var etcData: String? = null,
	@Column
	var regDate: Date = Date()
) {
}