package sdmed.back.model.sqlCSO.edi

enum class EDIApplyDateState(var index: Int) {
	None(0),
	Use(1),
	Expired(2),
}