package sdmed.back.model.sqlCSO.user

enum class UserFileType(var index: Int) {
	Taxpayer(1),
	BankAccount(2),
	CsoReport(3),
	MarketingContract(4),
}