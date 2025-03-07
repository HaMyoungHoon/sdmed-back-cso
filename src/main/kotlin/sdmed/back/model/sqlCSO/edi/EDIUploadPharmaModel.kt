package sdmed.back.model.sqlCSO.edi

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Transient
import java.util.*

@Entity
data class EDIUploadPharmaModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var ediPK: String = "",
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var pharmaPK: String = "",
	@Column(columnDefinition = "nvarchar(255)", updatable = false, nullable = false)
	var orgName: String = "",
	@Column(columnDefinition = "nvarchar(4)", nullable = false)
	var year: String = "",
	@Column(columnDefinition = "nvarchar(2)", nullable = false)
	var month: String = "",
	@Column(columnDefinition = "nvarchar(2)", nullable = false)
	var day: String = "",
	@Column
	var isCarriedOver: Boolean = false,
	@Column
	var ediState: EDIState = EDIState.None,
	@Transient
	var medicineList: MutableList<EDIUploadPharmaMedicineModel> = mutableListOf(),
	@Transient
	var fileList: MutableList<EDIUploadPharmaFileModel> = mutableListOf()
) {
	fun copy(pharma: EDIUploadPharmaModel): EDIUploadPharmaModel {
		this.thisPK = pharma.thisPK
		this.ediPK = pharma.ediPK
		this.pharmaPK = pharma.pharmaPK
		this.orgName = pharma.orgName
		this.year = pharma.year
		this.month = pharma.month
		this.day = pharma.day
		this.isCarriedOver = pharma.isCarriedOver
		this.ediState = pharma.ediState
		this.medicineList = pharma.medicineList
		this.fileList = pharma.fileList
		return this
	}
	fun dateCopy(year: String, month: String, day: String): EDIUploadPharmaModel {
		this.year = year
		this.month = month
		this.day = day
		return this
	}
	fun safeCopy(data: EDIUploadPharmaModel): EDIUploadPharmaModel {
		this.ediState = data.ediState
		this.medicineList.onEach { x ->
			data.medicineList.find { y -> y.thisPK == x.thisPK }?.let { z ->
				x.safeCopy(z)
			}
		}

		return this
	}
}