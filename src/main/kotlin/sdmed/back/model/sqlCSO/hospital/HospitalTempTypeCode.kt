package sdmed.back.model.sqlCSO.hospital

enum class HospitalTempTypeCode(var code: String, var desc: String) {
	CODE_00("00", "미지정"),
	CODE_01("01", "상급종합병원"),
	CODE_11("11", "종합병원"),
	CODE_21("21", "병원"),
	CODE_28("28", "요양병원"),
	CODE_29("29", "정신병원"),
	CODE_31("31", "의원"),
	CODE_41("41", "치과병원"),
	CODE_51("51", "치과의원"),
	CODE_61("61", "조산원"),
	CODE_71("71", "보건소"),
	CODE_72("72", "보건지소"),
	CODE_73("73", "보건진료소"),
	CODE_74("74", "모자보건센타"),
	CODE_75("75", "보건의료원"),
	CODE_81("81", "약국"),
	CODE_91("91", "한방종합병원"),
	CODE_92("92", "한방병원"),
	CODE_93("93", "한의원"),
	CODE_94("94", "한약방"),
	CODE_AA("AA", "병의원");
	companion object {
		fun parseCode(code: String?) = entries.find { x -> x.code == code } ?: CODE_00
	}
}