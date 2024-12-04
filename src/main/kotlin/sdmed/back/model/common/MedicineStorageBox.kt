package sdmed.back.model.common

enum class MedicineStorageBox(var index: Int, var desc: String) {
	Confidential(0, "기밀"),
	Sealed(1, "밀봉");

	companion object {
		fun parseString(data: String?) = entries.find { it.desc == data } ?: Confidential
	}
}