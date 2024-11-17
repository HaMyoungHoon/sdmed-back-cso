package sdmed.back.model.common

enum class BillType(var index: Int, var desc: String) {
	None(0, "미지정"),
	Unpublished(1, "미발행"),
	Unit(2, "건발행"),
	Monthly(3, "월발행");

	companion object {
		fun parseString(data: String?) = entries.find { it.desc == data } ?: None
	}
}