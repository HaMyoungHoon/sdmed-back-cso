package sdmed.back.model.common.medicine

enum class MedicineMethod(var index: Int, var desc: String) {
	ETC(0, "기타"),
	Oral(1, "내복"),
	External(2, "외용"),
	Inject(3, "주사"),
	Dental(4, "치과");

	companion object {
		fun parseString(data: String?) = entries.find { it.desc == data } ?: ETC
	}
}