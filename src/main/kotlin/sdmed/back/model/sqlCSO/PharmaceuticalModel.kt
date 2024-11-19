package sdmed.back.model.sqlCSO

import jakarta.persistence.*
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.model.common.*
import java.lang.StringBuilder
import java.sql.Date

/**
 * PharmaceuticalModel
 *
 * @property thisIndex
 * @property code 거래처코드
 * @property orgName 사업자원어명
 * @property innerName 사업자내부명
 * @property ownerName 대표자명
 * @property taxpayerNumber 사업자번호
 * @property phoneNumber 전화번호
 * @property faxNumber 팩스번호
 * @property zipCode 우편번호
 * @property address 주소
 * @property addressDetail 상세주소
 * @property businessType 업태
 * @property businessItem 종목
 * @property billType 계산서발행
 * @property pharmaceuticalType 거래처종류
 * @property pharmaceuticalGroup 거래처그룹
 * @property contractType 계약구분
 * @property deliveryDiv 배송구분
 * @property mail 메일
 * @property mobilePhone 담당자번호
 * @property openDate 거래개시일
 * @property closeDate 거래종료일
 * @property etc1 비고1
 * @property etc2 비고2
 * @property imageUrl 사업자등록증이미지
 * @property userDataModel
 * @property userHospitalRelations
 * @constructor Create empty Pharmaceutical model
 */
@Entity
data class PharmaceuticalModel(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	var thisIndex: Long = 0,
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
	@Column(columnDefinition = "nvarchar(255)")
	var phoneNumber: String? = null,
	@Column(columnDefinition = "nvarchar(255)")
	var faxNumber: String? = null,
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
	@Column(nullable = false)
	var billType: BillType = BillType.None,
	@Column(nullable = false)
	var pharmaceuticalType: PharmaceuticalType = PharmaceuticalType.None,
	@Column(nullable = false)
	var pharmaceuticalGroup: PharmaceuticalGroup = PharmaceuticalGroup.None,
	@Column(nullable = false)
	var contractType: ContractType = ContractType.None,
	@Column(nullable = false)
	var deliveryDiv: DeliveryDiv = DeliveryDiv.None,
	@Column(columnDefinition = "nvarchar(255)")
	var mail: String? = null,
	@Column(columnDefinition = "nvarchar(255)")
	var mobilePhone: String? = null,
	@Column
	var openDate: Date? = null,
	@Column
	var closeDate: Date? = null,
	@Column(columnDefinition = "nvarchar(500)")
	var etc1: String? = null,
	@Column(columnDefinition = "nvarchar(500)")
	var etc2: String? = null,
	@Column(columnDefinition = "nvarchar(500)")
	var imageUrl: String? = null,
	@ManyToMany(mappedBy = "pharmaceuticals")
	var userDataModel: MutableList<UserDataModel>? = null,
	@OneToMany(mappedBy = "pharmaceuticalModel")
	val userHospitalRelations: MutableList<UserPharmaceuticalHospital> = mutableListOf()
	) {

	fun findHeader(data: List<String>): Boolean {
		if (data.size < FConstants.PHARMA_MODEL_COUNT) {
			return false
		}

		if (data[0] != getTitle(0) || data[1] != getTitle(1) || data[2] != getTitle(2) || data[3] != getTitle(3) ||
			data[4] != getTitle(4) || data[5] != getTitle(5) ||	data[6] != getTitle(6) || data[7] != getTitle(7) ||
			data[8] != getTitle(8) || data[9] != getTitle(9) ||	data[10] != getTitle(10) || data[11] != getTitle(11) ||
			data[12] != getTitle(12) || data[13] != getTitle(13) ||	data[14] != getTitle(14) || data[15] != getTitle(15) ||
			data[16] != getTitle(16) || data[17] != getTitle(17) ||	data[18] != getTitle(18) || data[19] != getTitle(19) ||
			data[20] != getTitle(20) || data[21] != getTitle(21) ||	data[22] != getTitle(22)) {
			return false
		}

		return true
	}
	fun setRows(data: List<String>): Boolean? {
		if (data.size <= 1) {
			return false
		}

		return try {
			for ((index, value) in data.withIndex()) {
				setIndex(value, index)
			}
			if (errorCondition()) {
				return false
			}
			true
		} catch (_: Exception) {
			null
		}
	}
	fun getIndex(index: Int): String {
		return when (index) {
			0 -> code.toString()
			1 -> orgName
			2 -> innerName
			3 -> ownerName
			4 -> taxpayerNumber
			5 -> phoneNumber ?: ""
			6 -> faxNumber ?: ""
			7 -> zipCode
			8 -> address
			9 -> addressDetail ?: ""
			10 -> businessType ?: ""
			11 -> businessItem ?: ""
			12 -> billType.desc
			13 -> pharmaceuticalType.desc
			14 -> pharmaceuticalGroup.desc
			15 -> contractType.desc
			16 -> deliveryDiv.desc
			17 -> mail ?: ""
			18 -> mobilePhone ?: ""
			19 -> FExtensions.parseDateTimeString(openDate, "yyyy-MM-dd")
			20 -> FExtensions.parseDateTimeString(closeDate, "yyyy-MM-dd")
			21 -> etc1 ?: ""
			22 -> etc2 ?: ""
			else -> ""
		}
	}
	fun setIndex(data: String?, index: Int) {
		when (index) {
			0 -> code = data?.toIntOrNull() ?: 0
			1 -> orgName = data ?: ""
			2 -> innerName = data ?: ""
			3 -> ownerName = data ?: ""
			4 -> taxpayerNumber = data ?: ""
			5 -> phoneNumber = data
			6 -> faxNumber = data
			7 -> zipCode = data ?: ""
			8 -> address = data ?: ""
			9 -> addressDetail = data
			10 -> businessType = data
			11 -> businessItem = data
			12 -> billType = BillType.parseString(data)
			13 -> pharmaceuticalType = PharmaceuticalType.parseString(data)
			14 -> pharmaceuticalGroup = PharmaceuticalGroup.parseString(data)
			15 -> contractType = ContractType.parseString(data)
			16 -> deliveryDiv = DeliveryDiv.parseString(data)
			17 -> mail = data
			18 -> mobilePhone = data
			19 -> openDate = if (data.isNullOrEmpty()) null else FExtensions.parseStringToSqlDate(data, "yyyy-MM-dd")
			20 -> closeDate = if (data.isNullOrEmpty()) null else FExtensions.parseStringToSqlDate(data, "yyyy-MM-dd")
			21 -> etc1 = data
			22 -> etc2 = data
		}
	}
	fun getTitle(index: Int): String {
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
		if (getIndex(0) == "0") {
			return true
		} else if (getIndex(1).isEmpty()) {
			return true
		} else if (getIndex(2).isEmpty()) {
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
		for (i in 0 until FConstants.PHARMA_MODEL_COUNT) {
			ret.append("${getTitle(i)} : ${getIndex(i)}\n")
		}
		return ret.toString()
	}
}