package sdmed.back.model.common

enum class RequestType(var index: Int) {
	SignUp(1),
	EDIUpload(2),
	QnA(3),
}