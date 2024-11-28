package sdmed.back.model.sqlCSO

data class HosPharmaMedicinePairModel(
	var hosPK: String = "",
	var pharmaPK: String = "",
	var medicinePK: String = ""
) {
	fun insertString(thisPK: String, userPK: String) = "('$thisPK', '$userPK', '$hosPK', '$pharmaPK', '$medicinePK')"
}