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
import sdmed.back.config.FExtensions
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.config.security.JwtTokenProvider
import sdmed.back.model.common.UserRole
import sdmed.back.model.common.UserRole.Companion.getFlag
import sdmed.back.model.common.UserRoles
import sdmed.back.model.common.UserStatus
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.MedicineModel
import sdmed.back.model.sqlCSO.UserDataModel
import sdmed.back.repository.sqlCSO.ILogRepository
import sdmed.back.repository.sqlCSO.IMedicineRepository
import sdmed.back.repository.sqlCSO.IUserDataRepository
import java.util.*
import java.util.stream.Collectors.joining

@Service
class MedicineService {
	@Autowired lateinit var jwtTokenProvider: JwtTokenProvider
	@Autowired lateinit var logRepository: ILogRepository
	@Autowired lateinit var excelFileParser: FExcelFileParser
	@Autowired lateinit var userDataRepository: IUserDataRepository
	@Autowired lateinit var medicineRepository: IMedicineRepository
	@Autowired lateinit var entityManager: EntityManager

	fun getMedicine(token: String): List<MedicineModel> {
		isValid(token)
		return medicineRepository.selectAllByRecentData()
	}
	fun getMedicine(token: String, page: Int, size: Int): Page<MedicineModel> {
		isValid(token)
		return medicineRepository.findAllByOrderByName(PageRequest.of(page, size))
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun medicineUpload(token: String, applyDate: Date, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.MedicineFileUploader))) {
			throw AuthenticationEntryPointException()
		}

		val medicineModel = excelFileParser.medicineUploadExcelParse(tokenUser.id, applyDate, file)
		var index = 0
		val already = medicineRepository.selectAllByApplyDate(FExtensions.parseDateTimeString(applyDate, "yyyy-MM-dd") ?: "")
//		medicineModel.removeAll { x -> already.map { y -> y.mainIngredientCode }.toSet().contains(x.mainIngredientCode) }
		medicineModel.removeIf { x -> x.mainIngredientCode in already.map { y -> y.mainIngredientCode } }
		medicineModel.parallelStream()
		if (medicineModel.isEmpty()) {
			return "count : 0"
		}
		index = 0
		var retCount = 0
		while (true) {
			if (medicineModel.count() > index + 500) {
				retCount += insertAll(medicineModel.subList(index, index + 500))
			} else {
				retCount += insertAll(medicineModel.subList(index, medicineModel.count()))
				break
			}
			index += 500
		}
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add medicine count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}

	private fun insertAll(data: List<MedicineModel>): Int {
		val values: String = data.stream().map(this::renderSqlForMedicineModel).collect(joining(","))
		val ret = entityManager.createNativeQuery("${FConstants.MODEL_DRUG_INSERT_INTO}$values").executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun renderSqlForMedicineModel(data: MedicineModel): String {
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