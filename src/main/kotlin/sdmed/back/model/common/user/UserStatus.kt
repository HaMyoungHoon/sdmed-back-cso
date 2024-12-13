package sdmed.back.model.common.user

enum class UserStatus(var index: Int, var desc: String) {
	None(0, "미지정"),
	Live(1, "라이브"),
	Stop(2, "중지됨"),
	Delete(3, "삭제됨"),
	Expired(4, "만료됨");

	companion object {
		fun parseString(data: String?) = entries.find { it.desc == data } ?: None
	}
}