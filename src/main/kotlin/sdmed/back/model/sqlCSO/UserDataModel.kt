package sdmed.back.model.sqlCSO

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import io.jsonwebtoken.Claims
import jakarta.persistence.*
import sdmed.back.config.FConstants
import sdmed.back.model.common.*
import java.lang.Exception
import java.lang.StringBuilder
import java.sql.Timestamp
import java.util.*

/**
 * User data model
 *
 * @property thisIndex
 * @property id 아이디
 * @property pw 비번
 * @property name 이름
 * @property mail 메일
 * @property phoneNumber 번호
 * @property role 권한
 * @property dept 부서
 * @property status 상태
 * @property regDate 등록일
 * @property lastLoginDate 마지막로그인일
 * @property subData
 * @property children
 * @property userData
 * @property pharmaceuticals
 * @constructor Create empty User data model
 */
@Entity
data class UserDataModel(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false, nullable = false)
	var thisIndex: Long = 0,
	@Column(columnDefinition = "nvarchar(255)", nullable = false, updatable = false, unique = true)
	var id: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var pw: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var name: String = "",
	@Column(columnDefinition = "varchar(255)", nullable = false)
	var mail: String = "",
	@Column(columnDefinition = "nvarchar(255)")
	var phoneNumber: String? = null,
	@Column
	var role: Int = UserRole.None.flag,
	@Column
	var dept: Int = UserDept.None.flag,
	@Column
	var status: UserStatus = UserStatus.None,
	@Column
	var regDate: Timestamp = Timestamp(Date().time),
	@Column
	var lastLoginDate: Timestamp? = null,
	@OneToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn
	@JsonManagedReference
	var subData: UserDataSubModel? = null,
	@OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
	@JoinColumn
	@JsonManagedReference
	var children: MutableList<UserDataModel>? = null,
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn
	@JsonBackReference
	@JsonIgnore
	var userData: UserDataModel? = null,
	@ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
	@JoinTable(
		name = "user_pharmaceutical",
		joinColumns = [JoinColumn(name = "user_id")],
		inverseJoinColumns = [JoinColumn(name = "pharmaceutical_id")]
	)
	@JsonManagedReference
	var pharmaceuticals: MutableList<PharmaceuticalModel>? = null,
) {
	fun setChild(): UserDataModel {
		children?.forEach {
			it.userData = this
			it.setChild()
		}
		return this
	}
	fun init() {
		pharmaceuticals = mutableListOf()
		children?.forEach {
			it.init()
		}
	}
	fun buildData(claims: Claims): UserDataModel {
		this.thisIndex = claims[FConstants.CLAIM_INDEX].toString().toLong()
		this.id = claims[FConstants.CLAIM_ID].toString()
		this.name = claims[FConstants.CLAIM_NAME].toString()
		this.role = claims[FConstants.CLAIM_ROLE].toString().toInt()
		this.dept = claims[FConstants.CLAIM_DEPT].toString().toInt()
		this.status = UserStatus.valueOf(claims[FConstants.CLAIM_STATUS].toString())
		return this
	}
	fun addChild(child: List<UserDataModel>) {
		val childBuff = child.filterNot { isAncestorOf(it.id) }.filterNot { it.userData != null }.onEach { it.userData = this }
		children?.addAll(childBuff)
	}
	private fun isAncestorOf(userID: String): Boolean {
		var currentMother = this.userData
		while (currentMother != null) {
			if (currentMother.id == userID) {
				return true
			}
			currentMother = currentMother.userData
		}
		return false
	}

	fun findHeader(data: List<String>): Boolean {
		if (data.size < FConstants.USER_MODEL_COUNT) {
			return false
		}

		if (data[0] != getTitle(0) || data[1] != getTitle(1) ||
			data[2] != getTitle(2) || data[3] != getTitle(3) ||
			data[4] != getTitle(4) || data[5] != getTitle(5) ||
			data[6] != getTitle(6) || data[7] != getTitle(7) ||
			data[8] != getTitle(8) || data[9] != getTitle(9) ||
			data[10] != getTitle(10) || data[11] != getTitle(11)) {
			return false
		}

		return true
	}
	fun setRows(data: List<String>): Boolean? {
		if (data.size <= 1) {
			return false
		}

		return try {
			for (i in 0 until 7) {
				setIndex(data[i], i)
			}
			if (errorCondition()) {
				return false
			}
			setSubdata(data)
			true
		} catch (_: Exception) {
			null
		}
	}
	fun getIndex(index: Int): String {
		return when (index) {
			0 -> id
			1 -> pw
			2 -> name
			3 -> mail
			4 -> phoneNumber ?: ""
			5 -> UserRole.fromFlag(role).toString()
			6 -> UserDept.fromFlag(dept).toString()
			7 -> status.toString()
			else -> ""
		}
	}
	fun setIndex(data: String?, index: Int) {
		when (index) {
			0 -> id = data ?: ""
			1 -> pw = data ?: ""
			2 -> name = data ?: ""
			3 -> mail = data ?: ""
			4 -> phoneNumber = data
			5 -> role = UserRole.parseString(data).flag
			6 -> dept = UserDept.parseString(data).flag
			7 -> status = UserStatus.parseString(data)
		}
	}
	fun setSubdata(data: List<String>) {
		subData = UserDataSubModel().apply {
			companyName = data[8]
			companyNumber = data[9]
			companyAddress = data[10]
			bankAccount = data[11]
			mother = this@UserDataModel
		}
	}
	fun getTitle(index: Int): String {
		return when (index) {
			0 -> FConstants.USER_MODEL_ID
			1 -> FConstants.USER_MODEL_PW
			2 -> FConstants.USER_MODEL_NAME
			3 -> FConstants.USER_MODEL_MAIL
			4 -> FConstants.USER_MODEL_PHONE
			5 -> FConstants.USER_MODEL_ROLE
			6 -> FConstants.USER_MODEL_DEPT
			7 -> FConstants.USER_MODEL_STATUS
			8 -> FConstants.USER_MODEL_COMPANY_NAME
			9 -> FConstants.USER_MODEL_COMPANY_NUMBER
			10 -> FConstants.USER_MODEL_COMPANY_ADDRESS
			11 -> FConstants.USER_MODEL_BANK_ACCOUNT
			else -> ""
		}
	}
	fun errorCondition(): Boolean {
		if (getIndex(0).isEmpty()) {
			return true
		} else if (getIndex(1).isEmpty()) {
			return true
		} else if (getIndex(2).isEmpty()) {
			return true
		}
		return false
	}
	fun errorString(): String {
		val ret = StringBuilder()
		for (i in 0..FConstants.USER_MODEL_COUNT) {
			ret.append("${getTitle(i)} : ${getIndex(i)}\n")
		}
		return ret.toString()
	}
}