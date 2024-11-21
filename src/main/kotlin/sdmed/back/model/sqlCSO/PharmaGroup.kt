package sdmed.back.model.sqlCSO

enum class PharmaGroup(var index: Int, var desc: String) {
	None(0, "미지정"),
	Recipient(1, "공급받는자"),
	Supplier(2, "공급사"),
	ETC(3, "기타"),
	Pharmaceutical(4, "정산제약사");

	companion object {
		fun parseString(data: String?) = entries.find { it.desc == data } ?: None
	}
}