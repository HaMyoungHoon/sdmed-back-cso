package sdmed.back.model.common

enum class CorrespondentGroup(var index: Int, var desc: String) {
	None(0, "미지정"),
	Recipient(1, "공급받는자"),
	Supplier(2, "공급사"),
	ETC(3, "기타"),
	Pharmaceutical(4, "정산제약사"),
	Prescription(5, "처방처"),
	EDITrader(6, "EDI 거래처")
}