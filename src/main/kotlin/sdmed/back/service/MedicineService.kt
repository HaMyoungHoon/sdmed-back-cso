package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import sdmed.back.config.FConstants
import sdmed.back.config.FServiceBase
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.medicine.MedicineIngredientModel
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import sdmed.back.model.sqlCSO.medicine.MedicinePriceModel
import sdmed.back.repository.sqlCSO.IMedicineIngredientRepository
import sdmed.back.repository.sqlCSO.IMedicinePriceRepository
import sdmed.back.repository.sqlCSO.IMedicineRepository
import java.util.stream.Collectors.joining

open class MedicineService: FServiceBase() {
	@Autowired lateinit var medicineRepository: IMedicineRepository
	@Autowired lateinit var medicineIngredientRepository: IMedicineIngredientRepository
	@Autowired lateinit var medicinePriceRepository: IMedicinePriceRepository

	protected fun getAllMedicine(token: String, withAllPrice: Boolean = false): List<MedicineModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)
		val ret = if (haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			medicineRepository.selectAllByInvisibleOrderByCode()
		} else {
			medicineRepository.selectAllByInvisibleOpenOrderByCode()
		}
		val ingredient = medicineIngredientRepository.findAllByOrderByMainIngredientCode()
		medicineMerge(ret, ingredient)
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
	protected fun insertMedicineAll(data: List<MedicineModel>): Int {
		if (data.isEmpty()) {
			return 0
		}
		val values = data.stream().map{it.insertString()}.collect(joining(","))
		val sqlString = "${FConstants.MODEL_MEDICINE_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	protected fun updateMedicineAll(data: List<MedicineModel>): Int {
		if (data.isEmpty()) {
			return 0
		}
		val ret = data.size
		data.forEach { x ->
			entityManager.merge(x)
		}
		entityManager.flush()
		entityManager.clear()
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

	protected fun medicineMerge(mother: List<MedicineModel>, ingredient: List<MedicineIngredientModel>) {
		val ingredientMap = ingredient.associateBy { it.mainIngredientCode }
		mother.map { x ->
			ingredientMap[x.mainIngredientCode]?.let { y -> x.medicineIngredientModel = y }
		}
	}
}