package sdmed.back.service

import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.*
import sdmed.back.config.FExtensions
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.edi.*
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import java.util.*

class EDIListService: EDIService() {
	fun getEDIUploadList(token: String, startDate: Date, endDate: Date): List<EDIUploadListModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			return ediUploadRepository.selectAllByMe(tokenUser.thisPK, queryDate.first, queryDate.second)
		}

		return ediUploadRepository.selectAllByDate(queryDate.first, queryDate.second)
	}
	fun getEDIUploadListMyChild(token: String, startDate: Date, endDate: Date): List<EDIUploadListModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			return ediUploadRepository.selectAllByMe(tokenUser.thisPK, queryDate.first, queryDate.second)
		}

		val myChildPK = userChildPKRepository.selectAllByMotherPK(tokenUser.thisPK)
		if (myChildPK.isEmpty()) {
			return arrayListOf()
		}

		return ediUploadRepository.selectAllByMyChildAndDate(myChildPK.joinToString(",") { "'${it}'" }, queryDate.first, queryDate.second)
	}
	fun getEDIUploadData(token: String, thisPK: String): EDIUploadModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		val data = if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			ediUploadRepository.findByUserPKAndThisPK(tokenUser.thisPK, thisPK) ?: throw EDIUploadNotExistException()
		} else {
			ediUploadRepository.findByThisPK(thisPK) ?: throw EDIUploadNotExistException()
		}
		return parseEDIUploadModel(data)
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun postEDIResponseData(token: String, thisPK: String, responseData: EDIUploadResponseModel): EDIUploadModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val data = ediUploadRepository.findByThisPK(thisPK) ?: throw EDIUploadNotExistException()
		data.ediState = responseData.ediState
		val ret = ediUploadRepository.save(data)
		ediUploadResponseRepository.save(EDIUploadResponseModel().apply {
			this.ediPK = thisPK
			this.userPK = tokenUser.thisPK
			this.etc = responseData.etc
			this.ediState = responseData.ediState
		})
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add response : ${responseData.thisPK}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun postEDIData(token: String, uploadModel: EDIUploadModel): EDIUploadModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		if (!canIUseApplyDate(token, uploadModel.year, uploadModel.month)) {
			throw NotValidOperationException("${uploadModel.year}-${uploadModel.month}")
		}

		val serverTime = Date()
		val serverTimeYear = FExtensions.parseDateTimeString(serverTime, "yyyy") ?: throw NotValidOperationException()
		val serverTimeMonth = FExtensions.parseDateTimeString(serverTime, "MM") ?: throw NotValidOperationException()
		val serverTimeDay = FExtensions.parseDateTimeString(serverTime, "dd") ?: throw NotValidOperationException()
		uploadModel.thisPK = UUID.randomUUID().toString()
		uploadModel.day = serverTimeDay
		val hospital = hospitalRepository.findByThisPK(uploadModel.hospitalPK) ?: throw HospitalNotFoundException()
		val existPharmaList = pharmaRepository.findAllByThisPKIn(uploadModel.pharmaList.map { it.pharmaPK })

		val realPharma = realPharmaCheck(uploadModel.thisPK, uploadModel.pharmaList, existPharmaList)
		val existMedicineList = medicineRepository.findAllByThisPKIn(realPharma.flatMap { x -> x.medicineList.map { y -> y.medicinePK } })

		val kdCodeString = existMedicineList.map { it.kdCode }.joinToString { "'${it}'" }
		val yearMonthDay = "${uploadModel.year}-${uploadModel.month}-${uploadModel.day}"
		val medicinePriceList = medicinePriceRepository.selectAllByRecentDataKDCodeInAndYearMonth(kdCodeString, yearMonthDay)
		val existMedicineNewData = mutableListOf<MedicineModel>()
		mergeMedicinePrice(existMedicineList, medicinePriceList, existMedicineNewData)

		val realMedicineList = realMedicineCheck(uploadModel.thisPK, realPharma.flatMap { it.medicineList }, existMedicineNewData)
		realPharma.onEach {
			it.thisPK = UUID.randomUUID().toString()
			it.ediPK = uploadModel.thisPK
			it.year = uploadModel.year
			it.month = serverTimeMonth
			it.day = uploadModel.day
			it.medicineList.clear()
		}
		realMedicineList.onEach {
			it.thisPK = UUID.randomUUID().toString()
			it.ediPK = uploadModel.thisPK
			it.year = uploadModel.year
			it.month = uploadModel.month
			it.day = uploadModel.day
		}
		val realPharmaNewData: MutableList<EDIUploadPharmaModel> = mutableListOf()
		mergeEDIPharmaMedicine(realPharma, realMedicineList, realPharmaNewData)
		// 여기까진 올린 데이터가 진짜 있나 없나만 보는 거임
		// 밑은 올린 데이터가 마감 기한을 넘겼으면 이월 시키는 거임
		// 사진 데이터는 2024-11-11 자료라고 해도, 처리하는 지금은 2025-01-10 이면 마감일자를 1월 기준으로 봄
		// 그리고 pharma 의 year, month, day 는 처리 된 날짜를 말하는 거임
		// medicine 의 year, month, day 는 약가 기준일을 말함.
		val pharmaPKString = realPharmaNewData.map { it.pharmaPK }.joinToString { "'${it}'" }
		val dueDateList = ediPharmaDueDateRepository.selectAllByPharmaInThisYearMonthDueDate(pharmaPKString, serverTimeYear, serverTimeMonth)
		uploadModel.orgName = hospital.orgName
		uploadModel.pharmaList = carriedOverPharma(realPharmaNewData, dueDateList).toMutableList()
		uploadModel.ediState = EDIState.None
		uploadModel.regDate = serverTime
		uploadModel.fileList.onEach {
			it.thisPK = UUID.randomUUID().toString()
			it.ediPK = uploadModel.thisPK
		}
		val ret = ediUploadRepository.save(uploadModel)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add edi upload : ${uploadModel.thisPK} ${uploadModel.year}-${uploadModel.month}-${uploadModel.day}")
		logRepository.save(logModel)
		return ret
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun putEDIUpload(token: String, thisPK: String, ediUploadModel: EDIUploadModel): EDIUploadModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val data = parseEDIUploadModel(ediUploadRepository.findByThisPK(thisPK) ?: throw EDIUploadNotExistException())
		data.safeCopy(ediUploadModel)
		ediUploadPharmaMedicineRepository.saveAll(data.pharmaList.flatMap { it.medicineList })
		ediUploadPharmaRepository.saveAll(data.pharmaList)
		return ediUploadRepository.save(data)
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun putEDIPharma(token: String, thisPK: String, pharma: EDIUploadPharmaModel): EDIUploadPharmaModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val data = ediUploadPharmaRepository.findByThisPK(thisPK) ?: throw EDIUploadPharmaNotExistException()
		data.safeCopy(pharma)
		ediUploadPharmaMedicineRepository.saveAll(data.medicineList)
		val ret = ediUploadPharmaRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "modify edi pharma : ${data.thisPK} ${data.orgName}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun putEDIMedicine(token: String, thisPK: String, medicine: EDIUploadPharmaMedicineModel): EDIUploadPharmaMedicineModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val data = ediUploadPharmaMedicineRepository.findByThisPK(thisPK) ?: throw EDIUploadPharmaMedicineNotExistException()
		data.safeCopy(medicine)
		val ret = ediUploadPharmaMedicineRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "modify edi pharma medicine : ${data.thisPK} ${data.name}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun deleteEDIMedicine(token: String, thisPK: String): EDIUploadPharmaMedicineModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val data = ediUploadPharmaMedicineRepository.findByThisPK(thisPK) ?: throw EDIUploadPharmaMedicineNotExistException()
		val edi = ediUploadRepository.findByThisPK(data.ediPK) ?: throw EDIUploadNotExistException()
		if (edi.ediState != EDIState.None) {
			throw NotValidOperationException()
		}

		ediUploadPharmaMedicineRepository.delete(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "del edi pharma medicine : ${data.thisPK} ${data.name}")
		logRepository.save(logModel)
		return data
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun deleteEDIMedicine(token: String, thisPK: List<String>): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val data = ediUploadPharmaMedicineRepository.findByThisPKIn(thisPK)
		val edis = ediUploadRepository.findByThisPKIn(data.map { it.ediPK }).filter { it.ediState == EDIState.None }
		// 근데 edi 가 여러 개일 수가 있나?
		val deletableData = data.filter { x -> x.ediPK in edis.map { it.thisPK } }
		if (deletableData.isEmpty()) {
			return "count 0"
		}
		ediUploadPharmaMedicineRepository.deleteAll(deletableData)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "del edi pharma medicine : ${deletableData.count()}")
		logRepository.save(logModel)
		return "count: ${deletableData.count()}"
	}

	private fun parseEDIUploadModel(data: EDIUploadModel) = data.apply {
		this.responseList = ediUploadResponseRepository.findAllByEdiPKOrderByRegDate(thisPK).toMutableList()
		this.fileList = ediUploadFileRepository.findAllByEdiPKOrderByThisPK(thisPK).toMutableList()
		val pharmaList = ediUploadPharmaRepository.findALlByEdiPKOrderByPharmaPK(thisPK)
		val medicineList = ediUploadPharmaMedicineRepository.findAllByEdiPKAndPharmaPKInOrderByMedicinePK(thisPK, pharmaList.map { it.pharmaPK })
		val newData: MutableList<EDIUploadPharmaModel> = mutableListOf()
		mergeEDIPharmaMedicine(pharmaList, medicineList, newData)
		this.pharmaList = newData
	}
}