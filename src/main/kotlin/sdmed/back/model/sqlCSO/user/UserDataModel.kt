package sdmed.back.model.sqlCSO.user

import com.fasterxml.jackson.annotation.JsonProperty
import io.jsonwebtoken.Claims
import jakarta.persistence.*
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.model.common.user.UserDept
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserStatus
import sdmed.back.model.sqlCSO.FExcelParseModel
import sdmed.back.model.sqlCSO.hospital.HospitalModel
import java.sql.Timestamp
import java.util.*

@Entity
data class UserDataModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(255)", nullable = false, updatable = false, unique = true)
	var id: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	var pw: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var name: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var mail: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var phoneNumber: String = "",
	@Column
	var role: Int = UserRole.None.flag,
	@Column
	var dept: Int = UserDept.None.flag,
	@Column
	var status: UserStatus = UserStatus.None,
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var companyName: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var companyInnerName: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var companyNumber: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var companyOwner: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var companyAddress: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var bankAccount: String = "",
	@Column(columnDefinition = "nvarchar(50)", nullable = false)
	var csoReportNumber: String = "",
	@Column
	var contractDate: Date? = null,
	@Column
	var regDate: Timestamp = Timestamp(Date().time),
	@Column
	var lastLoginDate: Timestamp? = null,
	@Transient
	var motherPK: String = "",
	@Transient
	var children: MutableList<UserDataModel> = mutableListOf(),
	@Transient
	var hosList: MutableList<HospitalModel> = mutableListOf(),
	@Transient
	var fileList: MutableList<UserFileModel> = mutableListOf(),
	@Transient
	var trainingList: MutableList<UserTrainingModel> = mutableListOf(),
): FExcelParseModel() {
	@Transient
	override var dataCount = FConstants.MODEL_USER_COUNT
	fun safeCopy(data: UserDataModel): UserDataModel {
		this.name = data.name
		this.mail = data.mail
		this.phoneNumber = data.phoneNumber
		this.role = data.role
		this.dept = data.dept
		this.status = data.status
		this.companyName = data.companyName
		this.companyInnerName = data.companyInnerName
		this.companyNumber = data.companyNumber
		this.companyOwner = data.companyOwner
		this.companyAddress = data.companyAddress
		this.bankAccount = data.bankAccount
		this.csoReportNumber = data.csoReportNumber
		this.contractDate = data.contractDate
		return this
	}
	fun buildData(claims: Claims): UserDataModel {
		this.thisPK = claims[FConstants.CLAIM_INDEX].toString()
		this.name = claims[FConstants.CLAIM_NAME].toString()
		this.status = UserStatus.valueOf(claims[FConstants.CLAIM_STATUS].toString())
		return this
	}
	fun addChild(child: List<UserDataModel>) {
		val childBuff = child.filterNot { isAncestorOf(it.thisPK) }
		children.addAll(childBuff)
	}
	private fun isAncestorOf(userPK: String): Boolean {
		if (this.motherPK == userPK) {
			return true
		}
		this.motherPK = userPK
		return false
	}

	fun indexGet(index: Int): String {
		return when (index) {
			0 -> id
			1 -> pw
			2 -> name
			3 -> mail
			4 -> phoneNumber
			5 -> UserRole.fromFlag(role).toString()
			6 -> UserDept.fromFlag(dept).toString()
			7 -> status.toString()
			8 -> companyName
			9 -> companyInnerName
			10 -> companyNumber
			11 -> companyOwner
			12 -> companyAddress
			13 -> bankAccount
			14 -> csoReportNumber
			15 -> FExtensions.parseDateTimeString(contractDate, "yyyy-MM-dd") ?: ""
			else -> ""
		}
	}
	override fun indexSet(data: String?, index: Int) {
		when (index) {
			0 -> {
				if (FExtensions.regexIdCheck(data) == true) {
					id = data ?: ""
				}
			}
			1 -> {
				if (FExtensions.regexPasswordCheck(data) == true) {
					pw = data ?: ""
				}
			}
			2 -> name = data ?: ""
			3 -> mail = data ?: ""
			4 -> phoneNumber = data ?: ""
			5 -> role = UserRole.parseString(data).flag
			6 -> dept = UserDept.parseString(data).flag
			7 -> status = UserStatus.parseString(data)
			8 -> companyName = data ?: ""
			9 -> companyInnerName = data ?: ""
			10 -> companyNumber = data ?: ""
			11 -> companyOwner = data ?: ""
			12 -> companyAddress = data ?: ""
			13 -> bankAccount = data ?: ""
			14 -> csoReportNumber = data ?: ""
			15 -> contractDate = FExtensions.parseStringToJavaDate(data, "yyyyMMdd")
		}
	}
	override fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_USER_ID
			1 -> FConstants.MODEL_USER_PW
			2 -> FConstants.MODEL_USER_NAME
			3 -> FConstants.MODEL_USER_MAIL
			4 -> FConstants.MODEL_USER_PHONE
			5 -> FConstants.MODEL_USER_ROLE
			6 -> FConstants.MODEL_USER_DEPT
			7 -> FConstants.MODEL_USER_STATUS
			8 -> FConstants.MODEL_USER_COMPANY_NAME
			9 -> FConstants.MODEL_USER_COMPANY_INNER_NAME
			10 -> FConstants.MODEL_USER_COMPANY_NUMBER
			11 -> FConstants.MODEL_USER_COMPANY_OWNER
			12 -> FConstants.MODEL_USER_COMPANY_ADDRESS
			13 -> FConstants.MODEL_USER_BANK_ACCOUNT
			14 -> FConstants.MODEL_USER_CSO_REPORT_NUMBER
			15 -> FConstants.MODEL_USER_CONTRACT_DATE
			else -> ""
		}
	}
	override fun errorCondition(): Boolean {
		if (indexGet(0).length < 3) {
			return true
		} else if (indexGet(1).length < 8) {
			return true
		} else if (indexGet(2).isEmpty()) {
			return true
		} else if (indexGet(9).isEmpty()) {
			return true
		}
		return false
	}
	override fun errorString(): String {
		val ret = StringBuilder()
		for (i in 0..FConstants.MODEL_USER_COUNT) {
			ret.append("${titleGet(i)} : ${indexGet(i)}\n")
		}
		return ret.toString()
	}
	fun insertString(): String {
		val name = FExtensions.regexSpecialCharRemove(name)
		val mail = FExtensions.escapeString(mail)
		val phoneNumber = FExtensions.escapeString(phoneNumber)
		val companyName = FExtensions.escapeString(companyName)
		val companyInnerName = FExtensions.escapeString(companyInnerName)
		val companyNumber = FExtensions.escapeString(companyNumber)
		val companyAddress = FExtensions.escapeString(companyAddress)
		val bankAccount = FExtensions.escapeString(bankAccount)
		val contractDateString = FExtensions.parseDateTimeString(contractDate, "yyyy-MM-dd HH:mm:ss")
		val regDateString = FExtensions.parseDateTimeString(regDate, "yyyy-MM-dd HH:mm:ss")
		return "('$thisPK', '$id', '$pw', '$name', '$mail', '$phoneNumber', '$role', '$dept', '${status.index}', '$companyName', '$companyInnerName', '$companyNumber', '$companyOwner', '$companyAddress', '$bankAccount', '$csoReportNumber', '$contractDateString', '$regDateString')"
	}

	override fun toString(): String {
		var ret = "id: ${id}, "
//		ret += "pw: ${pw}, "
		ret += "name: ${name}, "
		ret += "mail: ${mail}, "
		ret += "phoneNumber: ${phoneNumber}, "
		ret += "role: ${role}, "
		ret += "dept: ${dept}, "
		ret += "status: ${status}, "
		ret += "companyName: ${companyName}, "
		ret += "companyInnerName: ${companyInnerName}, "
		ret += "companyNumber: ${companyNumber}, "
		ret += "companyOwner: ${companyOwner}, "
		ret += "companyAddress: ${companyAddress}, "
		ret += "bankAccount: ${bankAccount}, "
		ret += "csoReportNumber : ${csoReportNumber}, "
		ret += "contractDate: $contractDate"
		return ret
	}
}