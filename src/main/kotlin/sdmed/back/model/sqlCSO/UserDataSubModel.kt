package sdmed.back.model.sqlCSO

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*

/**
 * UserDataSubModel
 *
 * @property thisIndex
 * @property taxpayerImageUrl 사업자등록증 이미지
 * @property companyName 회사명
 * @property companyNumber 사업자등록번호
 * @property companyAddress 소제지
 * @property bankAccountImageUrl 은행계좌 이미지
 * @property mother
 * @constructor Create empty User data sub model
 */
@Entity
data class UserDataSubModel(
	@Id
	@Column(name = "this_index", updatable = false, nullable = false)
	@get:JsonProperty("this_index")
	var thisIndex: Long = 0,
	@Column(columnDefinition = "varchar(500)")
	var taxpayerImageUrl: String? = null,
	@Column(columnDefinition = "varchar(255)", nullable = false)
	var companyName: String = "",
	@Column(columnDefinition = "varchar(255)")
	var companyNumber: String? = null,
	@Column(columnDefinition = "varchar(255)")
	var companyAddress: String? = null,
	@Column(columnDefinition = "varchar(500)")
	var bankAccountImageUrl: String? = null,
	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn
	@JsonBackReference
	@JsonIgnore
	var mother: UserDataModel? = null
) {
}