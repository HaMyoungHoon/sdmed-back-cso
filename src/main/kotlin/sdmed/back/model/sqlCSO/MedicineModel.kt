package sdmed.back.model.sqlCSO

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import java.util.*

@Entity
@BatchSize(size = 20)
data class MedicineModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column
	var serialNumber: Int = 0,
	@Column(columnDefinition = "nvarchar(100)", nullable = false)
	var method: String = "",
	@Column(columnDefinition = "nvarchar(100)", nullable = false)
	var classify: String = "",
	@Column(columnDefinition = "nvarchar(100)", nullable = false)
	var mainIngredientCode: String = "",
	@Column(columnDefinition = "nvarchar(100)", nullable = false)
	var kdCode: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var name: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var pharmaName: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var standard: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var unit: String = "",
	@Column
	var general: Int = 0,
	@Transient
	var maxPrice: Int = 0,
	@Transient
	@JsonIgnore
	var etc: String = "",
	@Column
	var ancestorCode: Int = 0,
	@OneToMany(fetch = FetchType.LAZY, cascade = [CascadeType.ALL])
	@JoinColumn
	@JsonManagedReference(value = "medicinePriceManagedReference")
	var medicinePriceModel: MutableList<MedicinePriceModel> = mutableListOf(),
) {
	fun lazyHide() {
		medicinePriceModel.forEach { it.medicineModel = null }
		maxPrice = medicinePriceModel.maxByOrNull { it.applyDate }?.maxPrice ?: 0
	}
	fun findHeader(data: List<String>): Boolean {
		if (data.size < FConstants.MODEL_DRUG_COUNT) {
			return false
		}

		if (data[0] != titleGet(0) || data[1] != titleGet(1) || data[2] != titleGet(2) || data[3] != titleGet(3) ||
			data[4] != titleGet(4) || data[5] != titleGet(5) ||	data[6] != titleGet(6) || data[7] != titleGet(7) ||
			data[8] != titleGet(8) || data[9] != titleGet(9) || data[10] != titleGet(10) || data[11] != titleGet(11) ||
			data[12] != titleGet(12)) {
			return false
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
	fun indexGet(index: Int): String {
		return when (index) {
			0 -> serialNumber.toString()
			1 -> method
			2 -> classify
			3 -> mainIngredientCode
			4 -> kdCode
			5 -> name
			6 -> pharmaName
			7 -> standard
			8 -> unit
//			9 -> maxPrice.toString()
			10 -> general.toString()
//			11 -> etc
			12 -> ancestorCode.toString()
			else -> ""
		}
	}
	fun indexSet(data: String?, index: Int) {
		when (index) {
			0 -> serialNumber = data?.toIntOrNull() ?: 0
			1 -> method = data ?: ""
			2 -> classify = data ?: ""
			3 -> mainIngredientCode = data ?: ""
			4 -> kdCode = data ?: ""
			5 -> name = data ?: ""
			6 -> pharmaName = data ?: ""
			7 -> standard = data ?: ""
			8 -> unit = data ?: ""
			9 -> medicinePriceModel.add(MedicinePriceModel().apply {
				maxPrice = data?.toIntOrNull() ?: 0
				medicineModel = this@MedicineModel
			})
			10 -> general = if (data == "일반") 0 else 1
//			11 -> etc = data ?: ""
			12 -> ancestorCode = data?.toIntOrNull() ?: 0
		}
	}
	fun childDataSet(kdCode: String, etc: String, applyDate: Date) {
		medicinePriceModel.forEach { x ->
			x.kdCode = kdCode
			x.etc = etc
			x.applyDate = applyDate
		}
	}
	fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_DRUG_INDEX
			1 -> FConstants.MODEL_DRUG_METHOD
			2 -> FConstants.MODEL_DRUG_CLASSIFY
			3 -> FConstants.MODEL_DRUG_INGREDIENT_CODE
			4 -> FConstants.MODEL_DRUG_KD_CODE
			5 -> FConstants.MODEL_DRUG_NAME
			6 -> FConstants.MODEL_DRUG_PHARMA_NAME
			7 -> FConstants.MODEL_DRUG_STANDARD
			8 -> FConstants.MODEL_DRUG_UNIT
			9 -> FConstants.MODEL_DRUG_MAX_PRICE
			10 -> FConstants.MODEL_DRUG_GENERAL
			11 -> FConstants.MODEL_DRUG_ETC
			12 -> FConstants.MODEL_DRUG_ANCESTOR_CODE
			else -> ""
		}
	}
	fun errorCondition(): Boolean {
		if (indexGet(3).isEmpty()) {
			return true
		} else if (indexGet(4).isEmpty()) {
			return true
		} else if (indexGet(5).isEmpty()) {
			return true
		} else if (indexGet(6).isEmpty()) {
			return true
		} else if (indexGet(7).isEmpty()) {
			return true
		} else if (indexGet(8).isEmpty()) {
			return true
			// 0 원이 있나? 있네 시벌
//		} else if (getIndex(9) == "0") {
//			return true
		} else if (indexGet(10).isEmpty()) {
			return true
//		} else if (getIndex(11).isEmpty()) {
//			return true
//		} else if (getIndex(12) == "0") {
//			return true
		}
		return false
	}
	fun errorString(): String {
		val ret = StringBuilder()
		for (i in 0 until FConstants.MODEL_DRUG_COUNT) {
			ret.append("${titleGet(i)} : ${indexGet(i)}\n")
		}
		return ret.toString()
	}
	fun insertString(): String {
		val method = FExtensions.escapeString(method)
		val classify = FExtensions.escapeString(classify)
		val mainIngredientCode = FExtensions.escapeString(mainIngredientCode)
		val kdCode = FExtensions.escapeString(kdCode)
		val name = FExtensions.escapeString(name)
		val pharmaName = FExtensions.escapeString(pharmaName)
		val standard = FExtensions.escapeString(standard)
		val unit = FExtensions.escapeString(unit)
//		val etc = FExtensions.escapeString(etc)
		return "('$thisPK', '$serialNumber', '$method', '$classify', '$mainIngredientCode', '$kdCode', '$name', '$pharmaName', '$standard', '$unit', '$general', '$ancestorCode')"
	}
}