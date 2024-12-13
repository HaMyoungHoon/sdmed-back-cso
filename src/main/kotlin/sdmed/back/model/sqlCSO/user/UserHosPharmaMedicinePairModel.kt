package sdmed.back.model.sqlCSO.user

import java.util.*

data class UserHosPharmaMedicinePairModel(
	var thisPK: String = UUID.randomUUID().toString(),
	var userPK: String = "",
	var hosPK: String = "",
	var pharmaPK: String = "",
	var medicinePK: String = ""
) {
	fun insertString() = "('$thisPK', '$userPK', '$hosPK', '$pharmaPK', '$medicinePK')"
}