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
	var clientName: String? = null,
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
	constructor(buff: MedicineModel, makerName: String?, clientName: String?) : this() {
		this.thisPK = buff.thisPK
		this.code = buff.code
		this.mainIngredientCode = buff.mainIngredientCode
		this.kdCode = buff.kdCode
		this.standardCode = buff.standardCode
		this.clientName = clientName
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
			1 -> orgName = data ?: ""
			2 -> innerName = data ?: ""
			3 -> kdCode = data ?: ""
			4 -> customPrice = data?.toIntOrNull() ?: 0
			5 -> charge = data?.toIntOrNull() ?: 50
			6 -> medicineSubModel.standard = data ?: ""
			7 -> medicineSubModel.etc1 = data ?: ""
			8 -> mainIngredientCode = data ?: ""
			9 -> {
				code = data ?: ""
				medicineSubModel.code = code
			}
			10 -> medicineSubModel.accountUnit = data?.toDoubleOrNull() ?: 0.0
			11 -> medicineSubModel.medicineType = MedicineType.parseString(data)
			12 -> medicineSubModel.medicineMethod = MedicineMethod.parseString(data)
			13 -> medicineSubModel.medicineCategory = MedicineCategory.parseString(data)
			14 -> medicineSubModel.medicineGroup = MedicineGroup.parseString(data)
			15 -> medicineSubModel.medicineDiv = MedicineDiv.parseString(data)
			16 -> medicineSubModel.medicineStorageTemp = MedicineStorageTemp.parseString(data)
			17 -> medicineSubModel.medicineStorageBox = MedicineStorageBox.parseString(data)
			18 -> medicineSubModel.medicineRank = MedicineRank.parseString(data)
			19 -> standardCode = data?.toLongOrNull() ?: 0
			20 -> medicineSubModel.packageUnit = data?.toIntOrNull() ?: 0
			21 -> medicineSubModel.unit = data ?: ""
			22 -> medicineSubModel.etc2 = data ?: ""
			23 -> makerCode = data ?: ""
		}
	}
	override fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_MEDICINE_MAKER_NAME
			1 -> FConstants.MODEL_MEDICINE_NAME
			2 -> FConstants.MODEL_MEDICINE_INNER_NAME
			3 -> FConstants.MODEL_MEDICINE_KD_CODE
			4 -> FConstants.MODEL_MEDICINE_CUSTOM_PRICE
			5 -> FConstants.MODEL_MEDICINE_CHARGE
			6 -> FConstants.MODEL_MEDICINE_STANDARD
			7 -> FConstants.MODEL_MEDICINE_ETC1
			8 -> FConstants.MODEL_MEDICINE_MAIN_INGREDIENT_CODE
			9 -> FConstants.MODEL_MEDICINE_CODE
			10 -> FConstants.MODEL_MEDICINE_ACCOUNT_UNIT
			11 -> FConstants.MODEL_MEDICINE_TYPE
			12 -> FConstants.MODEL_MEDICINE_METHOD
			13 -> FConstants.MODEL_MEDICINE_CATEGORY
			14 -> FConstants.MODEL_MEDICINE_GROUP
			15 -> FConstants.MODEL_MEDICINE_DIV
			16 -> FConstants.MODEL_MEDICINE_STORAGE_TEMP
			17 -> FConstants.MODEL_MEDICINE_STORAGE_BOX
			18 -> FConstants.MODEL_MEDICINE_RANK
			19 -> FConstants.MODEL_MEDICINE_STANDARD_CODE
			20 -> FConstants.MODEL_MEDICINE_PACKAGE_UNIT
			21 -> FConstants.MODEL_MEDICINE_UNIT
			22 -> FConstants.MODEL_MEDICINE_ETC2
			23 -> FConstants.MODEL_MEDICINE_MAKER_CODE
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
		val innerName = FExtensions.escapeString(innerName)
		val orgName = FExtensions.escapeString(orgName)
		return "('$thisPK', '$code', '$mainIngredientCode', '$kdCode', '$standardCode', '$makerCode', '$innerName', '$orgName', '$customPrice', '$charge', ${if (inVisible) 1 else 0})"
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