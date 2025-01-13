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
	@Column(columnDefinition = "nvarchar(50)", nullable = false, unique = true)
	var code: String = "",
	@Column(columnDefinition = "nvarchar(255)")
	var mainIngredientCode: String = "",
	@Column(columnDefinition = "nvarchar(20)")
	var kdCode: String = "",
	@Column
	var standardCode: Long = 0L,
	@Column(columnDefinition = "nvarchar(50)", nullable = false)
	var makerCode: String = "",
	// mysql
	@Column(columnDefinition = "text")
//	@Column(columnDefinition = "nvarchar(max)")
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
				code = data ?: ""
				medicineSubModel.code = code
			}
			1 -> mainIngredientCode = data ?: ""
			2 -> kdCode = data ?: ""
			3 -> standardCode = data?.toLongOrNull() ?: 0
			5 -> makerCode = data ?: ""
			6 -> name = data ?: ""
			7 -> medicineSubModel.standard = data ?: ""
			8 -> medicineSubModel.accountUnit = data?.toDoubleOrNull() ?: 0.0
			9 -> charge = data?.toIntOrNull() ?: 50
			10 -> customPrice = data?.toIntOrNull() ?: 0
			11 -> medicineSubModel.medicineType = MedicineType.parseString(data)
			12 -> medicineSubModel.medicineMethod = MedicineMethod.parseString(data)
			13 -> medicineSubModel.medicineCategory = MedicineCategory.parseString(data)
			14 -> medicineSubModel.medicineGroup = MedicineGroup.parseString(data)
			15 -> medicineSubModel.medicineDiv = MedicineDiv.parseString(data)
			16 -> medicineSubModel.medicineRank = MedicineRank.parseString(data)
			17 -> medicineSubModel.medicineStorageTemp = MedicineStorageTemp.parseString(data)
			18 -> medicineSubModel.medicineStorageBox = MedicineStorageBox.parseString(data)
			19 -> medicineSubModel.packageUnit = data?.toIntOrNull() ?: 0
			20 -> medicineSubModel.unit = data ?: ""
			21 -> medicineSubModel.etc1 = data ?: ""
			22 -> medicineSubModel.etc2 = data ?: ""
		}
	}
	fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_MEDICINE_CODE
			1 -> FConstants.MODEL_MEDICINE_MAIN_INGREDIENT_CODE
			2 -> FConstants.MODEL_MEDICINE_KD_CODE
			3 -> FConstants.MODEL_MEDICINE_STANDARD_CODE
			4 -> FConstants.MODEL_MEDICINE_MAKER_NAME
			5 -> FConstants.MODEL_MEDICINE_MAKER_CODE
			6 -> FConstants.MODEL_MEDICINE_NAME
			7 -> FConstants.MODEL_MEDICINE_STANDARD
			8 -> FConstants.MODEL_MEDICINE_ACCOUNT_UNIT
			9 -> FConstants.MODEL_MEDICINE_CHARGE
			10 -> FConstants.MODEL_MEDICINE_CUSTOM_PRICE
			11 -> FConstants.MODEL_MEDICINE_TYPE
			12 -> FConstants.MODEL_MEDICINE_METHOD
			13 -> FConstants.MODEL_MEDICINE_CATEGORY
			14 -> FConstants.MODEL_MEDICINE_GROUP
			15 -> FConstants.MODEL_MEDICINE_DIV
			16 -> FConstants.MODEL_MEDICINE_RANK
			17 -> FConstants.MODEL_MEDICINE_STORAGE_TEMP
			18 -> FConstants.MODEL_MEDICINE_STORAGE_BOX
			19 -> FConstants.MODEL_MEDICINE_PACKAGE_UNIT
			20 -> FConstants.MODEL_MEDICINE_UNIT
			21 -> FConstants.MODEL_MEDICINE_ETC1
			22 -> FConstants.MODEL_MEDICINE_ETC2
			else -> ""
		}
	}
	fun errorCondition(): Boolean {
		if (code.isBlank()) {
			return true
		} else if (name.isBlank()) {
			return true
		}
		return false
	}
	fun errorString() = "${FConstants.MODEL_MEDICINE_CODE} : ${code}\n${FConstants.MODEL_MEDICINE_NAME} : ${name}"
	fun insertString(): String {
		val mainIngredientCode = FExtensions.escapeString(mainIngredientCode)
		val name = FExtensions.escapeString(name)
		return "('$thisPK', '$code', '$mainIngredientCode', '$kdCode', '$standardCode', '$makerCode', '$name', '$customPrice', '$charge', ${if (inVisible) 1 else 0})"
	}
	fun insertSubString() = medicineSubModel.insertString()
}