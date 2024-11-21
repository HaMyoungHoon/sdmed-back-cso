package sdmed.back.model.sqlCSO

enum class PharmaType(var index: Int, var desc: String) {
	None(0, "미지정"),
	ETC(1, "기타"),
	Wholesale(2, "도매업체"),
	GeneralHospital(3, "종합병원"),
	Pharmaceutical(4, "통계제약사");

	companion object {
		fun parseString(data: String?) = entries.find { it.desc == data } ?: None
	}
}