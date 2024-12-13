package sdmed.back.model.common.medicine

enum class MedicineType(var index: Int, var desc: String) {
	General(0, "일반"),
	Expert(1, "전문");

	companion object {
		fun parseString(data: String?) = entries.find { it.desc == data } ?: General
	}
}