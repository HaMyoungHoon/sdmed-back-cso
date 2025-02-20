package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import sdmed.back.config.FConstants
import sdmed.back.config.FServiceBase
import sdmed.back.model.sqlCSO.medicine.MedicineIngredientModel
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import sdmed.back.model.sqlCSO.medicine.MedicinePriceModel
import sdmed.back.model.sqlCSO.medicine.MedicineSubModel
import sdmed.back.repository.sqlCSO.IMedicineIngredientRepository
import sdmed.back.repository.sqlCSO.IMedicinePriceRepository
import sdmed.back.repository.sqlCSO.IMedicineRepository
import sdmed.back.repository.sqlCSO.IMedicineSubRepository
import java.util.stream.Collectors.joining

open class MedicineService: FServiceBase() {
	@Autowired lateinit var medicineRepository: IMedicineRepository
	@Autowired lateinit var medicineSubRepository: IMedicineSubRepository
	@Autowired lateinit var medicineIngredientRepository: IMedicineIngredientRepository
	@Autowired lateinit var medicinePriceRepository: IMedicinePriceRepository

	protected fun getAllMedicine(token: String, withAllPrice: Boolean = false): List<MedicineModel> {
		isValid(token)
		val ret = medicineRepository.selectAllByInvisibleAndMakerNameOrderByCode()
		val sub = medicineSubRepository.findAllByOrderByCode()
		val ingredient = medicineIngredientRepository.findAllByOrderByMainIngredientCode()
		medicineMerge(ret, sub, ingredient)
		if (withAllPrice) {
			val allPrice = medicinePriceRepository.findAllByOrderByApplyDateDesc()
			medicinePriceMerge(ret, allPrice)
		} else {
			val recentPrice = medicinePriceRepository.selectAllByRecentData()
			medicinePriceMerge(ret, recentPrice)
		}
		ret.onEach { it.init() }
		return ret
	}
	protected fun medicinePriceMerge(mother: List<MedicineModel>, price: List<MedicinePriceModel>) {
		val priceMap = price.groupBy { it.kdCode }
		mother.map { x ->
			priceMap[x.kdCode]?.let { y -> x.medicinePriceModel = y.toMutableList() }
		}
	}
	protected fun mergeMedicinePrice(lhsList: List<MedicinePriceModel>, rhsList: List<MedicinePriceModel>, newData: MutableList<MedicinePriceModel>) {
		val lhsMap = lhsList.associateBy { it.kdCode }
		for (rhs in rhsList) {
			val lhs = lhsMap[rhs.kdCode]
			if (lhs != null) {
				if (lhs.maxPrice != rhs.maxPrice) {
					newData.add(rhs)
				}
			} else {
				newData.add(rhs)
			}
		}
	}
	protected fun insertMedicineAll(data: List<MedicineModel>, withSub: Boolean = true): Int {
		if (data.isEmpty()) {
			return 0
		}
		val values = data.stream().map{it.insertString()}.collect(joining(","))
		val sqlString = "${FConstants.MODEL_MEDICINE_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		if (withSub) {
			insertMedicineSubAll(data.map { it.medicineSubModel }.filter { it.code.isNotBlank() })
		}
		return ret
	}
	protected fun insertMedicineSubAll(data: List<MedicineSubModel>): Int {
		if (data.isEmpty()) {
			return 0
		}
		val already = medicineSubRepository.findALlByCodeInOrderByCode(data.map { it.code }).distinctBy { it.thisPK }.filter { it.code.isNotBlank() }
		val saveList = data.filterNot { x -> x.code in already.map { y -> y.code } }
		var ret = 0
		if (saveList.isNotEmpty()) {
			ret = medicineSubRepository.saveAll(saveList).size
		}
		if (already.isNotEmpty()) {
			val buffMap = data.associateBy { it.code }
			if (already.isNotEmpty()) {
				already.forEach { x ->
					val buff = buffMap[x.code]
					if (buff != null) {
						x.safeCopy(buff)
					}
				}
			}
			ret += already.size
			already.forEach { x ->
				entityManager.merge(x)
			}
			entityManager.flush()
			entityManager.clear()
		}
		return ret
//		val values = data.stream().map{it.insertString()}.collect(joining(","))
//		val sqlString = "${FConstants.MODEL_MEDICINE_SUB_INSERT_INTO}$values"
//		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
//		entityManager.flush()
//		entityManager.clear()
//		return ret
	}
	protected fun updateMedicineAll(data: List<MedicineModel>, withSub: Boolean = true): Int {
		if (data.isEmpty()) {
			return 0
		}
		val ret = data.size
		data.forEach { x ->
			entityManager.merge(x)
		}
		entityManager.flush()
		entityManager.clear()
		if (withSub) {
			insertMedicineSubAll(data.map { it.medicineSubModel }.filter { it.code.isNotBlank() })
		}
		return ret
	}
	protected fun insertMedicineIngredientAll(data: List<MedicineIngredientModel>): Int {
		val values = data.stream().map{it.insertString()}.collect(joining(","))
		val sqlString = "${FConstants.MODEL_MEDICINE_INGREDIENT_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	protected fun insertMedicinePriceAll(data: List<MedicinePriceModel>): Int {
		val values = data.stream().map {it.insertString()}.collect(joining(","))
		val sqlString = "${FConstants.MODEL_MEDICINE_PRICE_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
}