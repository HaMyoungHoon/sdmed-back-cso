package sdmed.back.model.sqlCSO

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.model.common.*
import java.lang.StringBuilder
import java.sql.Date
import java.util.*

@Entity
data class PharmaModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(nullable = false)
	var code: Int = 0,
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var orgName: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var innerName: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var ownerName: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var taxpayerNumber: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var phoneNumber: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var faxNumber: String = "",
	@Column(columnDefinition = "nvarchar(100)", nullable = false)
	var zipCode: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var address: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var addressDetail: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var businessType: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var businessItem: String = "",
	@Column(nullable = false)
	var billType: BillType = BillType.None,
	@Column(nullable = false)
	var pharmaType: PharmaType = PharmaType.None,
	@Column(nullable = false)
	var pharmaGroup: PharmaGroup = PharmaGroup.None,
	@Column(nullable = false)
	var contractType: ContractType = ContractType.None,
	@Column(nullable = false)
	var deliveryDiv: DeliveryDiv = DeliveryDiv.None,
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var mail: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var mobilePhone: String = "",
	@Column
	var openDate: Date? = null,
	@Column
	var closeDate: Date? = null,
	@Column(columnDefinition = "text", nullable = false)
	var etc1: String = "",
	@Column(columnDefinition = "text", nullable = false)
	var etc2: String = "",
	@Column(columnDefinition = "text", nullable = false)
	var imageUrl: String = "",
	@OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
	@JoinColumn
	@JsonManagedReference(value = "pharmaMedicineManagedReference")
	var medicineList: MutableList<MedicineModel> = mutableListOf(),
	@Transient
	var relationMedicineList: MutableList<MedicineModel> = mutableListOf(),
	) {
	fun lazyHide() {
		medicineList.onEach { it.lazyHide() }
	}
	fun ownMedicineHide() {
		medicineList = mutableListOf()
	}
	fun findHeader(data: List<String>): Boolean {
		if (data.size < FConstants.MODEL_PHARMA_COUNT) {
			return false
		}

		if (data[0] != titleGet(0) || data[1] != titleGet(1) || data[2] != titleGet(2) || data[3] != titleGet(3) ||
			data[4] != titleGet(4) || data[5] != titleGet(5) ||	data[6] != titleGet(6) || data[7] != titleGet(7) ||
			data[8] != titleGet(8) || data[9] != titleGet(9) ||	data[10] != titleGet(10) || data[11] != titleGet(11) ||
			data[12] != titleGet(12) || data[13] != titleGet(13) ||	data[14] != titleGet(14) || data[15] != titleGet(15) ||
			data[16] != titleGet(16) || data[17] != titleGet(17) ||	data[18] != titleGet(18) || data[19] != titleGet(19) ||
			data[20] != titleGet(20) || data[21] != titleGet(21) ||	data[22] != titleGet(22)) {
			return false
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
	fun indexGet(index: Int): String {
		return when (index) {
			0 -> code.toString()
			1 -> orgName
			2 -> innerName
			3 -> ownerName
			4 -> taxpayerNumber
			5 -> phoneNumber
			6 -> faxNumber
			7 -> zipCode
			8 -> address
			9 -> addressDetail
			10 -> businessType
			11 -> businessItem
			12 -> billType.desc
			13 -> pharmaType.desc
			14 -> pharmaGroup.desc
			15 -> contractType.desc
			16 -> deliveryDiv.desc
			17 -> mail
			18 -> mobilePhone
			19 -> FExtensions.parseDateTimeString(openDate, "yyyy-MM-dd")
			20 -> FExtensions.parseDateTimeString(closeDate, "yyyy-MM-dd")
			21 -> etc1
			22 -> etc2
			else -> ""
		}
	}
	fun indexSet(data: String?, index: Int) {
		when (index) {
			0 -> code = data?.toIntOrNull() ?: 0
			1 -> orgName = data ?: ""
			2 -> innerName = data ?: ""
			3 -> ownerName = data ?: ""
			4 -> taxpayerNumber = data ?: ""
			5 -> phoneNumber = data ?: ""
			6 -> faxNumber = data ?: ""
			7 -> zipCode = data ?: ""
			8 -> address = data ?: ""
			9 -> addressDetail = data ?: ""
			10 -> businessType = data ?: ""
			11 -> businessItem = data ?: ""
			12 -> billType = BillType.parseString(data)
			13 -> pharmaType = PharmaType.parseString(data)
			14 -> pharmaGroup = PharmaGroup.parseString(data)
			15 -> contractType = ContractType.parseString(data)
			16 -> deliveryDiv = DeliveryDiv.parseString(data)
			17 -> mail = data ?: ""
			18 -> mobilePhone = data ?: ""
			19 -> openDate = if (data.isNullOrEmpty()) null else FExtensions.parseStringToSqlDate(data, "yyyy-MM-dd")
			20 -> closeDate = if (data.isNullOrEmpty()) null else FExtensions.parseStringToSqlDate(data, "yyyy-MM-dd")
			21 -> etc1 = data ?: ""
			22 -> etc2 = data ?: ""
		}
	}
	fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_CODE
			1 -> FConstants.MODEL_ORG_NAME
			2 -> FConstants.MODEL_INNER_NAME
			3 -> FConstants.MODEL_OWNER_NAME
			4 -> FConstants.MODEL_TAX_PAYER
			5 -> FConstants.MODEL_PHONE
			6 -> FConstants.MODEL_FAX
			7 -> FConstants.MODEL_ZIP_CODE
			8 -> FConstants.MODEL_ADDRESS
			9 -> FConstants.MODEL_ADDRESS_DETAIL
			10 -> FConstants.MODEL_BIZ_TYPE
			11 -> FConstants.MODEL_BIZ_ITEM
			12 -> FConstants.MODEL_BILL_TYPE
			13 -> FConstants.MODEL_CO_TYPE
			14 -> FConstants.MODEL_CO_GROUP
			15 -> FConstants.MODEL_CONTRACT_TYPE
			16 -> FConstants.MODEL_DELIVERY
			17 -> FConstants.MODEL_MAIL
			18 -> FConstants.MODEL_MOBILE_PHONE
			19 -> FConstants.MODEL_OPEN_DATE
			20 -> FConstants.MODEL_CLOSE_DATE
			21 -> FConstants.MODEL_ETC1
			22 -> FConstants.MODEL_ETC2
			else -> ""
		}
	}
	fun errorCondition(): Boolean {
		if (indexGet(0) == "0") {
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
	fun errorString(): String {
		val ret = StringBuilder()
		for (i in 0 until FConstants.MODEL_PHARMA_COUNT) {
			ret.append("${titleGet(i)} : ${indexGet(i)}\n")
		}
		return ret.toString()
	}
	fun insertString(): String {
		val orgName = FExtensions.escapeString(orgName)
		val innerName = FExtensions.escapeString(innerName)
		val ownerName = FExtensions.escapeString(ownerName)
		val taxpayerNumber = FExtensions.escapeString(taxpayerNumber)
		val phoneNumber = FExtensions.escapeString(phoneNumber)
		val faxNumber = FExtensions.escapeString(faxNumber)
		val zipCode = FExtensions.escapeString(zipCode)
		val address = FExtensions.escapeString(address)
		val addressDetail = FExtensions.escapeString(addressDetail)
		val businessType = FExtensions.escapeString(businessType)
		val businessItem = FExtensions.escapeString(businessItem)
		val mail = FExtensions.escapeString(mail)
		val mobilePhone = FExtensions.escapeString(mobilePhone)
		val openDateString: String = openDate?.let { "'${FExtensions.parseDateTimeString(it, "yyyy-MM-dd")}'" } ?: "null"
		val closeDateString: String = closeDate?.let { "'${FExtensions.parseDateTimeString(it, "yyyy-MM-dd")}'" } ?: "null"
		val etc1 = FExtensions.escapeString(etc1)
		val etc2 = FExtensions.escapeString(etc2)
		return "('$thisPK', '$code', '$orgName', '$innerName', '$ownerName', '$taxpayerNumber', '$phoneNumber', '$faxNumber', '$zipCode', '$address', '$addressDetail', '$businessType', '$businessItem', '${billType.index}', '${pharmaType.index}', '${pharmaGroup.index}', '${contractType.index}', '${deliveryDiv.index}', '$mail', '$mobilePhone', $openDateString, $closeDateString, '$etc1', '$etc2', '$imageUrl')"
	}
}