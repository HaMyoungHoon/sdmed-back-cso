package sdmed.back.model.sqlCSO

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.model.common.*
import java.sql.Date

@Entity
data class CorrespondentModel(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	var thisIndex: Long = 0,
	@Column(nullable = false)
	var code: Int = 0,
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var taxpayerNumber: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var orgName: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var innerName: String = "",
	@Column
	var openDate: Date? = null,
	@Column
	var closeDate: Date? = null,
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var ownerName: String = "",
	@Column(columnDefinition = "nvarchar(100)", nullable = false)
	var zipCode: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var address: String = "",
	@Column(columnDefinition = "nvarchar(255)")
	var addressDetail: String? = null,
	@Column(columnDefinition = "nvarchar(255)")
	var businessType: String? = null,
	@Column(columnDefinition = "nvarchar(255)")
	var businessItem: String? = null,
	@Column(columnDefinition = "nvarchar(255)")
	var phoneNumber: String? = null,
	@Column(columnDefinition = "nvarchar(255)")
	var faxNumber: String? = null,
	@Column(columnDefinition = "nvarchar(255)")
	var etc1: String? = null,
	@Column(columnDefinition = "nvarchar(255)")
	var etc2: String? = null,
	@Column(columnDefinition = "nvarchar(500)")
	var taxpayerImageUrl: String? = null,
	@OneToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn
	@JsonManagedReference
	var subData: CorrespondentSubModel? = null,
) {

	fun setRows(data: List<String>): Boolean? {
		if (data.size <= 1) {
			return false
		}

		return try {
			setIndex(data[0], 0)
			setIndex(data[1], 1)
			setIndex(data[2], 2)
			setIndex(data[3], 3)
			setIndex(data[4], 4)
			setIndex(data[5], 5)
			setIndex(data[6], 6)
			setIndex(data[7], 7)
			setIndex(data[8], 8)
			setIndex(data[9], 9)
			setIndex(data[10], 10)
			setIndex(data[11], 11)
			setIndex(data[12], 12)
			setIndex(data[13], 13)
			setIndex(data[14], 14)
			setIndex(data[15], 15)
			if (errorCondition()) {
				return false
			}
			setSubData(data)
			true
		} catch (_: Exception) {
			null
		}
	}
	fun findHeader(data: List<String>): Boolean {
		if (data.size < 16) {
			return false
		}

		if (data[0] != getTitle(0) || data[1] != getTitle(1) ||
			data[2] != getTitle(0) || data[3] != getTitle(1) ||
			data[4] != getTitle(0) || data[5] != getTitle(1) ||
			data[6] != getTitle(0) || data[7] != getTitle(1) ||
			data[8] != getTitle(0) || data[9] != getTitle(1) ||
			data[10] != getTitle(0) || data[11] != getTitle(1) ||
			data[12] != getTitle(0) || data[13] != getTitle(1) ||
			data[14] != getTitle(0) || data[15] != getTitle(1) ||
			data[16] != getTitle(0) || data[17] != getTitle(1) ||
			data[18] != getTitle(0) || data[19] != getTitle(1) ||
			data[20] != getTitle(0) || data[21] != getTitle(1) ||
			data[22] != getTitle(0) || data[23] != getTitle(1) ||
			data[24] != getTitle(0)) {
			return false
		}

		return true
	}
	fun errorCondition(): Boolean {
		if (getIndex(0).isEmpty()) {
			return true
		} else if (getIndex(1).isEmpty()) {
			return true
		} else if (getIndex(2).isEmpty()) {
			return true
		}else if (getIndex(3).isEmpty()) {
			return true
		}else if (getIndex(4).isEmpty()) {
			return true
		}else if (getIndex(6).isEmpty()) {
			return true
		}else if (getIndex(7).isEmpty()) {
			return true
		}else if (getIndex(8).isEmpty()) {
			return true
		}
		return false
	}
	fun getErrorString(): String {
		if (getIndex(0) == "0") {
			return "${getTitle(0)}${FConstants.NOT_FOUND_VALUE_OR_FORMAT}"
		} else if (getIndex(1).isEmpty()) {
			return "${getTitle(1)}${FConstants.NOT_FOUND_VALUE_OR_FORMAT}"
		} else if (getIndex(2).isEmpty()) {
			return "${getTitle(2)}${FConstants.NOT_FOUND_VALUE_OR_FORMAT}"
		} else if (getIndex(3).isEmpty()) {
			return "${getTitle(3)}${FConstants.NOT_FOUND_VALUE_OR_FORMAT}"
		} else if (getIndex(4).isEmpty()) {
			return "${getTitle(4)}${FConstants.NOT_FOUND_VALUE_OR_FORMAT}"
		} else if (getIndex(6).isEmpty()) {
			return "${getTitle(6)}${FConstants.NOT_FOUND_VALUE_OR_FORMAT}"
		} else if (getIndex(7).isEmpty()) {
			return "${getTitle(7)}${FConstants.NOT_FOUND_VALUE_OR_FORMAT}"
		} else if (getIndex(8).isEmpty()) {
			return "${getTitle(8)}${FConstants.NOT_FOUND_VALUE_OR_FORMAT}"
		}

		return "${getTitle(0)} : ${getIndex(0)}\n" +
			"${getTitle(1)} : ${getIndex(1)}\n" +
			"${getTitle(2)} : ${getIndex(2)}\n" +
			"${getTitle(3)} : ${getIndex(3)}\n" +
			"${getTitle(4)} : ${getIndex(4)}\n" +
			"${getTitle(5)} : ${getIndex(5)}\n" +
			"${getTitle(6)} : ${getIndex(6)}\n" +
			"${getTitle(7)} : ${getIndex(7)}\n" +
			"${getTitle(8)} : ${getIndex(8)}\n" +
			"${getTitle(9)} : ${getIndex(9)}\n" +
			"${getTitle(10)} : ${getIndex(10)}\n" +
			"${getTitle(11)} : ${getIndex(11)}\n" +
			"${getTitle(12)} : ${getIndex(12)}\n" +
			"${getTitle(13)} : ${getIndex(13)}\n" +
			"${getTitle(14)} : ${getIndex(14)}\n" +
			"${getTitle(15)} : ${getIndex(15)}\n"
	}
	fun getIndex(index: Int): String {
		return when (index) {
			0 -> code.toString()
			1 -> taxpayerNumber
			2 -> orgName
			3 -> innerName
			4 -> FExtensions.parseDateTimeString(openDate, "yyyy-MM-dd")
			5 -> FExtensions.parseDateTimeString(closeDate, "yyyy-MM-dd")
			6 -> ownerName
			7 -> zipCode
			8 -> address
			9 -> addressDetail ?: ""
			10 -> businessType ?: ""
			11 -> businessItem ?: ""
			12 -> phoneNumber ?: ""
			13 -> faxNumber ?: ""
			14 -> etc1 ?: ""
			15 -> etc2 ?: ""
			else -> ""
		}
	}
	fun setIndex(data: String?, index: Int) {
		when (index) {
			0 -> code = data?.toIntOrNull() ?: 0
			1 -> taxpayerNumber = data ?: ""
			2 -> orgName = data ?: ""
			3 -> innerName = data ?: ""
			4 -> openDate = if (data.isNullOrEmpty()) null else FExtensions.parseStringToSqlDate(data, "yyyy-MM-dd")
			5 -> closeDate = if (data.isNullOrEmpty()) null else FExtensions.parseStringToSqlDate(data, "yyyy-MM-dd")
			6 -> ownerName = data ?: ""
			7 -> zipCode = data ?: ""
			8 -> address = data ?: ""
			9 -> addressDetail = data
			10 -> businessType = data
			11 -> businessItem = data
			12 -> phoneNumber = data
			13 -> faxNumber = data
			14 -> etc1 = data
			15 -> etc2 = data
		}
	}
	fun setSubData(data: List<String>) {
		subData = CorrespondentSubModel().apply {
			code = data[0].toIntOrNull() ?: 0
			correspondentDiv = CorrespondentDiv.parseString(data[16])
			correspondentType = CorrespondentType.parseString(data[17])
			correspondentGroup = CorrespondentGroup.parseString(data[18])
			deliveryDiv = DeliveryDiv.parseString(data[19])
			contractType = ContractType.parseString(data[20])
			billType = BillType.parseString(data[21])
			actualPrice = data[22].toBoolean()
			prepayment = data[23].toBoolean()
			transactionState = data[24].toBoolean()
			mother = this@CorrespondentModel
		}
	}
	fun getTitle(index: Int): String {
		return when (index) {
			0 -> FConstants.CO_MODEL_CODE
			1 -> FConstants.CO_MODEL_TAX_PAYER
			2 -> FConstants.CO_MODEL_ORG_NAME
			3 -> FConstants.CO_MODEL_INNER_NAME
			4 -> FConstants.CO_MODEL_OPEN_DATE
			5 -> FConstants.CO_MODEL_CLOSE_DATE
			6 -> FConstants.CO_MODEL_OWNER_NAME
			7 -> FConstants.CO_MODEL_ZIP_CODE
			8 -> FConstants.CO_MODEL_ADDRESS
			9 -> FConstants.CO_MODEL_ADDRESS_DETAIL
			10 -> FConstants.CO_MODEL_BIZ_TYPE
			11 -> FConstants.CO_MODEL_BIZ_ITEM
			12 -> FConstants.CO_MODEL_PHONE
			13 -> FConstants.CO_MODEL_FAX
			14 -> FConstants.CO_MODEL_ETC1
			15 -> FConstants.CO_MODEL_ETC2
			16 -> FConstants.CO_MODEL_DIV
			17 -> FConstants.CO_MODEL_TYPE
			18 -> FConstants.CO_MODEL_GROUP
			19 -> FConstants.CO_MODEL_DELIVERY
			20 -> FConstants.CO_MODEL_CONTRACT_TYPE
			21 -> FConstants.CO_MODEL_BILL_TYPE
			22 -> FConstants.CO_MODEL_ACTUAL_PRICE
			23 -> FConstants.CO_MODEL_PREPAYMENT
			24 -> FConstants.CO_MODEL_TRANSACTION_STATE
			else -> ""
		}
	}
}