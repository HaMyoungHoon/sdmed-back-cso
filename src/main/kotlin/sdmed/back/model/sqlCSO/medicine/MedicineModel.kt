package sdmed.back.model.sqlCSO.medicine

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.model.common.medicine.*
import java.util.*

@Entity
data class MedicineModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(nullable = false, unique = true)
	var code: Int = 0,
	@Column(columnDefinition = "nvarchar(255)")
	var mainIngredientCode: String = "",
	@Column
	var kdCode: Int = 0,
	@Column
	var standardCode: Int = 0,
	@Column(columnDefinition = "nvarchar(max)")
	var pharma: String = "",
	@Column(columnDefinition = "nvarchar(max)")
	var name: String = "",
	@Column
	var customPrice: Int = 0,
	@Column(nullable = false)
	@ColumnDefault("50")
	var charge: Int = 50,
	@Column(columnDefinition = "bit default 0", nullable = false)
	var inVisible: Boolean = false,
	@Transient
	var maxPrice: Int = 0,
	@Transient
	var medicineSubModel: MedicineSubModel = MedicineSubModel(),
	@Transient
	var medicineIngredientModel: MedicineIngredientModel = MedicineIngredientModel(),
	@Transient
	var medicinePriceModel: MutableList<MedicinePriceModel> = mutableListOf(),
) {
	fun genSub() {
		medicineSubModel.thisPK = UUID.randomUUID().toString()
		medicineSubModel.code = code
	}
	fun init() {
		maxPrice = medicinePriceModel.maxByOrNull { it.applyDate }?.maxPrice ?: customPrice
	}
	fun findHeader(data: List<String>): Boolean {
		if (data.size < FConstants.MODEL_MEDICINE_COUNT) {
			return false
		}

		for (index in 0 until FConstants.MODEL_MEDICINE_COUNT) {
			if (data[index] != titleGet(index)) {
				return false
			}
		}

		return true
	}
	fun rowSet(data: List<String>): Boolean? {
		if (data.size <= 1) {
			return false
		}

		return try {
			for ((index, value) in data.withIndex()) {
				indexSet(value, index)
			}
			if (errorCondition()) {
				return false
			}
			true
		} catch (_: Exception) {
			null
		}
	}
	fun indexSet(data: String?, index: Int) {
		when (index) {
			0 -> {
				code = data?.toIntOrNull() ?: 0
				medicineSubModel.code = code
			}
			1 -> mainIngredientCode = data ?: ""
			2 -> kdCode = data?.toIntOrNull() ?: 0
			3 -> standardCode = data?.toIntOrNull() ?: 0
			4 -> pharma = data ?: ""
			5 -> name = data ?: ""
			6 -> medicineSubModel.standard = data ?: ""
			7 -> medicineSubModel.accountUnit = data?.toDoubleOrNull() ?: 0.0
			8 -> charge = data?.toIntOrNull() ?: 50
			9 -> customPrice = data?.toIntOrNull() ?: 0
			10 -> medicineSubModel.medicineType = MedicineType.parseString(data)
			11 -> medicineSubModel.medicineMethod = MedicineMethod.parseString(data)
			12 -> medicineSubModel.medicineCategory = MedicineCategory.parseString(data)
			13 -> medicineSubModel.medicineGroup = MedicineGroup.parseString(data)
			14 -> medicineSubModel.medicineDiv = MedicineDiv.parseString(data)
			15 -> medicineSubModel.medicineRank = MedicineRank.parseString(data)
			16 -> medicineSubModel.medicineStorageTemp = MedicineStorageTemp.parseString(data)
			17 -> medicineSubModel.medicineStorageBox = MedicineStorageBox.parseString(data)
			18 -> medicineSubModel.packageUnit = data?.toIntOrNull() ?: 0
			19 -> medicineSubModel.unit = data ?: ""
			20 -> medicineSubModel.etc1 = data ?: ""
			21 -> medicineSubModel.etc2 = data ?: ""
		}
	}
	fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_MEDICINE_CODE
			1 -> FConstants.MODEL_MEDICINE_MAIN_INGREDIENT_CODE
			2 -> FConstants.MODEL_MEDICINE_KD_CODE
			3 -> FConstants.MODEL_MEDICINE_STANDARD_CODE
			4 -> FConstants.MODEL_MEDICINE_PHARMA
			5 -> FConstants.MODEL_MEDICINE_NAME
			6 -> FConstants.MODEL_MEDICINE_STANDARD
			7 -> FConstants.MODEL_MEDICINE_ACCOUNT_UNIT
			8 -> FConstants.MODEL_MEDICINE_CHARGE
			9 -> FConstants.MODEL_MEDICINE_CUSTOM_PRICE
			10 -> FConstants.MODEL_MEDICINE_TYPE
			11 -> FConstants.MODEL_MEDICINE_METHOD
			12 -> FConstants.MODEL_MEDICINE_CATEGORY
			13 -> FConstants.MODEL_MEDICINE_GROUP
			14 -> FConstants.MODEL_MEDICINE_DIV
			15 -> FConstants.MODEL_MEDICINE_RANK
			16 -> FConstants.MODEL_MEDICINE_STORAGE_TEMP
			17 -> FConstants.MODEL_MEDICINE_STORAGE_BOX
			18 -> FConstants.MODEL_MEDICINE_PACKAGE_UNIT
			19 -> FConstants.MODEL_MEDICINE_UNIT
			20 -> FConstants.MODEL_MEDICINE_ETC1
			21 -> FConstants.MODEL_MEDICINE_ETC2
			else -> ""
		}
	}
	fun errorCondition(): Boolean {
		if (code == 0) {
			return true
		} else if (name.isBlank()) {
			return true
		}
		return false
	}
	fun errorString() = "${FConstants.MODEL_MEDICINE_CODE} : ${code}\n${FConstants.MODEL_MEDICINE_NAME} : ${name}"
	fun insertString(): String {
		val mainIngredientCode = FExtensions.escapeString(mainIngredientCode)
		val pharmaName = FExtensions.escapeString(pharma)
		val name = FExtensions.escapeString(name)
		return "('$thisPK', '$code', '$mainIngredientCode', '$kdCode', '$standardCode', '$pharmaName', '$name', '$customPrice', '$charge', '$inVisible')"
	}
	fun insertSubString() = medicineSubModel.insertString()
}