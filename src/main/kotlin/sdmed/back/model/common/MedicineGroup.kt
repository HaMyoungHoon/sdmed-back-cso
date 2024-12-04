package sdmed.back.model.common

enum class MedicineGroup(var index: Int, var desc: String) {
	Additional(0, "부가제품"),
	Consumable(1, "소모품"),
	Reagents(2, "시약"),
	Medicine(3, "약품"),
	NonMedicine(4, "의약외품");

	companion object {
		fun parseString(data: String?) = entries.find { it.desc == data } ?: Additional
	}
}