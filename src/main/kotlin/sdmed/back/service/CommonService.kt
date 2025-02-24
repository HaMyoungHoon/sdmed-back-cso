package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import sdmed.back.advice.exception.*
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.config.FServiceBase
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.IPLogModel
import sdmed.back.model.sqlCSO.LogViewModel
import sdmed.back.model.sqlCSO.common.CheckAuthNumberModel
import sdmed.back.model.sqlCSO.common.QuarantineAuthNumberModel
import sdmed.back.model.sqlCSO.common.VersionCheckModel
import sdmed.back.model.sqlCSO.common.VersionCheckType
import sdmed.back.repository.sqlCSO.ICheckAuthNumberRepository
import sdmed.back.repository.sqlCSO.IIPLogRepository
import sdmed.back.repository.sqlCSO.IQuarantineAuthNumberRepository
import sdmed.back.repository.sqlCSO.IVersionCheckRepository
import java.sql.Timestamp
import java.util.Date

class CommonService: FServiceBase() {
	@Autowired lateinit var ipLogRepository: IIPLogRepository
	@Autowired lateinit var versionCheckRepository: IVersionCheckRepository
	@Autowired lateinit var checkAuthNumberRepository: ICheckAuthNumberRepository
	@Autowired lateinit var quarantineAuthNumberRepository: IQuarantineAuthNumberRepository

	fun getAbleVersion(versionCheckType: VersionCheckType): List<VersionCheckModel> {
		val ret = versionCheckRepository.findByVersionCheckTypeAndAbleOrderByRegDateDesc(versionCheckType)
		if (ret.isEmpty()) {
			throw VersionCheckNotExistException()
		}
		return ret
	}

	fun getFindIDAuthNumber(name: String, phoneNumber: String) {
		if (FExtensions.isPhoneNumber(phoneNumber) != true) {
			throw PhoneNumberFormatException()
		}
		val ret = userDataRepository.findByName(name)
		if (ret.isEmpty()) {
			throw UserNotFoundException()
		}


		val numberBuff = FExtensions.regexOnlyNumber(phoneNumber)
		val findBuff = ret.map { Triple(it.thisPK, it.id, FExtensions.regexOnlyNumber(it.phoneNumber)) }.find { it.third == numberBuff } ?: throw UserNotFoundException()
		if (quarantineAuthNumberRepository.findByUserPK(findBuff.first) != null) {
			throw UserQuarantineException()
		}

		// 지금부터 10분 전까지 토탈 10개 초과 요청 했으면 격리 시켜버림
		val checkTimeNow = Date()
		val checkTimeBefore = Date(checkTimeNow.time - 10 * 60 * 60)
		val countOfAuthRequest = checkAuthNumberRepository.selectCheckAuthNumberCount(phoneNumber, checkTimeBefore, checkTimeNow)
		if (countOfAuthRequest.count() > 10) {
			quarantineAuthNumberRepository.save(QuarantineAuthNumberModel().apply {
				this.userPK = findBuff.first
			})
			throw UserQuarantineException()
		}

		// sms 전송 api
		val authNumber = FExtensions.getRandomNumberString(6)

		checkAuthNumberRepository.save(CheckAuthNumberModel().apply {
			this.phoneNumber = numberBuff
			this.authNumber = authNumber
			this.content = findBuff.first
		})
	}
	fun getFindPWAuthNumber(id: String, phoneNumber: String) {
		if (FExtensions.isPhoneNumber(phoneNumber) != true) {
			throw PhoneNumberFormatException()
		}

		val ret = userDataRepository.selectById(id) ?: throw UserNotFoundException()

		val numberBuff = FExtensions.regexOnlyNumber(phoneNumber)
		if (quarantineAuthNumberRepository.findByUserPK(ret.thisPK) != null) {
			throw UserQuarantineException()
		}

		// 지금부터 10분 전까지 토탈 10개 초과 요청 했으면 격리 시켜버림
		val checkTimeNow = Date()
		val checkTimeBefore = Date(checkTimeNow.time - 10 * 60 * 60)
		val countOfAuthRequest = checkAuthNumberRepository.selectCheckAuthNumberCount(phoneNumber, checkTimeBefore, checkTimeNow)
		if (countOfAuthRequest.count() > 10) {
			quarantineAuthNumberRepository.save(QuarantineAuthNumberModel().apply {
				this.userPK = ret.thisPK
			})
			throw UserQuarantineException()
		}

		// sms 전송 api
		val authNumber = FExtensions.getRandomNumberString(6)

		checkAuthNumberRepository.save(CheckAuthNumberModel().apply {
			this.phoneNumber = numberBuff
			this.authNumber = authNumber
			this.content = jwtTokenProvider.createToken(ret)
		})
	}
	fun getCheckAuthNumber(authNumber: String, phoneNumber: String): String {
		val numberBuff = FExtensions.regexOnlyNumber(phoneNumber)
		val checkTimeNow = Date()
		val checkTimeBefore = Date(checkTimeNow.time - 3 * 60 * 60)
		val findBuff = checkAuthNumberRepository.selectCheckAuthNumber(authNumber, numberBuff, checkTimeBefore, checkTimeNow)
		if (findBuff.isEmpty()) {
			throw Exception()
		}

		return findBuff.first().content
	}
	fun getLogViewModel(token: String, page: Int = 0, size: Int = 1000): Page<LogViewModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}
		val pageable = PageRequest.of(page, size)
		return logRepository.selectLogViewModel(pageable)
	}
	fun getIPLogModel(token: String, page: Int = 0, size: Int = 1000): Page<IPLogModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}
		val pageable = PageRequest.of(page, size)
		return ipLogRepository.selectIPLogModel(pageable)
	}
	fun addIPLog(ipLogModel: IPLogModel): IPLogModel {
		if (ipLogModel.ipContains(FConstants.IP_OFFICE_1) || ipLogModel.ipContains(FConstants.IP_OFFICE_2)) {
			return ipLogModel
		}
		if (ipLogModel.requestUri.startsWith(FConstants.REQUEST_MQTT) || ipLogModel.requestUri.startsWith(FConstants.REQUEST_COMMON) ||
			ipLogModel.requestUri.startsWith(FConstants.REQUEST_INTRA) || ipLogModel.requestUri.startsWith(FConstants.REQUEST_EXTRA)) {
			return ipLogModel
		}

		val minusTime = 1 * 30 * 1000
		val model = ipLogRepository.findByLocalAddrAndRequestUriAndDateTimeGreaterThan(ipLogModel.localAddr, ipLogModel.requestUri, Timestamp(ipLogModel.dateTime.time - minusTime))
		if (model != null) {
			return model
		}
		return ipLogRepository.save(ipLogModel)
	}
}