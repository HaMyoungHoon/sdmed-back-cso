package sdmed.back.model.common

enum class ContractType(var index: Int, var desc: String) {
	None(0, "미지정"),
	Veterinary(1, "수의계약"),
	Competitive(2, "경쟁입찰")
}