package sdmed.back.model.sqlCSO.edi

enum class EDIType(var index: Int) {
	DEFAULT(0),
	NEW(1),
	TRANSFER(2)
}