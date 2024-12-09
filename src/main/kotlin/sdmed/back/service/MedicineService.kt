package sdmed.back.service

import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.*
import sdmed.back.config.FConstants
import sdmed.back.config.FExcelFileParser
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.config.security.JwtTokenProvider
import sdmed.back.model.common.UserRole
import sdmed.back.model.common.UserRole.Companion.getFlag
import sdmed.back.model.common.UserRoles
import sdmed.back.model.common.UserStatus
import sdmed.back.model.sqlCSO.*
import sdmed.back.repository.sqlCSO.*
import java.util.*
import java.util.stream.Collectors.joining

@Service
class MedicineService {
	@Autowired lateinit var jwtTokenProvider: JwtTokenProvider
	@Autowired lateinit var logRepository: ILogRepository
	@Autowired lateinit var excelFileParser: FExcelFileParser
	@Autowired lateinit var userDataRepository: IUserDataRepository
	@Autowired lateinit var medicineRepository: IMedicineRepository
	@Autowired lateinit var medicineSubRepository: IMedicineSubRepository
	@Autowired lateinit var medicineIngredientRepository: IMedicineIngredientRepository
	@Autowired lateinit var medicinePriceRepository: IMedicinePriceRepository
	@Autowired lateinit var entityManager: EntityManager

	fun getAllMedicine(token: String, withAllPrice: Boolean = false): List<MedicineModel> {
		isValid(token)
		val ret = medicineRepository.findAllByOrderByCode()
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
	fun getMedicineSearch(token: String, searchString: String, isSearchTypeCode: Boolean = true): List<MedicineModel> {
		if (searchString.isEmpty()) {
			return arrayListOf()
		}

		isValid(token)
		val ret = if (isSearchTypeCode) {
			searchString.toIntOrNull()?.let { x ->
				medicineRepository.selectAllByCodeLikeOrKdCodeLike(x, x)
			} ?: medicineRepository.selectAllByNameContainingOrPharmaContaining(searchString, searchString)
		} else {
			medicineRepository.selectAllByNameContainingOrPharmaContaining(searchString, searchString)
		}
		val sub = medicineSubRepository.findALlByCodeInOrderByCode(ret.map { it.code })
		val ingredient = medicineIngredientRepository.findAllByMainIngredientCodeIn(ret.map { it.mainIngredientCode })
		medicineMerge(ret, sub, ingredient)
		return ret
	}
	fun getMedicinePriceList(token: String, kdCode: Int): List<MedicinePriceModel> {
		isValid(token)
		return medicinePriceRepository.findAllByKdCodeOrderByApplyDateDesc(kdCode)
	}
	fun getMedicineData(token: String, thisPK: String, withAllPrice: Boolean = false): MedicineModel {
		isValid(token)

		val ret = medicineRepository.findByThisPK(thisPK) ?: throw MedicineNotFoundException()
		medicineSubRepository.findByCode(ret.code)?.let { ret.medicineSubModel = it }
		medicineIngredientRepository.findByMainIngredientCode(ret.mainIngredientCode)?.let { ret.medicineIngredientModel = it }
		ret.medicinePriceModel = if (withAllPrice) {
			medicinePriceRepository.findAllByKdCodeOrderByApplyDateDesc(ret.kdCode).toMutableList()
		} else {
			medicinePriceRepository.findAllByKdCodeOrderByApplyDateDesc(ret.kdCode).toMutableList()
		}
		ret.init()
		return ret
	}
	private fun medicineMerge(mother: List<MedicineModel>, sub: List<MedicineSubModel>, ingredient: List<MedicineIngredientModel>) {
		val subMap = sub.associateBy { it.code }
		mother.map { x ->
			subMap[x.code]?.let { y -> x.medicineSubModel = y }
		}
		val ingredientMap = ingredient.associateBy { it.mainIngredientCode }
		mother.map { x ->
			ingredientMap[x.mainIngredientCode]?.let { y -> x.medicineIngredientModel = y }
		}
	}
	private fun medicinePriceMerge(mother: List<MedicineModel>, price: List<MedicinePriceModel>) {
		val priceMap = price.groupBy { it.kdCode }
		mother.map { x ->
			priceMap[x.kdCode]?.let { y -> x.medicinePriceModel = y.toMutableList() }
		}
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun medicineDataModify(token: String, data: MedicineModel): MedicineModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.MedicineChanger))) {
			throw AuthenticationEntryPointException()
		}

		val ret = medicineRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${data.name} modify")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun addMedicineData(token: String, data: MedicineModel): MedicineModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.MedicineChanger))) {
			throw AuthenticationEntryPointException()
		}

		val exist = medicineRepository.findByCode(data.code)
		if (exist != null) {
			throw MedicineExistException()
		}

		data.thisPK = UUID.randomUUID().toString()
		val ret = medicineRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add medicine : ${data.thisPK}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun medicineUpload(token: String, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.MedicineChanger))) {
			throw AuthenticationEntryPointException()
		}

		val excelModel = excelFileParser.medicineUploadExcelParse(tokenUser.id, file)
		val already: MutableList<MedicineModel> = mutableListOf()
		excelModel.chunked(500).forEach { x-> already.addAll(medicineRepository.findAllByCodeIn(x.map { y -> y.code })) }
		excelModel.removeIf { x -> x.code in already.map { y -> y.code } }
		if (excelModel.isEmpty()) {
			return "count : 0"
		}

		var retCount = 0
		excelModel.chunked(500).forEach { retCount += insertMedicineAll(it) }
		if (retCount == 0) {
			return "count : $retCount"
		}

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add medicine count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun medicinePriceUpload(token: String, applyDate: Date, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.MedicineChanger))) {
			throw AuthenticationEntryPointException()
		}

		val excelModel = excelFileParser.medicinePriceUploadExcelParse(tokenUser.id, applyDate, file)
		val already = medicinePriceRepository.selectAllByRecentData()
		val newData: MutableList<MedicinePriceModel> = mutableListOf()
		mergeMedicinePrice(already, excelModel, newData)
		if (excelModel.isEmpty()) {
			return "count : 0"
		}

		var retCount = 0
		newData.chunked(500).forEach { retCount += insertMedicinePriceAll(it) }
		if (retCount == 0) {
			return "count : $retCount"
		}

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add medicine price count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun medicineIngredientUpload(token: String, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.MedicineChanger))) {
			throw AuthenticationEntryPointException()
		}

		val excelModel = excelFileParser.medicineIngredientUploadExcelParse(tokenUser.id, file)
		val already: MutableList<MedicineIngredientModel> = mutableListOf()
		excelModel.chunked(500).forEach { x-> already.addAll(medicineIngredientRepository.findAllByMainIngredientCodeIn(x.map { y -> y.mainIngredientCode })) }
		excelModel.removeIf { x -> x.mainIngredientCode in already.map { y -> y.mainIngredientCode } }
		if (excelModel.isEmpty()) {
			return "count : 0"
		}

		var retCount = 0
		excelModel.distinctBy { x -> x.mainIngredientCode }.chunked(500).forEach { retCount += insertMedicineIngredientAll(it) }
		if (retCount == 0) {
			return "count : $retCount"
		}

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add medicine count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}

	private fun mergeMedicinePrice(lhsList: List<MedicinePriceModel>, rhsList: List<MedicinePriceModel>, newData: MutableList<MedicinePriceModel>) {
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

	private fun insertMedicineAll(data: List<MedicineModel>, withSub: Boolean = true): Int {
		val values = data.stream().map(this::renderSqlForInsertMedicineModel).collect(joining(","))
		val sqlString = "${FConstants.MODEL_MEDICINE_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		if (withSub) {
			insertMedicineSubAll(data.map { it.medicineSubModel })
		}
		return ret
	}
	private fun insertMedicineSubAll(data: List<MedicineSubModel>): Int {
		val values = data.stream().map(this::renderSqlForInsertMedicineSubModel).collect(joining(","))
		val sqlString = "${FConstants.MODEL_MEDICINE_SUB_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun insertMedicineIngredientAll(data: List<MedicineIngredientModel>): Int {
		val values = data.stream().map(this::renderSqlForInsertMedicineIngredientModel).collect(joining(","))
		val sqlString = "${FConstants.MODEL_MEDICINE_INGREDIENT_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun insertMedicinePriceAll(data: List<MedicinePriceModel>): Int {
		val values = data.stream().map(this::renderSqlForInsertMedicinePriceModel).collect(joining(","))
		val sqlString = "${FConstants.MODEL_MEDICINE_PRICE_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun renderSqlForInsertMedicineModel(data: MedicineModel) = data.insertString()
	private fun renderSqlForInsertMedicineSubModel(data: MedicineSubModel) = data.insertString()
	private fun renderSqlForInsertMedicineIngredientModel(data: MedicineIngredientModel) = data.insertString()
	private fun renderSqlForInsertMedicinePriceModel(data: MedicinePriceModel) = data.insertString()
	fun getUserData(id: String) = userDataRepository.selectById(id)?.lazyHide() ?: throw UserNotFoundException()
	fun getUserDataByToken(token: String) = getUserData(jwtTokenProvider.getAllClaimsFromToken(token).subject)
	fun isValid(token: String) {
		if (!jwtTokenProvider.validateToken(token)) {
			throw AuthenticationEntryPointException()
		}
	}
	fun isLive(user: UserDataModel, notLiveThrow: Boolean = true): Boolean {
		return if (user.status == UserStatus.Live) true
		else if (notLiveThrow) throw AccessDeniedException()
		else false
	}
	fun haveRole(user: UserDataModel, targetRole: UserRoles): Boolean {
		return targetRole.getFlag() and user.role != 0
	}
	fun haveRole(token: String, targetRole: UserRoles): Boolean {
		val user = UserDataModel().buildData(jwtTokenProvider.getAllClaimsFromToken(token))
		return haveRole(user, targetRole)
	}
}