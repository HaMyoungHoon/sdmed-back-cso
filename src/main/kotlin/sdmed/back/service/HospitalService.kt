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
import sdmed.back.model.sqlCSO.HospitalModel
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.UserDataModel
import sdmed.back.repository.sqlCSO.IHospitalRepository
import sdmed.back.repository.sqlCSO.ILogRepository
import sdmed.back.repository.sqlCSO.IUserDataRepository

@Service
class HospitalService {
	@Autowired lateinit var jwtTokenProvider: JwtTokenProvider
	@Autowired lateinit var logRepository: ILogRepository
	@Autowired lateinit var excelFileParser: FExcelFileParser
	@Autowired lateinit var userDataRepository: IUserDataRepository
	@Autowired lateinit var hospitalRepository: IHospitalRepository

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun hospitalUpload(token: String, file: MultipartFile): List<HospitalModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalFileUploader))) {
			throw AuthenticationEntryPointException()
		}

		val hospitalDataModel = excelFileParser.hospitalUploadExcelParse(tokenUser.id, file)
		val already = hospitalRepository.findAllByCodeIn(hospitalDataModel.map { it.code })
		hospitalDataModel.removeIf { x -> x.code in already.map { y -> y.code } }
		if (hospitalDataModel.isEmpty()) {
			return arrayListOf()
		}
		val ret = hospitalRepository.saveAll(hospitalDataModel)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisIndex, stackTrace[1].className, stackTrace[1].methodName, "add hospital : ${hospitalDataModel.joinToString(",") { it.innerName }}")
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