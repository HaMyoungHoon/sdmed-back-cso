package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.*
import sdmed.back.config.FExtensions
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.edi.EDIPharmaDueDateModel
import sdmed.back.model.sqlCSO.pharma.PharmaModel
import sdmed.back.repository.sqlCSO.IUserRelationRepository
import java.util.*

class EDIDueDateService: EDIService() {
	@Autowired lateinit var userRelationModel: IUserRelationRepository
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
	fun getEDIDueDateMyList(token: String, date: Date, isYear: Boolean = false): List<EDIPharmaDueDateModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()
		val myPharmaPK = userRelationModel.findAllByUserPK(tokenUser.thisPK).map { it.pharmaPK }

		val ret = if (isYear) ediPharmaDueDateRepository.selectAllByThisYearDueDate(year)
		else ediPharmaDueDateRepository.selectAllByThisYearMonthDueDate(year, month)
		return ret.filter { it.pharmaPK in myPharmaPK }
	}
	fun getEDIDueDateRangeList(token: String, startDate: Date, endDate: Date): List<EDIPharmaDueDateModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)
		return ediPharmaDueDateRepository.selectAllByThisYearMonthRangeDueDate(queryDate.first, queryDate.second)
	}
	fun getEDIDueDateRangeMyList(token: String, startDate: Date, endDate: Date): List<EDIPharmaDueDateModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		val myPharmaPK = userRelationModel.findAllByUserPK(tokenUser.thisPK).map { it.pharmaPK }
		val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)
		return ediPharmaDueDateRepository.selectAllByThisYearMonthRangeDueDate(queryDate.first, queryDate.second).filter { it.pharmaPK in myPharmaPK }
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

		return ediPharmaDueDateRepository.selectAllByPharmaInThisYearMonthDueDate(year, month).filter { it.pharmaPK in pharmaPK }
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
	fun ediDueDateUpload(token: String, file: MultipartFile): List<EDIPharmaDueDateModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger))) {
			throw AuthenticationEntryPointException()
		}

		val excelModel = excelFileParser.ediDueDateUploadExcelParse(tokenUser.id, file).distinctBy { it.pharmaCode to it.year to it.month }
		val pharmaList = pharmaRepository.findAllByCodeIn(excelModel.map { it.pharmaCode })
		if (pharmaList.isEmpty()) {
			throw EDIDueDateFileUploadException("pharma is empty\ncode : ${excelModel.joinToString(",") { it.pharmaCode }}")
		}

		val excelMap = excelModel.groupBy { it.pharmaCode }
		pharmaList.forEach { x ->
			val mapBuff = excelMap[x.code]
			if (!mapBuff.isNullOrEmpty()) {
				mapBuff.forEach { y ->
					y.pharmaPK = x.thisPK
					y.orgName = x.orgName
				}
			}
		}
		val saveList = mutableListOf<EDIPharmaDueDateModel>()
		val dateMap = excelModel.filter { it.pharmaPK.isNotEmpty() }.groupBy { Pair(it.year, it.month) }
		dateMap.forEach { x ->
			val existDueDate = ediPharmaDueDateRepository.selectAllByPharmaInThisYearMonthDueDate(x.key.first, x.key.second).filter { y -> y.pharmaPK in x.value.map { z -> z.pharmaPK } }
			val buff = x.value.filterNot { y -> y.pharmaPK in existDueDate.map { it.pharmaPK } }.map { z ->
				EDIPharmaDueDateModel().apply {
					this.pharmaPK = z.pharmaPK
					this.orgName = z.orgName
					this.year = z.year
					this.month = z.month
					this.day = z.day
				}
			}
			saveList.addAll(buff)
		}

		val ret = ediPharmaDueDateRepository.saveAll(saveList)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add edi due date : ${saveList.joinToString(",") { it.pharmaPK }}")
		logRepository.save(logModel)

		return ret
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
		val existDueDate = ediPharmaDueDateRepository.selectAllByPharmaInThisYearMonthDueDate(year, month).filter { it.pharmaPK in pharmaPK }

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
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add edi due date : ${pharmaPK.joinToString(",")} $year $month $day")
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

		val buff = ediPharmaDueDateRepository.findByThisPK(thisPK) ?: throw EDIPharmaDueDateNotExistException()

		val exist = ediPharmaDueDateRepository.selectByPharmaThisYearMonthDueDate(buff.pharmaPK, year, month)
		if (exist != null && exist.thisPK != buff.thisPK) {
			throw EDIPharmaDueDateExistException()
		}

		val ret = ediPharmaDueDateRepository.save(buff.apply {
			this.year = year
			this.month = month
			this.day = day
		})
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