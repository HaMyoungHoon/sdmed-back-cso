package sdmed.back.model.sqlCSO.qna

enum class QnAState(var index: Int) {
	None(0),
	OK(1),
	Recep(2),
	Reply(3),
}