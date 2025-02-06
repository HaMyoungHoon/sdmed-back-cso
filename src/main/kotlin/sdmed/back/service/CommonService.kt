package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sdmed.back.advice.exception.PhoneNumberFormatException
import sdmed.back.advice.exception.UserNotFoundException
import sdmed.back.advice.exception.UserQuarantineException
import sdmed.back.advice.exception.VersionCheckNotExistException
import sdmed.back.config.FAmhohwa
import sdmed.back.config.FExtensions
import sdmed.back.config.FServiceBase
import sdmed.back.model.sqlCSO.common.CheckAuthNumberModel
import sdmed.back.model.sqlCSO.common.QuarantineAuthNumberModel
import sdmed.back.model.sqlCSO.common.VersionCheckModel
import sdmed.back.model.sqlCSO.common.VersionCheckType
import sdmed.back.repository.sqlCSO.ICheckAuthNumberRepository
import sdmed.back.repository.sqlCSO.IQuarantineAuthNumberRepository
import sdmed.back.repository.sqlCSO.IVersionCheckRepository
import java.util.Date

class CommonService: FServiceBase() {
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
}