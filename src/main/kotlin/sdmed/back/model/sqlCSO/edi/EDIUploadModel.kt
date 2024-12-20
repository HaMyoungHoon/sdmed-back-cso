package sdmed.back.model.sqlCSO.edi

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Transient
import java.util.*

/**
 * EDI upload model
 *
 * @property thisPK
 * @property userPK
 * @property year 해당 데이터 원본 날짜
 * @property month 해당 데이터 원본 날짜
 * @property day 해당 업로드 서버 날짜  (서버는 2024-12-12 인데, 데이터가 2024-10월 분 일 수도 있음)
 * @property hospitalPK
 * @property orgName
 * @property ediState
 * @property regDate
 * @property pharmaList
 * @property fileList
 * @property responseList
 * @constructor Create empty E d i upload model
 */
@Entity
data class EDIUploadModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var userPK: String = "",
	@Column(columnDefinition = "nvarchar(4)", nullable = false)
	var year: String = "",
	@Column(columnDefinition = "nvarchar(2)", nullable = false)
	var month: String = "",
	@Column(columnDefinition = "nvarchar(2)", nullable = false)
	var day: String = "",
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var hospitalPK: String = "",
	@Column(columnDefinition = "nvarchar(255)", updatable = false, nullable = false)
	var orgName: String = "",
	@Column
	var ediState: EDIState = EDIState.None,
	@Column(updatable = false, nullable = false)
	var regDate: Date = Date(),
	@Transient
	var pharmaList: MutableList<EDIUploadPharmaModel> = mutableListOf(),
	@Transient
	var fileList: MutableList<EDIUploadFileModel> = mutableListOf(),
	@Transient
	var responseList: MutableList<EDIUploadResponseModel> = mutableListOf(),
) {
}