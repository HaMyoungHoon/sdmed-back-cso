package sdmed.back.model.sqlCSO.edi

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
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
) {
	fun findHeader(data: List<String>): Boolean {
		if (data.size < FConstants.MODEL_EDI_DUE_DATE_COUNT) {
			return false
		}
		for (index in 0 until FConstants.MODEL_EDI_DUE_DATE_COUNT) {
			if (data[index] != titleGet(index)) {
				return false
			}
		}
		return true
	}
	fun rowSet(data: List<String>): Boolean? {
		if (data.size <= 1) {
			return false
		}
		return try {
			for ((index, value) in data.withIndex()) {
				indexSet(value, index)
			}
			if (errorCondition()) {
				return false
			}
			true
		} catch (_: Exception) {
			null
		}
	}
	fun indexSet(data: String?, index: Int) {
		when (index) {
			0 -> yyyyMMddSet(data)
			1 -> pharmaCode = data ?: ""
		}
	}
	fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_EDI_DUE_DATE_DATE
			1 -> FConstants.MODEL_EDI_DUE_DATE_PHARMA_CODE
			else -> ""
		}
	}
	fun errorCondition(): Boolean {
		if (pharmaCode.isBlank()) return true
		if (year.isBlank()) return true
		if (month.isBlank()) return true
		if (day.isBlank()) return true
		return false
	}
	fun errorString() = "${FConstants.MODEL_EDI_DUE_DATE_DATE} : ${year}-${month}-${day}\n${FConstants.MODEL_EDI_DUE_DATE_PHARMA_CODE} : ${pharmaCode}"
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