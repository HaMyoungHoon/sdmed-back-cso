package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.PharmaExistException
import sdmed.back.advice.exception.PharmaNotFoundException
import sdmed.back.config.FConstants
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.medicine.MedicineIngredientModel
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import sdmed.back.model.sqlCSO.pharma.PharmaModel
import sdmed.back.repository.sqlCSO.IMedicineIngredientRepository
import java.util.*
import java.util.stream.Collectors

open class PharmaListService: PharmaService() {
	@Autowired lateinit var medicineIngredientRepository: IMedicineIngredientRepository
	fun getList(token: String): List<PharmaModel> {
		isValid(token)
		isLive(getUserDataByToken(token))

		return pharmaRepository.selectAllByInvisibleOrderByCode()
	}
	fun getData(token: String, thisPK: String, pharmaOwnMedicineView: Boolean = false) = getPharmaData(token, thisPK, pharmaOwnMedicineView)
	fun getMedicineSearch(token: String, pharmaPK: String, searchString: String, isSearchTypeCode: Boolean = true): List<MedicineModel> {
		if (searchString.isEmpty()) {
			return arrayListOf()
		}

		isValid(token)
		isLive(getUserDataByToken(token))
		val pharma = pharmaRepository.findByThisPK(pharmaPK) ?: throw PharmaNotFoundException()
		var ret = if (isSearchTypeCode) {
			medicineRepository.selectAllByCodeLikeOrKdCodeLike(searchString, searchString)
		} else {
			medicineRepository.selectAllByNameContainingOrPharmaContaining(searchString, searchString)
		}
		ret = ret.filterNot { it.clientCode == pharma.code }
		val ingredient = medicineIngredientRepository.findAllByMainIngredientCodeIn(ret.map { it.mainIngredientCode })
		medicineMerge(ret, ingredient)
		return ret
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun addPharmaData(token: String, data: PharmaModel): PharmaModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaChanger))) {
			throw AuthenticationEntryPointException()
		}
		isLive(tokenUser)

		val exist = pharmaRepository.findByCode(data.code)
		if (exist != null) {
			throw PharmaExistException()
		}

		data.thisPK = UUID.randomUUID().toString()
		val ret = pharmaRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add pharma : ${data.thisPK}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun pharmaUpload(token: String, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaChanger))) {
			throw AuthenticationEntryPointException()
		}

		val excelModel = excelFileParser.pharmaUploadExcelParse(tokenUser.id, file)
		val already: MutableList<PharmaModel> = mutableListOf()
		excelModel.chunked(500).forEach { x -> already.addAll(pharmaRepository.findAllByCodeIn(x.map { y -> y.code })) }
		var retCount = 0
		val saveList = excelModel.toMutableList()
		saveList.removeIf { x -> x.code in already.map { y -> y.code } }
		saveList.chunked(500).forEach { x -> retCount += insertAll(x) }
		if (already.isNotEmpty()) {
			val buffMap = excelModel.associateBy { it.code }
			if (already.isNotEmpty()) {
				already.forEach { x ->
					val buff = buffMap[x.code]
					if (buff != null) {
						x.safeCopy(buff)
					}
				}
			}
		}
		already.chunked(500).forEach { x -> retCount += updateAll(x) }

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add pharma count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun pharmaDataModify(token: String, pharmaData: PharmaModel): PharmaModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaChanger))) {
			throw AuthenticationEntryPointException()
		}

		val ret = pharmaRepository.save(pharmaData)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${pharmaData.orgName} modify")
		logRepository.save(logModel)
		return ret
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun modPharmaDrugList(token: String, pharmaPK: String, medicinePKList: List<String>): PharmaModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaChanger))) {
			throw AuthenticationEntryPointException()
		}

		val ret = pharmaRepository.findByThisPK(pharmaPK) ?: throw PharmaNotFoundException()
		val existMedicine = medicineRepository.findAllByThisPKIn(medicinePKList).toMutableList()
		existMedicine.onEach { x -> x.clientCode = ret.code }
		if (existMedicine.isNotEmpty()) {
			medicineRepository.saveAll(existMedicine)
		}
		
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add pharma ${ret.innerName} count : ${existMedicine.count()}")
		logRepository.save(logModel)
		return ret
	}

	private fun insertAll(data: List<PharmaModel>): Int {
		if (data.isEmpty()) {
			return 0
		}
		val values: String = data.stream().map{it.insertString()}.collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_PHARMA_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun updateAll(data: List<PharmaModel>): Int {
		if (data.isEmpty()) {
			return 0
		}

		data.forEach { x ->
			entityManager.merge(x)
		}
		entityManager.flush()
		entityManager.clear()
		return data.size
	}

	protected fun medicineMerge(mother: List<MedicineModel>, ingredient: List<MedicineIngredientModel>) {
		val ingredientMap = ingredient.associateBy { it.mainIngredientCode }
		mother.map { x ->
			ingredientMap[x.mainIngredientCode]?.let { y -> x.medicineIngredientModel = y }
		}
	}
}