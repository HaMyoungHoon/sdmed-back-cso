package sdmed.back.model.sqlCSO.qna

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

@Entity
data class QnAHeaderModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var userPK: String = "",
	@Column(columnDefinition = "nvarchar(255)", updatable = false, nullable = false)
	var id: String = "",
	@Column(columnDefinition = "nvarchar(255)", updatable = false, nullable = false)
	var name: String = "",
	@Column(columnDefinition = "nvarchar(100)", updatable = false, nullable = false)
	var title: String = "",
	@Column
	var regDate: Date = Date(),
	@Column
	var qnaState: QnAState = QnAState.None,
) {
}