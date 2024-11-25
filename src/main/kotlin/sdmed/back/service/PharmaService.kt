package sdmed.back.service

import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.config.FConstants
import sdmed.back.config.FExcelFileParser
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.config.security.JwtTokenProvider
import sdmed.back.model.common.UserRole
import sdmed.back.model.common.UserRole.Companion.getFlag
import sdmed.back.model.common.UserRoles
import sdmed.back.model.common.UserStatus
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.PharmaModel
import sdmed.back.model.sqlCSO.UserDataModel
import sdmed.back.repository.sqlCSO.ILogRepository
import sdmed.back.repository.sqlCSO.IMedicineRepository
import sdmed.back.repository.sqlCSO.IPharmaRepository
import sdmed.back.repository.sqlCSO.IUserDataRepository
import java.util.stream.Collectors

@Service
class PharmaService {
	@Autowired lateinit var jwtTokenProvider: JwtTokenProvider
	@Autowired lateinit var logRepository: ILogRepository
	@Autowired lateinit var excelFileParser: FExcelFileParser
	@Autowired lateinit var userDataRepository: IUserDataRepository
	@Autowired lateinit var pharmaRepository: IPharmaRepository
	@Autowired lateinit var medicineRepository: IMedicineRepository
	@Autowired lateinit var entityManager: EntityManager


	fun getAllPharma(token: String): List<PharmaModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		isLive(tokenUser)

		return pharmaRepository.findAllByOrderByCode()
	}
	fun getPagePharma(token: String, page: Int, size: Int): Page<PharmaModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		isLive(tokenUser)

		val pageable = PageRequest.of(page, size)
		return pharmaRepository.findAllByOrderByCode(pageable)
	}

	fun getAllPharma(): List<PharmaModel> {
		return pharmaRepository.findAllByOrderByCode()
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun getPharma(token: String, pharmaPK: String): PharmaModel? {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		isLive(tokenUser)
		return pharmaRepository.findByThisPK(pharmaPK)?.apply { medicineList = mutableListOf() }
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun getPharmaWithDrug(token: String, pharmaPK: String): PharmaModel? {
		val ret = getPharma(token, pharmaPK)
		ret?.let {
			ret.medicineList.addAll(medicineRepository.findAllByPharma(it))
		}

		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun addPharmaDrugList(token: String, pharmaPK: String, medicinePKList: List<String>) {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaFileUploader))) {
			throw AuthenticationEntryPointException()
		}

		val ret = pharmaRepository.findByThisPK(pharmaPK) ?: throw NotValidOperationException()
		ret.medicineList.addAll(medicineRepository.findAllByPharma(ret))
		val buff = medicinePKList.toMutableList().distinct().toMutableList()
		buff.removeIf { x -> x in ret.medicineList.map { y -> y.thisPK } }
		if (buff.isEmpty()) {
			return
		}

		val medicineList = medicineRepository.findAllByThisPKIn(buff)
		if (medicineList.isEmpty()) {
			return
		}

		ret.medicineList.addAll(medicineList)
		ret.medicineList = ret.medicineList.distinctBy { it.thisPK }.toMutableList().onEach { it.pharma = ret }
		pharmaRepository.save(ret)
		val retCount = medicineRepository.saveAll(ret.medicineList).count()

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add pharma ${ret.innerName} count : $retCount")
		logRepository.save(logModel)
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun modPharmaDrugList(token: String, pharmaPK: String, medicinePKList: List<String>) {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaFileUploader))) {
			throw AuthenticationEntryPointException()
		}

		val ret = pharmaRepository.findByThisPK(pharmaPK) ?: throw NotValidOperationException()
		val childBuff = medicineRepository.findAllByThisPKIn(medicineRepository.findAllByPharma(ret).map { it.thisPK }).onEach { it.pharma = null }
		medicineRepository.saveAll(childBuff)
		ret.medicineList.removeIf { x -> x.thisPK in childBuff.map { y -> y.thisPK } }
		val buff = medicinePKList.toMutableList().distinct().toMutableList()
		val medicineList = medicineRepository.findAllByThisPKIn(buff).onEach { it.pharma = ret }

		ret.medicineList.addAll(medicineList)
		ret.medicineList = ret.medicineList.distinctBy { it.thisPK }.toMutableList().onEach { it.pharma = ret }
		pharmaRepository.save(ret)
		val retCount = medicineRepository.saveAll(ret.medicineList).count()

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add pharma ${ret.innerName} count : $retCount")
		logRepository.save(logModel)
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun pharmaUpload(token: String, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaFileUploader))) {
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

	private fun insertAll(data: List<PharmaModel>): Int {
		val values: String = data.stream().map(this::renderSqlForPharmaModel).collect(Collectors.joining(","))
		val ret = entityManager.createNativeQuery("${FConstants.MODEL_PHARMA_INSERT_INTO}$values").executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun renderSqlForPharmaModel(data: PharmaModel): String {
		return data.insertString()
	}
	fun getUserData(id: String) = userDataRepository.findById(id)
	fun getUserDataByToken(token: String) = userDataRepository.findById(jwtTokenProvider.getAllClaimsFromToken(token).subject)
	fun isValid(token: String) {
		if (!jwtTokenProvider.validateToken(token)) {
			throw AuthenticationEntryPointException()
		}
	}
	fun isLive(user: UserDataModel, notLiveThrow: Boolean = true): Boolean {
		return if (user.status == UserStatus.Live) true
		else if (notLiveThrow) throw NotValidOperationException()
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