package sdmed.back.service

import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.EDIPharmaDueDateExistException
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.advice.exception.PharmaNotFoundException
import sdmed.back.config.FExtensions
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.edi.EDIPharmaDueDateModel
import sdmed.back.model.sqlCSO.pharma.PharmaModel
import java.util.*

class EDIDueDateService: EDIService() {
	fun getEDIDueDateList(token: String, date: Date, isYear: Boolean = false): List<EDIPharmaDueDateModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()
		return if (isYear) ediPharmaDueDateRepository.selectAllByThisYearDueDate(year)
		else ediPharmaDueDateRepository.selectAllByThisYearMonthDueDate(year, month)
	}
	fun getEDIDueDateRangeList(token: String, startDate: Date, endDate: Date): List<EDIPharmaDueDateModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		return if (startDate > endDate) {
			ediPharmaDueDateRepository.selectAllByThisYearMonthRangeDueDate(endDate, startDate)
		} else {
			ediPharmaDueDateRepository.selectAllByThisYearMonthRangeDueDate(startDate, endDate)
		}
	}
	fun getEDIPharmaDueDateList(token: String, pharmaPK: String, year: String): List<EDIPharmaDueDateModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		return ediPharmaDueDateRepository.selectAllByPharmaThisYearDueDate(pharmaPK, year)
	}
	fun getEDIPharmaDueDateList(token: String, pharmaPK: List<String>, date: Date): List<EDIPharmaDueDateModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()

		val pharmaPKString = pharmaPK.joinToString(",") { "'${it}'" }
		return ediPharmaDueDateRepository.selectAllByPharmaInThisYearMonthDueDate(pharmaPKString, year, month)
	}
	fun getEDIPharmaAble(token: String, date: Date): List<PharmaModel> {
		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()
		return getEDIPharmaAble(token, year, month)
	}
	fun getEDIPharmaAble(token: String, year: String, month: String): List<PharmaModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		return ediPharmaDueDateRepository.selectPharmaListByThisYearMonthDueDate(year, month)
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun postEDIPharmaDueDate(token: String, pharmaPK: String, date: Date): EDIPharmaDueDateModel {
		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()
		val day = FExtensions.parseDateTimeString(date, "dd") ?: throw NotValidOperationException()
		return postEDIPharmaDueDate(token, pharmaPK, year, month, day)
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun postEDIPharmaDueDate(token: String, pharmaPK: String, year: String, month: String, day: String): EDIPharmaDueDateModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger))) {
			throw AuthenticationEntryPointException()
		}

		val pharma = pharmaRepository.findByThisPK(pharmaPK) ?: throw PharmaNotFoundException()
		if (ediPharmaDueDateRepository.selectByPharmaThisYearMonthDueDate(pharmaPK, year, month) != null) {
			throw EDIPharmaDueDateExistException()
		}

		val ret = ediPharmaDueDateRepository.save(EDIPharmaDueDateModel().apply {
			this.pharmaPK = pharma.thisPK
			this.orgName = pharma.orgName
			this.year = year
			this.month = month
			this.day = day
		})
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add edi due date : $pharmaPK $year $month $day")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun postEDIPharmaDueDate(token: String, pharmaPK: List<String>, date: Date): List<EDIPharmaDueDateModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger))) {
			throw AuthenticationEntryPointException()
		}
		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()
		val day = FExtensions.parseDateTimeString(date, "dd") ?: throw NotValidOperationException()

		val pharma = pharmaRepository.findAllByThisPKIn(pharmaPK)
		val pharmaPKString = pharmaPK.joinToString(",") { "'${it}'" }
		val existDueDate = ediPharmaDueDateRepository.selectAllByPharmaInThisYearMonthDueDate(pharmaPKString, year, month)

		val buff = pharma.filterNot { x -> x.thisPK in existDueDate.map { it.pharmaPK } }.map { x ->
			EDIPharmaDueDateModel().apply {
				this.pharmaPK = x.thisPK
				this.orgName = x.orgName
				this.year = year
				this.month = month
				this.day = day
			}
		}

		val ret = ediPharmaDueDateRepository.saveAll(buff)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add edi due date : $pharmaPKString $year $month $day")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun putEDIPharmaDueDate(token: String, pharmaPK: String, date: Date): EDIPharmaDueDateModel {
		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()
		val day = FExtensions.parseDateTimeString(date, "dd") ?: throw NotValidOperationException()
		return putEDIPharmaDueDate(token, pharmaPK, year, month, day)
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun putEDIPharmaDueDate(token: String, thisPK: String, year: String, month: String, day: String): EDIPharmaDueDateModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger))) {
			throw AuthenticationEntryPointException()
		}

		val buff = ediPharmaDueDateRepository.findByThisPK(thisPK)?.apply {
			this.year = year
			this.month = month
			this.day = day
		} ?: throw EDIPharmaDueDateExistException()

		val ret = ediPharmaDueDateRepository.save(buff)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add edi due date : ${buff.orgName} $year $month $day")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun deleteEDIPharmaDueDate(token: String, pharmaPK: String, date: Date): Boolean {
		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()
		return deleteEDIPharmaDueDate(token, pharmaPK, year, month)
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun deleteEDIPharmaDueDate(token: String, pharmaPK: String, year: String, month: String): Boolean {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger))) {
			throw AuthenticationEntryPointException()
		}

		pharmaRepository.findByThisPK(pharmaPK) ?: throw PharmaNotFoundException()
		val dueDate = ediPharmaDueDateRepository.selectAllByPharmaThisYearMonthDueDate(pharmaPK, year, month)
		if (dueDate.isNotEmpty()) {
			val stackTrace = Thread.currentThread().stackTrace
			val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "delete edi due date : $pharmaPK $year $month")
			logRepository.save(logModel)
			ediPharmaDueDateRepository.deleteAll(dueDate)
			return true
		}

		return false
	}
}