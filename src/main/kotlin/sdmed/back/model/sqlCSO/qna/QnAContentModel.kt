package sdmed.back.model.sqlCSO.qna

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class QnAContentModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var headerPK: String = "",
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var userPK: String = "",
	// mysql
	@Column(columnDefinition = "text", updatable = false, nullable = false)
//	@Column(columnDefinition = "nvarchar(max)", updatable = false, nullable = false)
	var content: String = "",
	@Transient
	var fileList: MutableList<QnAFileModel> = mutableListOf(),
	@Transient
	var replyList: MutableList<QnAReplyModel> = mutableListOf()
) {
}