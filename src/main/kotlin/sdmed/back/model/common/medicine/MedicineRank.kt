package sdmed.back.model.common.medicine

enum class MedicineRank(var index: Int, var desc: String) {
	None(0, "미지정"),
	Option1(1, "01.유지(자사생동)"),
	Option2(2, "02.유지(위탁생동)"),
	Option3(3, "03.유지(생동완료)"),
	Option4(4, "04.유지(미대상)"),
	Option5(5, "05.유지(오리지널,DMF)"),
	Option6(6, "06.유지(인하완료)"),
	Option7(7, "07.유지(사유모름)"),
	Option8(8, "08.인하"),
	Option9(8, "09.공지예정(생동진행or계획)"),
	Option10(10, "10.공지없음");

	companion object {
		fun parseString(data: String?) = entries.find { it.desc == data } ?: None
	}
}