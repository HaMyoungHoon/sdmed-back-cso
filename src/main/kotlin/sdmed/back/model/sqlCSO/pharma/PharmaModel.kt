package sdmed.back.model.sqlCSO.pharma

import jakarta.persistence.*
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.model.sqlCSO.FExcelParseModel
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import java.sql.Date
import java.util.*

@Entity
data class PharmaModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(50)", nullable = false, unique = true)
	var code: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var innerName: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var orgName: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var ownerName: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var taxpayerNumber: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var address: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var phoneNumber: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var faxNumber: String = "",
	@Column(columnDefinition = "nvarchar(100)", nullable = false)
	var zipCode: String = "",
	@Column
	var openDate: Date? = null,
	@Column(columnDefinition = "text", nullable = false)
	var retroactiveRule: String = "",
	@Column(columnDefinition = "text", nullable = false)
	var innerSettlementRule: String = "",
	@Column(columnDefinition = "text", nullable = false)
	var outerSettlementRule: String = "",
	@Column(columnDefinition = "text", nullable = false)
	var etc1: String = "",
	@Column(columnDefinition = "text", nullable = false)
	var etc2: String = "",
	@Column(columnDefinition = "text", nullable = false)
	var imageUrl: String = "",
	@Column(columnDefinition = "bit default 0", nullable = false)
	var inVisible: Boolean = false,
	@Transient
	var medicineList: MutableList<MedicineModel> = mutableListOf(),
	@Transient
	var relationMedicineList: MutableList<MedicineModel> = mutableListOf()
): FExcelParseModel() {
	@Transient
	override var dataCount = FConstants.MODEL_PHARMA_COUNT
	fun indexGet(index: Int): String {
		return when (index) {
			0 -> code.toString()
			1 -> innerName
			2 -> orgName
			3 -> ownerName
			4 -> taxpayerNumber
			5 -> address
			6 -> phoneNumber
			7 -> faxNumber
			8 -> zipCode
			9 -> FExtensions.parseDateTimeString(openDate, "yyyy-MM-dd")
			10 -> retroactiveRule
			11 -> innerSettlementRule
			12 -> outerSettlementRule
			13 -> etc1
			14 -> etc2
			else -> ""
		}
	}
	override fun indexSet(data: String?, index: Int) {
		when (index) {
			0 -> code = data ?: ""
			1 -> innerName = data ?: ""
			2 -> orgName = data ?: ""
			3 -> ownerName = data ?: ""
			4 -> taxpayerNumber = data ?: ""
			5 -> phoneNumber = data ?: ""
			6 -> faxNumber = data ?: ""
			7 -> zipCode = data ?: ""
			8 -> address = data ?: ""
			9 -> openDate = if (data.isNullOrEmpty()) null else FExtensions.parseStringToSqlDate(data, "yyyyMMdd")
			10 -> retroactiveRule = data ?: ""
			11 -> innerSettlementRule = data ?: ""
			12 -> outerSettlementRule = data ?: ""
			13 -> etc1 = data ?: ""
			14 -> etc2 = data ?: ""
		}
	}
	override fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_CODE
			1 -> FConstants.MODEL_INNER_NAME
			2 -> FConstants.MODEL_ORG_NAME
			3 -> FConstants.MODEL_OWNER_NAME
			4 -> FConstants.MODEL_TAX_PAYER
			5 -> FConstants.MODEL_ADDRESS
			6 -> FConstants.MODEL_PHONE
			7 -> FConstants.MODEL_FAX
			8 -> FConstants.MODEL_ZIP_CODE
			9 -> FConstants.MODEL_OPEN_DATE
			10 -> FConstants.MODEL_RETROACTIVE_RULE
			11 -> FConstants.MODEL_INNER_SETTLEMENT_RULE
			12 -> FConstants.MODEL_OUTER_SETTLEMENT_RULE
			13 -> FConstants.MODEL_ETC1
			14 -> FConstants.MODEL_ETC2
			else -> ""
		}
	}
	override fun errorCondition(): Boolean {
		if (indexGet(0).isBlank()) {
			return true
		} else if (indexGet(1).isEmpty()) {
			return true
		} else if (indexGet(2).isEmpty()) {
			return true
		}
		return false
	}
	override fun errorString(): String {
		val ret = StringBuilder()
		for (i in 0 until FConstants.MODEL_PHARMA_COUNT) {
			ret.append("${titleGet(i)} : ${indexGet(i)}\n")
		}
		return ret.toString()
	}
	fun insertString(): String {
		val innerName = FExtensions.escapeString(innerName)
		val orgName = FExtensions.escapeString(orgName)
		val ownerName = FExtensions.escapeString(ownerName)
		val taxpayerNumber = FExtensions.escapeString(taxpayerNumber)
		val phoneNumber = FExtensions.escapeString(phoneNumber)
		val faxNumber = FExtensions.escapeString(faxNumber)
		val zipCode = FExtensions.escapeString(zipCode)
		val address = FExtensions.escapeString(address)
		val openDateString: String = openDate?.let { "'${FExtensions.parseDateTimeString(it, "yyyy-MM-dd")}'" } ?: "null"
		val retroactiveRule = FExtensions.escapeString(retroactiveRule)
		val innerSettlementRule = FExtensions.escapeString(innerSettlementRule)
		val outerSettlementRule = FExtensions.escapeString(outerSettlementRule)
		val etc1 = FExtensions.escapeString(etc1)
		val etc2 = FExtensions.escapeString(etc2)
		return "('$thisPK', '$code', '$innerName', '$orgName', '$ownerName', '$taxpayerNumber', '$address', '$phoneNumber', '$faxNumber', '$zipCode', $openDateString, '$retroactiveRule', '$innerSettlementRule', '$outerSettlementRule', '$etc1', '$etc2', '$imageUrl', ${if (inVisible) 1 else 0})"
	}
	fun safeCopy(rhs: PharmaModel): PharmaModel {
		this.innerName = rhs.innerName
		this.orgName = rhs.orgName
		this.ownerName = rhs.ownerName
		this.taxpayerNumber = rhs.taxpayerNumber
		this.phoneNumber = rhs.phoneNumber
		this.faxNumber = rhs.faxNumber
		this.zipCode = rhs.zipCode
		this.address = rhs.address
		this.openDate = rhs.openDate
		this.retroactiveRule = rhs.retroactiveRule
		this.innerSettlementRule = rhs.innerSettlementRule
		this.outerSettlementRule = rhs.outerSettlementRule
		this.etc1 = rhs.etc1
		this.etc2 = rhs.etc2
		return this
	}
	fun clone(): PharmaModel {
		return PharmaModel(thisPK,
			code,
			innerName,
			orgName,
			ownerName,
			taxpayerNumber,
			address,
			phoneNumber,
			faxNumber,
			zipCode,
			openDate,
			retroactiveRule,
			innerSettlementRule,
			outerSettlementRule,
			etc1,
			etc2,
			imageUrl,
			inVisible,
			medicineList.toMutableList(),
			relationMedicineList.toMutableList()).apply {
		}
	}
}