package sdmed.back.model.sqlCSO

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.sql.Timestamp
import java.util.*

@Entity
data class LogModel(
	@Id
	@Column(updatable = false, nullable = false)
	var thisIndex: Long = 0,
	@Column(name = "this_index", updatable = false)
	@get:JsonProperty("this_index")
	var userIndex: Long? = null,
	@Column(columnDefinition = "nvarchar(255)", updatable = false)
	var className: String = "",
	@Column(columnDefinition = "nvarchar(255)", updatable = false)
	var funcName: String = "",
	@Column(columnDefinition = "nvarchar(512)", updatable = false)
	var content: String = "",
	@Column(updatable = false, nullable = false)
	var regDate: Timestamp = Timestamp(Date().time)
) {
	fun build(user: Long?, className: String, func: String, content: String): LogModel {
		userIndex = user
		this.className = className
		funcName = func
		this.content = content
		return this
	}
}