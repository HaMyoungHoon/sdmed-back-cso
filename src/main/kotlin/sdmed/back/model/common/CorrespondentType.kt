package sdmed.back.model.common

enum class CorrespondentType(var index: Int, var desc: String) {
	None(0, "미지정"),
	Donate(1, "기부"),
	ETC(2, "기타"),
	FarmersBank(3, "농협"),
	Wholesale(4, "도매업체"),
	LaonPharm(5, "라온팜 제뉴원사이언스"),
	Hospital(6, "병의원"),
	Pharmacy(7, "약국"),
	MedicalEq(8, "의료기"),
	GeneralHospital(9, "종합병원"),
	Pharmaceutical(10, "통계제약사"),
	SpecialInst(11, "특수의료시설및학술기관"),
	SalesAgencyP(12, "판매대행(개인)"),
	SalesAgencyC(13, "판매대행(사업자)")
}