package sdmed.back.model.common

enum class CorrespondentDiv(var index: Int, var desc: String) {
	None(0, "미지정"),
	Purchase(1, "매입처"),
	Sales(2, "매출처"),
	Additional(3, "부가코드"),
	Bank(4, "은행처"),
	Manufacturing(5, "제조사"),
	Quitter(6, "퇴사자"),
	Accounting(7, "회계처")
}