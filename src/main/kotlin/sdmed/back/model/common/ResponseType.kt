package sdmed.back.model.common

enum class ResponseType(val index: Int) {
	None(0),
	Recep(1),
	OK(2),
	Pending(3),
	Ignore(4),
	Reject(5),
}