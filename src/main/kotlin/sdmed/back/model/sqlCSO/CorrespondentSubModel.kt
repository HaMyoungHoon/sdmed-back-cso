package sdmed.back.model.sqlCSO

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import sdmed.back.model.common.*

/**
 * CorrespondentSubModel
 *
 * @property thisIndex
 * @property code 거래처 코드
 * @property correspondentDiv 거래처 구분
 * @property correspondentType 거래처 종류
 * @property correspondentGroup 거래처 그룹
 * @property correspondentDelivery 배송 구분
 * @property contractType 계약 구분
 * @property correspondentBillType 계산서 발행
 * @property actualPrice 실단가 유무
 * @property prepayment 선결제 여부
 * @property transactionState 거래 유무
 * @property mother
 * @constructor Create empty Correspondent sub model
 */
@Entity
data class CorrespondentSubModel(
	@Id
	@Column(name = "this_index", updatable = false, nullable = false)
	var thisIndex: Long = 0,
	@Column(nullable = false)
	var code: Int = 0,
	@Column(nullable = false)
	var correspondentDiv: CorrespondentDiv = CorrespondentDiv.None,
	@Column(nullable = false)
	var correspondentType: CorrespondentType = CorrespondentType.None,
	@Column(nullable = false)
	var correspondentGroup: CorrespondentGroup = CorrespondentGroup.None,
	@Column(nullable = false)
	var correspondentDelivery: CorrespondentDelivery = CorrespondentDelivery.None,
	@Column(nullable = false)
	var contractType: ContractType = ContractType.None,
	@Column(nullable = false)
	var correspondentBillType: CorrespondentBillType = CorrespondentBillType.None,
	@Column(nullable = false)
	var actualPrice: Boolean = false,
	@Column(nullable = false)
	var prepayment: Boolean = false,
	@Column(nullable = false)
	var transactionState: Boolean = false,
	@OneToOne(fetch = FetchType.LAZY, optional = true)
	@JoinColumn
	@JsonBackReference
	@JsonIgnore
	var mother: CorrespondentModel? = null,
) {
}