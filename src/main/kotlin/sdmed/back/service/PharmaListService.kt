package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.PharmaExistException
import sdmed.back.advice.exception.PharmaNotFoundException
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.medicine.MedicineIngredientModel
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import sdmed.back.model.sqlCSO.medicine.MedicineSubModel
import sdmed.back.model.sqlCSO.pharma.PharmaMedicineRelationModel
import sdmed.back.model.sqlCSO.pharma.PharmaModel
import sdmed.back.repository.sqlCSO.IMedicineIngredientRepository
import sdmed.back.repository.sqlCSO.IMedicineSubRepository
import java.util.*
import java.util.stream.Collectors

class PharmaListService: PharmaService() {
	@Autowired lateinit var medicineSubRepository: IMedicineSubRepository
	@Autowired lateinit var medicineIngredientRepository: IMedicineIngredientRepository
	fun getList(token: String): List<PharmaModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)

		return pharmaRepository.selectAllByInvisibleOrderByCode()
	}
	fun getData(token: String, thisPK: String, pharmaOwnMedicineView: Boolean = false) = getPharmaData(token, thisPK, pharmaOwnMedicineView)
	fun getMedicineSearch(token: String, searchString: String, isSearchTypeCode: Boolean = true): List<MedicineModel> {
		if (searchString.isEmpty()) {
			return arrayListOf()
		}

		isValid(token)
		var ret = if (isSearchTypeCode) {
			searchString.toIntOrNull()?.let { x ->
				medicineRepository.selectAllByCodeLikeOrKdCodeLike(x, x)
			} ?: medicineRepository.selectAllByNameContainingOrPharmaContaining(searchString, searchString)
		} else {
			medicineRepository.selectAllByNameContainingOrPharmaContaining(searchString, searchString)
		}
		val exist = pharmaMedicineRelationRepository.findAllByMedicinePKIn(ret.map { it.thisPK })
		ret = ret.filterNot { it.thisPK in exist.map { it.medicinePK } }
		val sub = medicineSubRepository.findALlByCodeInOrderByCode(ret.map { it.code })
		val ingredient = medicineIngredientRepository.findAllByMainIngredientCodeIn(ret.map { it.mainIngredientCode })
		medicineMerge(ret, sub, ingredient)
		return ret
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun addPharmaData(token: String, data: PharmaModel): PharmaModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaChanger))) {
			throw AuthenticationEntryPointException()
		}

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
	fun pharmaUpload(token: String, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaChanger))) {
			throw AuthenticationEntryPointException()
		}

		val pharmaDataModel = excelFileParser.pharmaUploadExcelParse(tokenUser.id, file)
		var index = 0
		val already: MutableList<PharmaModel> = mutableListOf()
		while (true) {
			if (pharmaDataModel.count() > index + 500) {
				already.addAll(pharmaRepository.findAllByCodeIn(pharmaDataModel.subList(index, index + 500).map { it.code }))
			} else {
				already.addAll(pharmaRepository.findAllByCodeIn(pharmaDataModel.subList(index, pharmaDataModel.count()).map { it.code }))
				break
			}
			index += 500
		}
		pharmaDataModel.removeIf { x -> x.code in already.map { y -> y.code } }
		if (pharmaDataModel.isEmpty()) {
			return "count : 0"
		}
		index = 0
		var retCount = 0
		while (true) {
			if (pharmaDataModel.count() > index + 500) {
				retCount += insertAll(pharmaDataModel.subList(index, index + 500))
			} else {
				retCount += insertAll(pharmaDataModel.subList(index, pharmaDataModel.count()))
				break
			}
			index += 500
		}
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add pharma count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun pharmaMedicineUpload(token: String, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaChanger))) {
			throw AuthenticationEntryPointException()
		}

		var pharmaMedicineParseModel = excelFileParser.pharmaMedicineUploadExcelParse(tokenUser.id, file)
		val existPharma: MutableList<PharmaModel> = mutableListOf()
		pharmaMedicineParseModel.chunked(500).forEach { x -> existPharma.addAll(pharmaRepository.findAllByCodeIn(x.map { y -> y.pharmaCode })) }
		pharmaMedicineParseModel = pharmaMedicineParseModel.filter { x -> x.pharmaCode in existPharma.map { y -> y.code } }.toMutableList()
		if (pharmaMedicineParseModel.isEmpty()) {
			return "count : 0"
		}

		val existMedicine: MutableList<MedicineModel> = mutableListOf()
		pharmaMedicineParseModel.chunked(500).forEach { x -> existMedicine.addAll(medicineRepository.findAllByCodeIn(x.flatMap { y -> y.medicineCodeList })) }
		pharmaMedicineParseModel = pharmaMedicineParseModel.map { x ->
			val filteredMedicineCodeList = x.medicineCodeList.filter { y -> y in existMedicine.map { z -> z.code }.toSet() }
			x.copy(medicineCodeList = filteredMedicineCodeList.toMutableList())
		}.filter { x -> x.medicineCodeList.isNotEmpty() }.toMutableList()
		if (pharmaMedicineParseModel.isEmpty()) {
			return "count : 0"
		}

		val already: MutableList<PharmaMedicineRelationModel> = mutableListOf()
		existPharma.chunked(500).forEach { x -> already.addAll(pharmaMedicineRelationRepository.findAllByPharmaPKIn(x.map { y -> y.thisPK })) }
		existMedicine.removeIf { x -> x.thisPK in already.map { y -> y.medicinePK } }
		val exist = pharmaMedicineRelationRepository.findAllByMedicinePKIn(existMedicine.map { it.thisPK })
		existMedicine.removeIf { x -> x.thisPK in exist.map { y -> y.medicinePK } }

		val pharmaMap = existPharma.associateBy({it.code}, {it.thisPK})
		val medicineMap = existMedicine.associateBy({it.code}, {it.thisPK})
		val pharmaMedicineRelationModel: MutableList<PharmaMedicineRelationModel> = mutableListOf()
		pharmaMedicineParseModel.flatMap { x ->
			val pharmaPK = pharmaMap[x.pharmaCode] ?: ""
			x.medicineCodeList.map { y ->
				val medicinePK = medicineMap[y]
				if (pharmaPK.isNotEmpty() && medicinePK != null) {
					pharmaMedicineRelationModel.add(PharmaMedicineRelationModel().apply {
						this.pharmaPK = pharmaPK
						this.medicinePK = medicinePK
					})
				}
			}
		}

		pharmaMedicineRelationModel.chunked(500).forEach { insertRelation(it) }

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add pharma count : ${pharmaMedicineRelationModel.count()}")
		logRepository.save(logModel)
		return "count : ${pharmaMedicineRelationModel.count()}"
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun pharmaDataModify(token: String, pharmaData: PharmaModel): PharmaModel {
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
	fun modPharmaDrugList(token: String, pharmaPK: String, medicinePKList: List<String>): PharmaModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaChanger))) {
			throw AuthenticationEntryPointException()
		}

		val ret = pharmaRepository.findByThisPK(pharmaPK) ?: throw PharmaNotFoundException()
		val existMedicine = medicineRepository.findAllByThisPKIn(medicinePKList).toMutableList()
		val exist = pharmaMedicineRelationRepository.findAllByPharmaPKNotAndMedicinePKIn(pharmaPK, medicinePKList)
		existMedicine.removeIf { x -> x.thisPK in exist.map { y -> y.medicinePK } }

		deleteRelationByPharmaPK(ret.thisPK)
		if (existMedicine.isEmpty()) {
			return ret
		}
		insertRelation(existMedicine.map { x -> PharmaMedicineRelationModel().apply {
			this.pharmaPK = pharmaPK
			this.medicinePK = x.thisPK
		}})

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add pharma ${ret.innerName} count : ${existMedicine.count()}")
		logRepository.save(logModel)
		return ret
	}

	private fun deleteRelationByPharmaPK(pharmaPK: String) {
		val sqlString = "${FConstants.MODEL_PHARMA_MEDICINE_RELATIONS_DELETE_WHERE_PHARMA_PK} '${pharmaPK}'"
		entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
	}
	private fun insertRelation(data: List<PharmaMedicineRelationModel>) {
		val values = data.stream().map{it.insertString()}.collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_PHARMA_MEDICINE_RELATIONS_INSERT_INTO}$values"
		entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
	}

	private fun insertAll(data: List<PharmaModel>): Int {
		val values: String = data.stream().map{it.insertString()}.collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_PHARMA_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
}