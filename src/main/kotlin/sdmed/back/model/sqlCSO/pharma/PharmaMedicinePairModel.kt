package sdmed.back.model.sqlCSO.pharma

data class PharmaMedicinePairModel(
	var pharmaPK: String = "",
	var medicinePK: String = "",
) {

	fun insertString(thisPK: String, userPK: String, hosPK: String) = "('$thisPK', '$userPK', '$hosPK', '$pharmaPK', '$medicinePK')"
}