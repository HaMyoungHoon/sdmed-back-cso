package sdmed.back.model.common.medicine

enum class MedicineDiv(var index: Int, var desc: String) {
	Open(0, "공개"),
	Close(1, "비공개");

	companion object {
		fun parseString(data: String?) = entries.find { it.desc == data } ?: Open
	}
}