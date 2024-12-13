package sdmed.back.model.common

enum class ResponseType(val index: Int) {
	None(0),
	OK(1),
	Pending(2),
	Ignore(3),
	Reject(4),
}