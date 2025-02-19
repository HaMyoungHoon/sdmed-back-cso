package sdmed.back.model.sqlCSO.edi

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.model.sqlCSO.FExcelParseModel
import java.util.*

@Entity
data class EDIPharmaDueDateModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var pharmaPK: String = "",
	@Column(columnDefinition = "nvarchar(255)", updatable = false, nullable = false)
	var orgName: String = "",
	@Column(columnDefinition = "nvarchar(4)", nullable = false)
	var year: String = "",
	@Column(columnDefinition = "nvarchar(2)", nullable = false)
	var month: String = "",
	@Column(columnDefinition = "nvarchar(2)", nullable = false)
	var day: String = "",
	@Column
	var regDate: Date = Date(),
	@Transient
	var pharmaCode: String = ""
): FExcelParseModel() {
	@Transient
	override var dataCount = FConstants.MODEL_EDI_DUE_DATE_COUNT
	override fun indexSet(data: String?, index: Int) {
		when (index) {
			0 -> yyyyMMddSet(data)
			1 -> pharmaCode = data ?: ""
		}
	}
	override fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_EDI_DUE_DATE_DATE
			1 -> FConstants.MODEL_EDI_DUE_DATE_PHARMA_CODE
			else -> ""
		}
	}
	override fun errorCondition(): Boolean {
		if (pharmaCode.isBlank()) return true
		if (year.isBlank()) return true
		if (month.isBlank()) return true
		if (day.isBlank()) return true
		return false
	}
	override fun errorString() = "${FConstants.MODEL_EDI_DUE_DATE_DATE} : ${year}-${month}-${day}\n${FConstants.MODEL_EDI_DUE_DATE_PHARMA_CODE} : ${pharmaCode}"
	fun insertString(): String {
		val regDate = FExtensions.parseDateTimeString(this.regDate, "yyyy-MM-dd")
		return "('$thisPK', '$pharmaPK', '$orgName', '$year', '$month', '$day', '$regDate')"
	}

	fun yyyyMMddSet(data: String?) {
		data ?: return
		val date = FExtensions.parseStringToJavaDate(data, "yyyy-MM-dd")
		year = FExtensions.parseDateTimeString(date, "yyyy") ?: ""
		month = FExtensions.parseDateTimeString(date, "MM") ?: ""
		day = FExtensions.parseDateTimeString(date, "dd") ?: ""
	}
}