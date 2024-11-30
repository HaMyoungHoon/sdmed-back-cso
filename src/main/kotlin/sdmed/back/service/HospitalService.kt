package sdmed.back.service

import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AccessDeniedException
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.UserNotFoundException
import sdmed.back.config.FConstants
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
import java.util.stream.Collectors

@Service
class HospitalService {
	@Autowired lateinit var jwtTokenProvider: JwtTokenProvider
	@Autowired lateinit var logRepository: ILogRepository
	@Autowired lateinit var excelFileParser: FExcelFileParser
	@Autowired lateinit var userDataRepository: IUserDataRepository
	@Autowired lateinit var hospitalRepository: IHospitalRepository
	@Autowired lateinit var entityManager: EntityManager

	fun getAllHospital(token: String): List<HospitalModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)

		return hospitalRepository.findAllByOrderByCode()
	}
	fun getPageHospital(token: String, page: Int, size: Int): Page<HospitalModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)

		val pageable = PageRequest.of(page, size)
		return hospitalRepository.findAllByOrderByCode(pageable)
	}
	fun getHospitalAllSearch(token: String, searchString: String, isSearchTypeCode: Boolean = true): List<HospitalModel> {
		if (searchString.isEmpty()) {
			return arrayListOf()
		}

		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)
		if (isSearchTypeCode) {
			searchString.toIntOrNull()?.let { x ->
				return hospitalRepository.selectAllByCodeContainingOrderByCode(x.toString())
			} ?: return hospitalRepository.findAllByInnerNameContainingOrOrgNameContainingOrderByCode(searchString, searchString)
		}

		return hospitalRepository.findAllByInnerNameContainingOrOrgNameContainingOrderByCode(searchString, searchString)
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun hospitalUpload(token: String, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
		}

		val hospitalDataModel = excelFileParser.hospitalUploadExcelParse(tokenUser.id, file)
		var index = 0
		val already: MutableList<HospitalModel> = mutableListOf()
		while (true) {
			if (hospitalDataModel.count() > index + 500) {
				already.addAll(hospitalRepository.findAllByCodeIn(hospitalDataModel.subList(index, index + 500).map { it.code }))
			} else {
				already.addAll(hospitalRepository.findAllByCodeIn(hospitalDataModel.subList(index, hospitalDataModel.count()).map { it.code }))
				break
			}
			index += 500
		}
		hospitalDataModel.removeIf { x -> x.code in already.map { y -> y.code } }
		if (hospitalDataModel.isEmpty()) {
			return "count : 0"
		}
		index = 0
		var retCount = 0
		while (true) {
			if (hospitalDataModel.count() > index + 500) {
				retCount += insertAll(hospitalDataModel.subList(index, index + 500))
			} else {
				retCount += insertAll(hospitalDataModel.subList(index, hospitalDataModel.count()))
				break
			}
			index += 500
		}
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add hospital count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}

	private fun insertAll(data: List<HospitalModel>): Int {
		val values: String = data.stream().map(this::renderSqlForHosModel).collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_HOS_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun renderSqlForHosModel(data: HospitalModel) = data.insertString()
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