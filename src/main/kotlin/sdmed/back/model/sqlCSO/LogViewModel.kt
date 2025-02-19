package sdmed.back.model.sqlCSO

import java.util.Date

data class LogViewModel(
	var id: String? = null,
	var content: String = "",
	var className: String = "",
	var regDate: Date = Date()
) {
}