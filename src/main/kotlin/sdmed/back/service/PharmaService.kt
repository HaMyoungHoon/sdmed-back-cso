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
import java.util.stream.Collectors

@Service
class PharmaService {
	@Autowired lateinit var jwtTokenProvider: JwtTokenProvider
	@Autowired lateinit var logRepository: ILogRepository
	@Autowired lateinit var excelFileParser: FExcelFileParser
	@Autowired lateinit var userDataRepository: IUserDataRepository
	@Autowired lateinit var pharmaRepository: IPharmaRepository
	@Autowired lateinit var medicineRepository: IMedicineRepository
	@Autowired lateinit var pharmaMedicineRelationRepository: IPharmaMedicineRelationRepository
	@Autowired lateinit var entityManager: EntityManager


	fun getAllPharma(token: String): List<PharmaModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)

		return pharmaRepository.selectAllByInvisibleOrderByCode()
	}
	fun getPharmaAllSearch(token: String, searchString: String, isSearchTypeCode: Boolean = true): List<PharmaModel> {
		if (searchString.isEmpty()) {
			return arrayListOf()
		}

		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)
		val ret: List<PharmaModel> = if (isSearchTypeCode) {
			searchString.toIntOrNull()?.let { x ->
				pharmaRepository.selectAllByCodeContainingOrderByCode(x.toString())
			} ?: pharmaRepository.selectAllByInnerNameContainingOrOrgNameContainingOrderByCode(searchString, searchString)
		} else {
			pharmaRepository.selectAllByInnerNameContainingOrOrgNameContainingOrderByCode(searchString, searchString)
		}

		return ret
	}

	fun getPharmaData(token: String, pharmaPK: String, pharmaOwnMedicineView: Boolean = false): PharmaModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)

		val ret = pharmaRepository.findByThisPK(pharmaPK) ?: throw PharmaNotFoundException()
		if (pharmaOwnMedicineView) {
			val relation = pharmaMedicineRelationRepository.findAllByPharmaPK(pharmaPK)
			ret.medicineList = medicineRepository.findAllByThisPKIn(relation.map { it.medicinePK }).toMutableList()
		}
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
		val existMedicine = medicineRepository.findAllByThisPKIn(medicinePKList)

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
	fun deleteRelationByPharmaPK(pharmaPK: String) {
		val sqlString = "${FConstants.MODEL_PHARMA_MEDICINE_RELATIONS_DELETE_WHERE_PHARMA_PK} '${pharmaPK}'"
		entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
	}
	fun insertRelation(data: List<PharmaMedicineRelationModel>) {
		val values = data.stream().map(this::renderSqlInsertRelation).collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_PHARMA_MEDICINE_RELATIONS_INSERT_INTO}$values"
		entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
	}
	fun renderSqlInsertRelation(data: PharmaMedicineRelationModel) = data.insertString()
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
		existMedicine.removeIf { it.thisPK in already.map { it.medicinePK } }

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

	private fun insertAll(data: List<PharmaModel>): Int {
		val values: String = data.stream().map(this::renderSqlForPharmaModel).collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_PHARMA_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun renderSqlForPharmaModel(data: PharmaModel) = data.insertString()
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