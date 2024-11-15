package sdmed.back.model.sqlCSO

import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
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
 * @constructor Create empty Correspondent model
 */
@Entity
data class CorrespondentModel(
	@Id
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
	var subData: CorrespondentSubModel? = null
) {
}