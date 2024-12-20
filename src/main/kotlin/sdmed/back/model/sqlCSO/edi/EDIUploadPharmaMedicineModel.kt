package sdmed.back.model.sqlCSO.edi

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.util.*

/**
 * EDI upload pharma medicine model
 *
 * @property thisPK
 * @property ediPK
 * @property pharmaPK
 * @property medicinePK
 * @property name
 * @property count
 * @property price
 * @property year 자료 업데이트 일자의 기준약가 날짜
 * @property month 자료 업데이트 일자의 기준약가 날짜
 * @property day 자료 업데이트 일자의 기준약가 날짜
 * @constructor Create empty E d i upload pharma medicine model
 */
@Entity
data class EDIUploadPharmaMedicineModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var ediPK: String = "",
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var pharmaPK: String = "",
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var medicinePK: String = "",
	@Column(columnDefinition = "nvarchar(255)", updatable = false, nullable = false)
	var name: String = "",
	@Column
	var count: Int = 0,
	@Column
	var price: Int = 0,
	@Column(columnDefinition = "nvarchar(4)", updatable = false, nullable = false)
	var year: String = "",
	@Column(columnDefinition = "nvarchar(2)", updatable = false, nullable = false)
	var month: String = "",
	@Column(columnDefinition = "nvarchar(2)", updatable = false, nullable = false)
	var day: String = "",
) {
}