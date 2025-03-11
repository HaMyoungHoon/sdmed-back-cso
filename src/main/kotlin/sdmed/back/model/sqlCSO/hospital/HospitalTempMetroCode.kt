package sdmed.back.model.sqlCSO.hospital

enum class HospitalTempMetroCode(var code: Int, var desc: String) {
	CODE_000000(0, "미지정"),
	CODE_110000(110000, "서울"),
	CODE_210000(210000, "부산"),
	CODE_220000(220000, "인천"),
	CODE_230000(230000, "대구"),
	CODE_240000(240000, "광주"),
	CODE_250000(250000, "대전"),
	CODE_260000(260000, "울산"),
	CODE_310000(310000, "경기"),
	CODE_320000(320000, "강원"),
	CODE_330000(330000, "충북"),
	CODE_340000(340000, "충남"),
	CODE_350000(350000, "전북"),
	CODE_360000(360000, "전남"),
	CODE_370000(370000, "경북"),
	CODE_380000(380000, "경남"),
	CODE_390000(390000, "제주"),
	CODE_410000(410000, "세종");
	companion object {
		fun parseCode(code: Int?) = entries.find { x -> x.code == code } ?: CODE_000000
	}
}