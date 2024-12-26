package sdmed.back.model.sqlCSO.user

import com.fasterxml.jackson.annotation.JsonProperty
import io.jsonwebtoken.Claims
import jakarta.persistence.*
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.model.common.user.UserDept
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserStatus
import sdmed.back.model.sqlCSO.hospital.HospitalModel
import java.lang.Exception
import java.lang.StringBuilder
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
	@Column(columnDefinition = "nvarchar(max)", nullable = false)
	var taxpayerImageUrl: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var companyName: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var companyNumber: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var companyAddress: String = "",
	@Column(columnDefinition = "nvarchar(max)", nullable = false)
	var bankAccountImageUrl: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var bankAccount: String = "",
	@Column
	var regDate: Timestamp = Timestamp(Date().time),
	@Column
	var lastLoginDate: Timestamp? = null,
	@Transient
	var motherPK: String = "",
	@Transient
	var children: MutableList<UserDataModel> = mutableListOf(),
	@Transient
	var hosList: MutableList<HospitalModel> = mutableListOf()
) {
	fun safeCopy(data: UserDataModel): UserDataModel {
		this.name = data.name
		this.mail = data.mail
		this.phoneNumber = data.phoneNumber
		this.role = data.role
		this.dept = data.dept
		this.status = data.status
		this.taxpayerImageUrl = data.taxpayerImageUrl
		this.companyName = data.companyName
		this.companyNumber = data.companyNumber
		this.companyAddress = data.companyAddress
		this.bankAccountImageUrl = data.bankAccountImageUrl
		this.bankAccount = data.bankAccount
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

	fun findHeader(data: List<String>): Boolean {
		if (data.size < FConstants.MODEL_USER_COUNT) {
			return false
		}

		if (data[0] != titleGet(0) || data[1] != titleGet(1) ||
			data[2] != titleGet(2) || data[3] != titleGet(3) ||
			data[4] != titleGet(4) || data[5] != titleGet(5) ||
			data[6] != titleGet(6) || data[7] != titleGet(7) ||
			data[8] != titleGet(8) || data[9] != titleGet(9) ||
			data[10] != titleGet(10) || data[11] != titleGet(11)) {
			return false
		}

		return true
	}
	fun rowSet(data: List<String>): Boolean? {
		if (data.size <= 1) {
			return false
		}

		return try {
			for (i in 0 until FConstants.MODEL_USER_COUNT) {
				indexSet(data[i], i)
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
			0 -> id
			1 -> pw
			2 -> name
			3 -> mail
			4 -> phoneNumber
			5 -> UserRole.fromFlag(role).toString()
			6 -> UserDept.fromFlag(dept).toString()
			7 -> status.toString()
			8 -> companyName
			9 -> companyNumber
			10 -> companyAddress
			11 -> bankAccount
			else -> ""
		}
	}
	fun indexSet(data: String?, index: Int) {
		when (index) {
			0 -> id = data ?: ""
			1 -> pw = data ?: ""
			2 -> name = data ?: ""
			3 -> mail = data ?: ""
			4 -> phoneNumber = data ?: ""
			5 -> role = UserRole.parseString(data).flag
			6 -> dept = UserDept.parseString(data).flag
			7 -> status = UserStatus.parseString(data)
			8 -> companyName = data ?: ""
			9 -> companyNumber = data ?: ""
			10 -> companyAddress = data ?: ""
			11 -> bankAccount = data ?: ""
		}
	}
	fun titleGet(index: Int): String {
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
			9 -> FConstants.MODEL_USER_COMPANY_NUMBER
			10 -> FConstants.MODEL_USER_COMPANY_ADDRESS
			11 -> FConstants.MODEL_USER_BANK_ACCOUNT
			else -> ""
		}
	}
	fun errorCondition(): Boolean {
		if (indexGet(0).isEmpty()) {
			return true
		} else if (indexGet(1).isEmpty()) {
			return true
		} else if (indexGet(2).isEmpty()) {
			return true
		}
		return false
	}
	fun errorString(): String {
		val ret = StringBuilder()
		for (i in 0..FConstants.MODEL_USER_COUNT) {
			ret.append("${titleGet(i)} : ${indexGet(i)}\n")
		}
		return ret.toString()
	}
	fun insertString(): String {
		val id = FExtensions.regexSpecialCharRemove(id)
		val pw = FExtensions.regexSpecialCharRemove(pw)
		val name = FExtensions.regexSpecialCharRemove(name)
		val mail = FExtensions.escapeString(mail)
		val phoneNumber = FExtensions.escapeString(phoneNumber)
		val companyName = FExtensions.escapeString(companyName)
		val companyNumber = FExtensions.escapeString(companyNumber)
		val companyAddress = FExtensions.escapeString(companyAddress)
		val bankAccount = FExtensions.escapeString(bankAccount)
		val regDateString = FExtensions.parseDateTimeString(regDate, "yyyy-MM-dd HH:mm:ss")
		return "('$thisPK', '$id', '$pw', '$name', '$mail', '$phoneNumber', '$role', '$dept', '${status.index}', '$companyName', '$companyNumber', '$companyAddress', '$bankAccount', '$regDateString', '$taxpayerImageUrl', '$bankAccountImageUrl')"
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
		ret += "taxpayerImageUrl: ${taxpayerImageUrl}, "
		ret += "companyName: ${companyName}, "
		ret += "companyNumber: ${companyNumber}, "
		ret += "companyAddress: ${companyAddress}, "
		ret += "bankAccountImageUrl: ${bankAccountImageUrl}, "
		ret += "bankAccount: ${bankAccount}"
		return ret
	}
}