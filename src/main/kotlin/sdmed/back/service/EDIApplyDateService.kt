package sdmed.back.service

import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.EDIApplyDateExistException
import sdmed.back.advice.exception.EDIApplyDateNotExistException
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.config.FExtensions
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.edi.EDIApplyDateModel
import sdmed.back.model.sqlCSO.edi.EDIApplyDateState
import java.util.*

open class EDIApplyDateService: EDIService() {
	fun getAllApplyDate(token: String): List<EDIApplyDateModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger))) {
			return ediApplyDateRepository.selectAllByUse()
		}

		return ediApplyDateRepository.findAllByOrderByYearDescMonthDesc()
	}
	fun getUseApplyDate(): List<EDIApplyDateModel> {
		return ediApplyDateRepository.selectAllByUse()
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun postApplyDate(token: String, applyDate: Date): EDIApplyDateModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger))) {
			throw AuthenticationEntryPointException()
		}

		val year = FExtensions.parseDateTimeString(applyDate, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(applyDate, "MM") ?: throw NotValidOperationException()
		if (ediApplyDateRepository.selectByApplyDate(year, month) != null) {
			throw EDIApplyDateExistException()
		}

		val ret = ediApplyDateRepository.save(EDIApplyDateModel().apply {
			this.userPK = tokenUser.thisPK
			this.year = year
			this.month = month
		})
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add apply date : ${applyDate}")
		logRepository.save(logModel)

		return ret
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun putApplyDateModify(token: String, thisPK: String, state: EDIApplyDateState): EDIApplyDateModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger))) {
			throw AuthenticationEntryPointException()
		}

		val data = ediApplyDateRepository.findByThisPK(thisPK) ?: throw EDIApplyDateNotExistException()
		data.applyDateState = state
		val ret = ediApplyDateRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "modify apply date state : $state")
		logRepository.save(logModel)
		return ret
	}
}