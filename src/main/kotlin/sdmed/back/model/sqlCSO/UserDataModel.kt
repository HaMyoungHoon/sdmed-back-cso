package sdmed.back.model.sqlCSO

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import io.jsonwebtoken.Claims
import jakarta.persistence.*
import sdmed.back.config.FConstants
import sdmed.back.model.common.*
import sdmed.back.model.common.UserRoles
import java.lang.Exception
import java.sql.Timestamp
import java.util.*

/**
 * UserDataModel
 *
 * @property thisIndex
 * @property id 아이디
 * @property pw 비번
 * @property name 이름
 * @property mail 메일주소
 * @property phoneNumber 전화번호
 * @property regDate 등록일
 * @property lastLoginDate 마지막 로그인일
 * @property role 권한
 * @property dept 부서
 * @property status 상태
 * @property subData
 * @property children 하위 유저
 * @property userData mother
 * @property correspondents 담당 거래처
 * @constructor Create empty User data model
 */
@Entity
data class UserDataModel(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "this_index", updatable = false, nullable = false)
	@get:JsonProperty("this_index")
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
	@OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
	@JoinColumn
	@JsonManagedReference
	var correspondents: MutableList<CorrespondentModel>? = null,
) {
	fun setChild(): UserDataModel {
		children?.forEach {
			it.userData = this
			it.setChild()
		}
		correspondents?.forEach {
			it.userData = this
			it.setChild()
		}
		return this
	}
	fun init() {
		correspondents = mutableListOf()
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
			true
		} catch (_: Exception) {
			null
		}
	}
	fun findHeader(data: List<String>): Boolean {
		if (data.size < 8) {
			return false
		}

		if (data[0] != getTitle(0) || data[1] != getTitle(1) ||
			data[2] != getTitle(2) || data[3] != getTitle(3) ||
			data[4] != getTitle(4) || data[5] != getTitle(5) ||
			data[6] != getTitle(6) || data[7] != getTitle(7)) {
			return false
		}

		return true
	}

	fun getErrorString(): String {
		if (getIndex(0).isEmpty()) {
			return "${getTitle(0)}${FConstants.NOT_FOUND_VALUE_OR_FORMAT}"
		} else if (getIndex(1).isEmpty()) {
			return "${getTitle(1)}${FConstants.NOT_FOUND_VALUE_OR_FORMAT}"
		} else if (getIndex(2).isEmpty()) {
			return "${getTitle(2)}${FConstants.NOT_FOUND_VALUE_OR_FORMAT}"
		}

		return "${getTitle(0)} : ${getIndex(0)}\n" +
			"${getTitle(1)} : ${getIndex(1)}\n" +
			"${getTitle(2)} : ${getIndex(2)}\n" +
			"${getTitle(3)} : ${getIndex(3)}\n" +
			"${getTitle(4)} : ${getIndex(4)}\n" +
			"${getTitle(5)} : ${getIndex(5)}\n" +
			"${getTitle(6)} : ${getIndex(6)}\n" +
			"${getTitle(7)} : ${getIndex(7)}\n"
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
			else -> ""
		}
	}
}