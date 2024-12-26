package sdmed.back.model.sqlCSO.request

import jakarta.persistence.*
import sdmed.back.model.common.RequestType
import sdmed.back.model.common.ResponseType
import java.util.*

@Entity
data class RequestModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var requestUserPK: String = "",
	@Column(columnDefinition = "nvarchar(255)")
	var requestUserID: String = "",
	@Column(columnDefinition = "nvarchar(36)", updatable = false)
	var requestItemPK: String = "",
	@Column(columnDefinition = "nvarchar(36)")
	var responseUserPK: String = "",
	@Column(columnDefinition = "nvarchar(255)")
	var responseUserName: String = "",
	@Column(nullable = false)
	var requestType: RequestType = RequestType.SignUp,
	@Column
	var responseType: ResponseType = ResponseType.None,
	@Column(nullable = false)
	var requestDate: Date = Date(),
	@Column
	var responseDate: Date? = null,
) {
}