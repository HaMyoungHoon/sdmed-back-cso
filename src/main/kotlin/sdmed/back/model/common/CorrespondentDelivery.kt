package sdmed.back.model.common

enum class CorrespondentDelivery(var index: Int, var desc: String) {
	None(0, "미지정"),
	Direct1(1, "직배1호"),
	Direct2(2, "직배2호"),
	Direct3(3, "직배3호"),
	Parcel(4, "택배")
}