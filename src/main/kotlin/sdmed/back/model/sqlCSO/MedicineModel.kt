package sdmed.back.model.sqlCSO

import jakarta.persistence.*
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.model.common.*
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
	@Column(columnDefinition = "text")
	var pharma: String = "",
	@Column(columnDefinition = "text")
	var name: String = "",
	@Column
	var customPrice: Int = 0,
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
			8 -> customPrice = data?.toIntOrNull() ?: 0
			9 -> medicineSubModel.medicineType = MedicineType.parseString(data)
			10 -> medicineSubModel.medicineMethod = MedicineMethod.parseString(data)
			11 -> medicineSubModel.medicineCategory = MedicineCategory.parseString(data)
			12 -> medicineSubModel.medicineGroup = MedicineGroup.parseString(data)
			13 -> medicineSubModel.medicineDiv = MedicineDiv.parseString(data)
			14 -> medicineSubModel.medicineRank = MedicineRank.parseString(data)
			15 -> medicineSubModel.medicineStorageTemp = MedicineStorageTemp.parseString(data)
			16 -> medicineSubModel.medicineStorageBox = MedicineStorageBox.parseString(data)
			17 -> medicineSubModel.packageUnit = data?.toIntOrNull() ?: 0
			18 -> medicineSubModel.unit = data ?: ""
			19 -> medicineSubModel.etc1 = data ?: ""
			20 -> medicineSubModel.etc2 = data ?: ""
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
			8 -> FConstants.MODEL_MEDICINE_CUSTOM_PRICE
			9 -> FConstants.MODEL_MEDICINE_TYPE
			10 -> FConstants.MODEL_MEDICINE_METHOD
			11 -> FConstants.MODEL_MEDICINE_CATEGORY
			12 -> FConstants.MODEL_MEDICINE_GROUP
			13 -> FConstants.MODEL_MEDICINE_DIV
			14 -> FConstants.MODEL_MEDICINE_RANK
			15 -> FConstants.MODEL_MEDICINE_STORAGE_TEMP
			16 -> FConstants.MODEL_MEDICINE_STORAGE_BOX
			17 -> FConstants.MODEL_MEDICINE_PACKAGE_UNIT
			18 -> FConstants.MODEL_MEDICINE_UNIT
			19 -> FConstants.MODEL_MEDICINE_ETC1
			20 -> FConstants.MODEL_MEDICINE_ETC2
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
		return "('$thisPK', '$code', '$mainIngredientCode', '$kdCode', '$standardCode', '$pharmaName', '$name', '$customPrice', '$inVisible')"
	}
	fun insertSubString() = medicineSubModel.insertString()
}