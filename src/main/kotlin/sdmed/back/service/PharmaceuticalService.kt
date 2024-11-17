package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.config.FExcelFileParser
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.config.security.JwtTokenProvider
import sdmed.back.model.common.UserRole
import sdmed.back.model.common.UserRole.Companion.getFlag
import sdmed.back.model.common.UserRoles
import sdmed.back.model.common.UserStatus
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.PharmaceuticalModel
import sdmed.back.model.sqlCSO.UserDataModel
import sdmed.back.repository.sqlCSO.ILogRepository
import sdmed.back.repository.sqlCSO.IPharmaceuticalRepository
import sdmed.back.repository.sqlCSO.IUserDataRepository

@Service
class PharmaceuticalService {
	@Autowired lateinit var jwtTokenProvider: JwtTokenProvider
	@Autowired lateinit var logRepository: ILogRepository
	@Autowired lateinit var excelFileParser: FExcelFileParser
	@Autowired lateinit var userDataRepository: IUserDataRepository
	@Autowired lateinit var pharmaceuticalRepository: IPharmaceuticalRepository


	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun pharmaUpload(token: String, file: MultipartFile): List<PharmaceuticalModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PharmaFileUploader))) {
			throw AuthenticationEntryPointException()
		}

		val pharmaDataModel = excelFileParser.pharmaUploadExcelParse(tokenUser.id, file)
		val already = pharmaceuticalRepository.findAllByCodeIn(pharmaDataModel.map { it.code })
		pharmaDataModel.removeIf { x -> x.code in already.map { y -> y.code } }
		if (pharmaDataModel.isEmpty()) {
			return arrayListOf()
		}
		val ret = pharmaceuticalRepository.saveAll(pharmaDataModel)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisIndex, stackTrace[1].className, stackTrace[1].methodName, "add pharma : ${pharmaDataModel.joinToString(",") { it.innerName }}")
		logRepository.save(logModel)
		return ret
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