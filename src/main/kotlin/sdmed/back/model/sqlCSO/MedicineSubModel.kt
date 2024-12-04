package sdmed.back.model.sqlCSO

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import sdmed.back.config.FExtensions
import sdmed.back.model.common.*
import java.util.*

@Entity
data class MedicineSubModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(nullable = false, unique = true)
	var code: Int = 0,
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
	@Column(columnDefinition = "text")
	var unit: String = "",
	@Column(columnDefinition = "text")
	var etc1: String = "",
	@Column(columnDefinition = "text")
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
}