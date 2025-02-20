package sdmed.back.model.sqlCSO.medicine

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import sdmed.back.config.FExtensions
import sdmed.back.model.common.medicine.*
import java.util.*

@Entity
data class MedicineSubModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(50)", nullable = false, unique = true)
	var code: String = "",
	@Column(columnDefinition = "nvarchar(100)")
	var standard: String = "",
	@Column
	var accountUnit: Double = 0.0,
	@Column
	var medicineType: MedicineType = MedicineType.General,
	@Column
	var medicineMethod: MedicineMethod = MedicineMethod.ETC,
	@Column
	var medicineCategory: MedicineCategory = MedicineCategory.ETC,
	@Column
	var medicineGroup: MedicineGroup = MedicineGroup.Medicine,
	@Column
	var medicineDiv: MedicineDiv = MedicineDiv.Open,
	@Column
	var medicineRank: MedicineRank = MedicineRank.None,
	@Column
	var medicineStorageTemp: MedicineStorageTemp = MedicineStorageTemp.RoomTemp,
	@Column
	var medicineStorageBox: MedicineStorageBox = MedicineStorageBox.Confidential,
	@Column
	var packageUnit: Int = 0,
	// mysql
	@Column(columnDefinition = "text")
//	@Column(columnDefinition = "nvarchar(max)")
	var unit: String = "",
	// mysql
	@Column(columnDefinition = "text")
//	@Column(columnDefinition = "nvarchar(max)")
	var etc1: String = "",
	// mysql
	@Column(columnDefinition = "text")
//	@Column(columnDefinition = "nvarchar(max)")
	var etc2: String = "",
	) {

	fun insertString(): String {
		val standard = FExtensions.escapeString(standard)
		val unit = FExtensions.escapeString(unit)
		val etc1 = FExtensions.escapeString(etc1)
		val etc2 = FExtensions.escapeString(etc2)
		return "('$thisPK', '$code', '$standard', '$accountUnit', '${medicineType.index}', '${medicineMethod.index}', '${medicineCategory.index}', '${medicineGroup.index}', " +
				"'${medicineDiv.index}', '${medicineRank.index}', '${medicineStorageTemp.index}', '${medicineStorageBox.index}', '$packageUnit', '$unit', '$etc1', '$etc2')"
	}
	fun safeCopy(rhs: MedicineSubModel): MedicineSubModel {
		this.standard = rhs.standard
		this.accountUnit = rhs.accountUnit
		this.medicineType = rhs.medicineType
		this.medicineMethod = rhs.medicineMethod
		this.medicineCategory = rhs.medicineCategory
		this.medicineGroup = rhs.medicineGroup
		this.medicineDiv = rhs.medicineDiv
		this.medicineRank = rhs.medicineRank
		this.medicineStorageTemp = rhs.medicineStorageTemp
		this.medicineStorageBox = rhs.medicineStorageBox
		this.packageUnit = rhs.packageUnit
		this.unit = rhs.unit
		this.etc1 = rhs.etc1
		this.etc2 = rhs.etc2
		return this
	}
}