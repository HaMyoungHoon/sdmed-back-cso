package sdmed.back.model.common.medicine

enum class MedicineStorageTemp(var index: Int, var desc: String) {
	RoomTemp(0, "실온보관"),
	Cold(1, "냉장보관");

	companion object {
		fun parseString(data: String?) = entries.find { it.desc == data } ?: RoomTemp
	}
}