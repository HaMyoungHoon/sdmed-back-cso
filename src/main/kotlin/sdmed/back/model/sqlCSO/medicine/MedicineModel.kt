package sdmed.back.model.sqlCSO.medicine

import jakarta.persistence.*
import org.hibernate.annotations.ColumnDefault
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.model.common.medicine.*
import sdmed.back.model.sqlCSO.FExcelParseModel
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
	@Transient
	var makerName: String? = null,
	@Column(columnDefinition = "nvarchar(50)", nullable = false)
	var makerCode: String = "",
	// mysql
	@Column(columnDefinition = "text")
//	@Column(columnDefinition = "nvarchar(max)")
	var orgName: String = "",
	@Column(columnDefinition = "text")
	var innerName: String = "",
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
): FExcelParseModel() {
	constructor(buff: MedicineModel, makerName: String?) : this() {
		this.thisPK = buff.thisPK
		this.code = buff.code
		this.mainIngredientCode = buff.mainIngredientCode
		this.kdCode = buff.kdCode
		this.standardCode = buff.standardCode
		this.makerName = makerName
		this.makerCode = buff.makerCode
		this.orgName = buff.orgName
		this.innerName = buff.innerName
		this.customPrice = buff.customPrice
		this.charge = buff.charge
		this.inVisible = buff.inVisible
		this.maxPrice = buff.maxPrice
		this.medicineSubModel = buff.medicineSubModel
		this.medicineIngredientModel = buff.medicineIngredientModel
		this.medicinePriceModel = buff.medicinePriceModel
	}
	@Transient
	override var dataCount = FConstants.MODEL_MEDICINE_COUNT
	fun genSub() {
		medicineSubModel.thisPK = UUID.randomUUID().toString()
		medicineSubModel.code = code
	}
	fun init() {
		maxPrice = medicinePriceModel.maxByOrNull { it.applyDate }?.maxPrice ?: customPrice
	}
	override fun indexSet(data: String?, index: Int) {
		when (index) {
			0 -> {
				code = data ?: ""
				medicineSubModel.code = code
			}
			1 -> mainIngredientCode = data ?: ""
			2 -> kdCode = data ?: ""
			3 -> standardCode = data?.toLongOrNull() ?: 0
			5 -> makerCode = data ?: ""
			6 -> orgName = data ?: ""
			7 -> innerName = data ?: ""
			8 -> medicineSubModel.standard = data ?: ""
			9 -> medicineSubModel.accountUnit = data?.toDoubleOrNull() ?: 0.0
			10 -> charge = data?.toIntOrNull() ?: 50
			11 -> customPrice = data?.toIntOrNull() ?: 0
			12 -> medicineSubModel.medicineType = MedicineType.parseString(data)
			13 -> medicineSubModel.medicineMethod = MedicineMethod.parseString(data)
			14 -> medicineSubModel.medicineCategory = MedicineCategory.parseString(data)
			15 -> medicineSubModel.medicineGroup = MedicineGroup.parseString(data)
			16 -> medicineSubModel.medicineDiv = MedicineDiv.parseString(data)
			17 -> medicineSubModel.medicineRank = MedicineRank.parseString(data)
			18 -> medicineSubModel.medicineStorageTemp = MedicineStorageTemp.parseString(data)
			19 -> medicineSubModel.medicineStorageBox = MedicineStorageBox.parseString(data)
			20 -> medicineSubModel.packageUnit = data?.toIntOrNull() ?: 0
			21 -> medicineSubModel.unit = data ?: ""
			22 -> medicineSubModel.etc1 = data ?: ""
			23 -> medicineSubModel.etc2 = data ?: ""
		}
	}
	override fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_MEDICINE_CODE
			1 -> FConstants.MODEL_MEDICINE_MAIN_INGREDIENT_CODE
			2 -> FConstants.MODEL_MEDICINE_KD_CODE
			3 -> FConstants.MODEL_MEDICINE_STANDARD_CODE
			4 -> FConstants.MODEL_MEDICINE_MAKER_NAME
			5 -> FConstants.MODEL_MEDICINE_MAKER_CODE
			6 -> FConstants.MODEL_MEDICINE_NAME
			7 -> FConstants.MODEL_MEDICINE_INNER_NAME
			8 -> FConstants.MODEL_MEDICINE_STANDARD
			9 -> FConstants.MODEL_MEDICINE_ACCOUNT_UNIT
			10 -> FConstants.MODEL_MEDICINE_CHARGE
			11 -> FConstants.MODEL_MEDICINE_CUSTOM_PRICE
			12 -> FConstants.MODEL_MEDICINE_TYPE
			13 -> FConstants.MODEL_MEDICINE_METHOD
			14 -> FConstants.MODEL_MEDICINE_CATEGORY
			15 -> FConstants.MODEL_MEDICINE_GROUP
			16 -> FConstants.MODEL_MEDICINE_DIV
			17 -> FConstants.MODEL_MEDICINE_RANK
			18 -> FConstants.MODEL_MEDICINE_STORAGE_TEMP
			19 -> FConstants.MODEL_MEDICINE_STORAGE_BOX
			20 -> FConstants.MODEL_MEDICINE_PACKAGE_UNIT
			21 -> FConstants.MODEL_MEDICINE_UNIT
			22 -> FConstants.MODEL_MEDICINE_ETC1
			23 -> FConstants.MODEL_MEDICINE_ETC2
			else -> ""
		}
	}
	override fun errorCondition(): Boolean {
		if (code.isBlank()) {
			return true
		} else if (orgName.isBlank()) {
			return true
		} else if (innerName.isBlank()) {
			return true
		}
		return false
	}
	override fun errorString() = "${FConstants.MODEL_MEDICINE_CODE} : ${code}\n${FConstants.MODEL_MEDICINE_NAME} : ${orgName}"
	fun insertString(): String {
		val mainIngredientCode = FExtensions.escapeString(mainIngredientCode)
		val name = FExtensions.escapeString(orgName)
		val innerName = FExtensions.escapeString(innerName)
		return "('$thisPK', '$code', '$mainIngredientCode', '$kdCode', '$standardCode', '$makerCode', '$name', '$innerName', '$customPrice', '$charge', ${if (inVisible) 1 else 0})"
	}
	fun insertSubString() = medicineSubModel.insertString()

	fun safeCopy(rhs: MedicineModel): MedicineModel {
		this.mainIngredientCode = rhs.mainIngredientCode
		this.kdCode = rhs.kdCode
		this.standardCode = rhs.standardCode
		this.makerCode = rhs.makerCode
		this.orgName = rhs.orgName
		this.innerName = rhs.innerName
		this.customPrice = rhs.customPrice
		this.charge = rhs.charge
		return this
	}
}