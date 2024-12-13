package sdmed.back.model.common.medicine

enum class MedicineCategory(var index: Int, var desc: String) {
	ETC(0, "기타"),
	HighRisk(1, "오남용우려약품"),
	Psychotropic(2, "향정신성의약품");

	companion object {
		fun parseString(data: String?) = entries.find { it.desc == data } ?: ETC
	}
}