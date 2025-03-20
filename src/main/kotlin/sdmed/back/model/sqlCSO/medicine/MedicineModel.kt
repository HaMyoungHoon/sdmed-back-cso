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
	@Transient
	var makerName: String? = null,
	@Column(columnDefinition = "text")
	var orgName: String = "",
	@Column(columnDefinition = "text")
	var innerName: String = "",
	@Column(columnDefinition = "nvarchar(20)")
	var kdCode: String = "",
	@Column
	var customPrice: Int = 0,
	@Column(nullable = false)
	@ColumnDefault("50")
	var charge: Int = 50,
	@Column(columnDefinition = "nvarchar(100)")
	var standard: String = "",
	@Column(columnDefinition = "text")
	var etc1: String = "",
	@Column(columnDefinition = "nvarchar(255)")
	var mainIngredientCode: String = "",
	@Column(columnDefinition = "nvarchar(50)", nullable = false, unique = true)
	var code: String = "",
	@Column(columnDefinition = "nvarchar(50)", nullable = false)
	var makerCode: String = "",
	@Column
	var medicineDiv: MedicineDiv = MedicineDiv.Open,
	@Column(columnDefinition = "bit default 0", nullable = false)
	var inVisible: Boolean = false,
	@Transient
	var clientName: String? = null,
	@Transient
	var maxPrice: Int = 0,
	@Transient
	var medicineIngredientModel: MedicineIngredientModel = MedicineIngredientModel(),
	@Transient
	var medicinePriceModel: MutableList<MedicinePriceModel> = mutableListOf(),
): FExcelParseModel() {
	constructor(buff: MedicineModel, makerName: String?, clientName: String?) : this() {
		this.thisPK = buff.thisPK
		this.makerName = makerName
		this.orgName = buff.orgName
		this.innerName = buff.innerName
		this.kdCode = buff.kdCode
		this.customPrice = buff.customPrice
		this.charge = buff.charge
		this.standard = buff.standard
		this.etc1 = buff.etc1
		this.mainIngredientCode = buff.mainIngredientCode
		this.code = buff.code
		this.makerCode = buff.makerCode
		this.medicineDiv = buff.medicineDiv
		this.inVisible = buff.inVisible
		this.clientName = clientName
		this.maxPrice = buff.maxPrice
		this.medicineIngredientModel = buff.medicineIngredientModel
		this.medicinePriceModel = buff.medicinePriceModel
	}
	@Transient
	override var dataCount = FConstants.MODEL_MEDICINE_COUNT
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
			6 -> standard = data ?: ""
			7 -> etc1 = data ?: ""
			8 -> mainIngredientCode = data ?: ""
			9 -> code = data ?: ""
			10 -> makerCode = data ?: ""
			11 -> medicineDiv = MedicineDiv.parseString(data)
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
			23 -> FConstants.MODEL_MEDICINE_MAKER_CODE
			15 -> FConstants.MODEL_MEDICINE_DIV
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
		return "('$thisPK', '$orgName', '$innerName', '$kdCode', '$customPrice', '$charge', '$standard', '$etc1', '$mainIngredientCode', '$code', '$makerCode', '${medicineDiv.index}', ${if (inVisible) 1 else 0})"
	}
	fun safeCopy(rhs: MedicineModel): MedicineModel {
		this.mainIngredientCode = rhs.mainIngredientCode
		this.kdCode = rhs.kdCode
		this.standard = rhs.standard
		this.makerCode = rhs.makerCode
		this.orgName = rhs.orgName
		this.innerName = rhs.innerName
		this.customPrice = rhs.customPrice
		this.charge = rhs.charge
		this.etc1 = rhs.etc1
		this.medicineDiv = rhs.medicineDiv
		return this
	}
}