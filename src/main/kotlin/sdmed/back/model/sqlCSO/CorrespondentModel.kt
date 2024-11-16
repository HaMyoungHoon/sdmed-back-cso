package sdmed.back.model.sqlCSO

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import java.sql.Date

/**
 * CorrespondentModel
 *
 * @property thisIndex
 * @property code 거래처 코드
 * @property taxpayerNumber 사업자등록번호
 * @property orgName 사업자원어명
 * @property innerName 상호(내부)명
 * @property openDate 거래 개시일
 * @property closeDate 거래 종료일
 * @property ownerName 대표자명
 * @property zipCode 우편번호
 * @property address 주소
 * @property addressDetail 상세주소
 * @property businessType 업태
 * @property businessItem 종목
 * @property phoneNumber 전화번호
 * @property faxNumber 팩스번호
 * @property etc1 비고 1
 * @property etc2 비고 2
 * @property taxpayerImageUrl 사업자등록증 image url
 * @property subData 기본정보
 * @property userData 상위 유저
 * @property userDataThisIndex 상위 유저 index
 * @property children 하위 거래처
 * @property correspondent mother
 * @constructor Create empty Correspondent model
 */
@Entity
data class CorrespondentModel(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "this_index", updatable = false, nullable = false)
	var thisIndex: Long = 0,
	@Column(nullable = false)
	var code: Int = 0,
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var taxpayerNumber: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var orgName: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var innerName: String = "",
	@Column(nullable = false)
	var openDate: Date = Date(java.util.Date().time),
	@Column
	var closeDate: Date? = null,
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var ownerName: String = "",
	@Column(columnDefinition = "nvarchar(100)", nullable = false)
	var zipCode: String = "00000",
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
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn
	@JsonBackReference
	var userData: UserDataModel? = null,
	@Column(insertable = false, updatable = false, name = "userData_thisIndex")
	var userDataThisIndex: Long? = null,
	@OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
	@JoinColumn
	@JsonManagedReference
	var children: MutableList<CorrespondentModel>? = null,
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn
	@JsonBackReference
	@JsonIgnore
	var correspondent: CorrespondentModel? = null,
) {
	fun setChild(): CorrespondentModel {
		children?.forEach {
			it.correspondent = this
			it.setChild()
		}
		return this
	}
	fun init() {
		children?.forEach { it.init() }
	}

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
			data[14] != getTitle(0) || data[15] != getTitle(1)) {
			return false
		}

		return true
	}
	fun getErrorString(): String {

		return ""
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
			0 -> code = data?.toInt() ?: 0
			1 -> taxpayerNumber = data ?: ""
			2 -> orgName = data ?: ""
			3 -> innerName = data ?: ""
			4 -> openDate = FExtensions.parseStringToSqlDate(data, "yyyy-MM-dd")
			5 -> closeDate = if (data != null) FExtensions.parseStringToSqlDate(data, "yyyy-MM-dd") else null
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
			else -> ""
		}
	}
}