package sdmed.back.model.sqlCSO

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.sql.Timestamp
import java.util.*

@Entity
data class LogModel(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	var thisIndex: Long = 0,
	@Column(updatable = false)
	var userPK: String? = null,
	@Column(columnDefinition = "nvarchar(255)", updatable = false)
	var className: String = "",
	@Column(columnDefinition = "nvarchar(255)", updatable = false)
	var funcName: String = "",
	// mysql
	@Column(columnDefinition = "text", updatable = false)
//	@Column(columnDefinition = "nvarchar(max)", updatable = false)
	var content: String = "",
	@Column(updatable = false, nullable = false)
	var regDate: Timestamp = Timestamp(Date().time)
) {
	fun build(user: String?, className: String, func: String, content: String): LogModel {
		userPK = user
		this.className = className
		funcName = func
		this.content = content
		return this
	}
}