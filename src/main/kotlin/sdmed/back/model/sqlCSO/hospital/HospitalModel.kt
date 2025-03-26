package sdmed.back.model.sqlCSO.hospital

import jakarta.persistence.*
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.model.sqlCSO.FExcelParseModel
import sdmed.back.model.sqlCSO.pharma.PharmaModel
import java.util.*

@Entity
data class HospitalModel(
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
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var businessType: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var businessItem: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var nursingHomeNumber: String = "",
	@Column(columnDefinition = "text", nullable = false)
	var etc1: String = "",
	@Column(columnDefinition = "text", nullable = false)
	var etc2: String = "",
	@Column(columnDefinition = "text", nullable = false)
	var imageUrl: String = "",
	@Column(columnDefinition = "bit default 0", nullable = false)
	var inVisible: Boolean = false,
	@Transient
	var pharmaList: MutableList<PharmaModel> = mutableListOf()
): FExcelParseModel() {
	@Transient
	override var dataCount = FConstants.MODEL_HOS_COUNT
	fun indexGet(index: Int): String {
		return when (index) {
			0 -> code
			1 -> innerName
			2 -> orgName
			3 -> ownerName
			4 -> taxpayerNumber
			5 -> address
			6 -> phoneNumber
			7 -> faxNumber
			8 -> zipCode
			9 -> businessType
			10 -> businessItem
			11 -> nursingHomeNumber
			12 -> etc1
			13 -> etc2
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
			5 -> address = data ?: ""
			6 -> phoneNumber = data ?: ""
			7 -> faxNumber = data ?: ""
			8 -> zipCode = data ?: ""
			9 -> businessType = data ?: ""
			10 -> businessItem = data ?: ""
			11 -> nursingHomeNumber = data ?: ""
			12 -> etc1 = data ?: ""
			13 -> etc2 = data ?: ""
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
			9 -> FConstants.MODEL_BIZ_TYPE
			10 -> FConstants.MODEL_BIZ_ITEM
			11 -> FConstants.MODEL_NURSING_HOME_NUMBER
			21 -> FConstants.MODEL_ETC1
			22 -> FConstants.MODEL_ETC2
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
//		} else if (getIndex(3).isEmpty()) {
//			return true
//		} else if (getIndex(4).isEmpty()) {
//			return true
//		} else if (getIndex(7).isEmpty()) {
//			return true
//		} else if (getIndex(8).isEmpty()) {
//			return true
		}
		return false
	}
	override fun errorString(): String {
		val ret = StringBuilder()
		for (i in 0 until FConstants.MODEL_HOS_COUNT) {
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
		val businessType = FExtensions.escapeString(businessType)
		val businessItem = FExtensions.escapeString(businessItem)
		val nursingHomeNumber = FExtensions.escapeString(nursingHomeNumber)
		val etc1 = FExtensions.escapeString(etc1)
		val etc2 = FExtensions.escapeString(etc2)
		return "('$thisPK', '$code', '$innerName', '$orgName', '$ownerName', '$taxpayerNumber', '$address', '$phoneNumber', '$faxNumber', '$zipCode', '$businessType', '$businessItem', '$nursingHomeNumber', '$etc1', '$etc2', '$imageUrl', ${if (inVisible) 1 else 0})"
	}
	fun safeCopy(rhs: HospitalModel): HospitalModel {
		this.innerName = rhs.innerName
		this.orgName = rhs.orgName
		this.ownerName = rhs.ownerName
		this.taxpayerNumber = rhs.taxpayerNumber
		this.address = rhs.address
		this.phoneNumber = rhs.phoneNumber
		this.faxNumber = rhs.faxNumber
		this.zipCode = rhs.zipCode
		this.businessType = rhs.businessType
		this.businessItem = rhs.businessItem
		this.nursingHomeNumber = rhs.nursingHomeNumber
		this.etc1 = rhs.etc1
		this.etc2 = rhs.etc2
		this.imageUrl = rhs.imageUrl
		this.inVisible = rhs.inVisible
		return this
	}
}