package sdmed.back.model.sqlCSO

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import io.jsonwebtoken.Claims
import jakarta.persistence.*
import sdmed.back.config.FConstants
import sdmed.back.model.common.*
import sdmed.back.model.common.UserRoles
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
 * @constructor Create empty User data model
 */
@Entity
data class UserDataModel(
	@Id
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
	var regDate: Timestamp = Timestamp(Date().time),
	@Column
	var lastLoginDate: Timestamp? = null,
	@Column
	var role: Int = UserRole.None.flag,
	@Column
	var dept: Int = UserDept.None.flag,
	@Column
	var status: UserStatus = UserStatus.None,
	@OneToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn
	@JsonManagedReference
	var subData: UserDataSubModel? = null
) {
	fun buildData(claims: Claims): UserDataModel {
		this.thisIndex = claims[FConstants.CLAIM_INDEX].toString().toLong()
		this.id = claims[FConstants.CLAIM_ID].toString()
		this.name = claims[FConstants.CLAIM_NAME].toString()
		this.role = claims[FConstants.CLAIM_ROLE].toString().toInt()
		this.dept = claims[FConstants.CLAIM_DEPT].toString().toInt()
		this.status = UserStatus.valueOf(claims[FConstants.CLAIM_STATUS].toString())
		return this
	}
}