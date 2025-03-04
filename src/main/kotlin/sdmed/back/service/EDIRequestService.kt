package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.*
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.RequestType
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.edi.*
import sdmed.back.model.sqlCSO.hospital.HospitalModel
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import sdmed.back.model.sqlCSO.request.RequestModel
import sdmed.back.repository.sqlCSO.IUserRelationRepository
import java.util.*

class EDIRequestService: EDIService() {
	@Autowired lateinit var userRelationRepository: IUserRelationRepository
	fun getApplyDateList(token: String): List<EDIApplyDateModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		return ediApplyDateRepository.selectAllByUse()
	}
	fun getHospitalList(token: String, applyDate: Date, withChild: Boolean = true): List<EDIHosBuffModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		val year = FExtensions.parseDateTimeString(applyDate, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(applyDate, "MM") ?: throw NotValidOperationException()
		val ret = userRelationRepository.selectAllMyHospital(tokenUser.thisPK).distinctBy { it.thisPK }
		val pharma = userRelationRepository.selectAllMyPharmaAbleIn(tokenUser.thisPK, ret.map { it.thisPK }, year, month).distinctBy { Pair(it.thisPK, it.hosPK) }
		if (withChild) {
			val medicine = userRelationRepository.selectAllMyMedicineIn(tokenUser.thisPK, ret.map { it.thisPK }).distinctBy { Triple(it.thisPK, it.pharmaPK, it.hosPK) }.filter { it.pharmaPK in pharma.map { it.thisPK } }
			mergePharmaMedicine(pharma, medicine)
			mergeHosPharma(ret, pharma.toMutableList())
			return ret.toMutableList().apply {
				removeIf { it.pharmaList.isEmpty() }
			}
		}

		return ret
	}
	fun getPharmaList(token: String, applyDate: Date, withMedicine: Boolean = false): List<EDIPharmaBuffModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		val ret = userRelationRepository.selectAllByInvisible().distinctBy { it.thisPK }
		if (withMedicine) {
			val pharmaPK = ret.map { it.thisPK }
//			val medicine = userRelationRepository.selectAllMyMedicine(tokenUser.thisPK, hosPK).distinctBy { it.thisPK }.filter { it.pharmaPK in pharmaPK }
//			mergePharmaMedicine(ret, medicine)
		}

		return ret
	}
	fun getPharmaList(token: String, hosPK: String, applyDate: Date, withMedicine: Boolean = true): List<EDIPharmaBuffModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		val year = FExtensions.parseDateTimeString(applyDate, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(applyDate, "MM") ?: throw NotValidOperationException()
		val ret = userRelationRepository.selectAllMyPharmaAble(tokenUser.thisPK, hosPK, year, month).distinctBy { it.thisPK }
		if (withMedicine) {
			val pharmaPK = ret.map { it.thisPK }
			val medicine = userRelationRepository.selectAllMyMedicine(tokenUser.thisPK, hosPK).distinctBy { it.thisPK }.filter { it.pharmaPK in pharmaPK }
			mergePharmaMedicine(ret, medicine)
		}

		return ret
	}
	fun getMedicineList(token: String, hosPK: String, pharmaPK: List<String>): List<EDIMedicineBuffModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		if (pharmaPK.isEmpty()) {
			return arrayListOf()
		}

		return userRelationRepository.selectAllMyMedicine(tokenUser.thisPK, hosPK).distinctBy { it.thisPK }.filter { it.pharmaPK in pharmaPK }
	}
	fun getEDIUploadMyList(token: String, startDate: Date, endDate: Date): List<EDIUploadModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)
		return ediUploadRepository.selectAllByMe(tokenUser.thisPK, queryDate.first, queryDate.second)
	}
	fun getEDIUploadMyData(token: String, thisPK: String): EDIUploadModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		val data = ediUploadRepository.findByUserPKAndThisPK(tokenUser.thisPK, thisPK) ?: throw EDIUploadNotExistException()
		return parseEDIUploadModel(data)
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun postEDIData(token: String, ediUploadModel: EDIUploadModel): EDIUploadModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		if (!canIUseApplyDate(token, ediUploadModel.year, ediUploadModel.month)) {
			throw NotValidOperationException("${ediUploadModel.year}-${ediUploadModel.month}")
		}

		val serverTime = Date()
		val serverTimeYear = FExtensions.parseDateTimeString(serverTime, "yyyy") ?: throw NotValidOperationException()
		val serverTimeMonth = FExtensions.parseDateTimeString(serverTime, "MM") ?: throw NotValidOperationException()
		val serverTimeDay = FExtensions.parseDateTimeString(serverTime, "dd") ?: throw NotValidOperationException()
		ediUploadModel.thisPK = UUID.randomUUID().toString()
		ediUploadModel.day = serverTimeDay
		ediUploadModel.regDate = serverTime
		ediUploadModel.userPK = tokenUser.thisPK
		ediUploadModel.id = tokenUser.id
		ediUploadModel.name = tokenUser.name
		val hospital = hospitalRepository.findByThisPK(ediUploadModel.hospitalPK) ?: throw HospitalNotFoundException()
		if (hospital.inVisible) {
			throw HospitalNotFoundException()
		}

		val existPharmaList = pharmaRepository.findAllByThisPKIn(ediUploadModel.pharmaList.map { it.pharmaPK }).filter { !it.inVisible }

		val realPharma = realPharmaCheck(ediUploadModel.thisPK, ediUploadModel.pharmaList, existPharmaList)
		val existMedicineList = medicineRepository.findAllByThisPKIn(realPharma.flatMap { x -> x.medicineList.map { y -> y.medicinePK } }).filter { !it.inVisible }

		val kdCodeString = existMedicineList.map { it.kdCode }.distinct()
		val yearMonthDay = "${ediUploadModel.year}-${ediUploadModel.month}-${ediUploadModel.day}"
		val medicinePriceList = medicinePriceRepository.selectAllByRecentDataKDCodeInAndYearMonth(kdCodeString, yearMonthDay)
		val existMedicineNewData = mutableListOf<MedicineModel>()
		mergeMedicinePrice(existMedicineList, medicinePriceList, existMedicineNewData)

		val realMedicineList = realMedicineCheck(ediUploadModel.thisPK, realPharma.flatMap { it.medicineList }, existMedicineNewData)
		realPharma.onEach {
			it.thisPK = UUID.randomUUID().toString()
			it.ediPK = ediUploadModel.thisPK
			it.year = serverTimeYear
			it.month = serverTimeMonth
			it.day = ediUploadModel.day
			it.medicineList.clear()
		}
		realMedicineList.onEach {
			it.thisPK = UUID.randomUUID().toString()
			it.ediPK = ediUploadModel.thisPK
			it.year = ediUploadModel.year
			it.month = ediUploadModel.month
			it.day = ediUploadModel.day
		}
		val realPharmaNewData: MutableList<EDIUploadPharmaModel> = mutableListOf()
		mergeEDIPharmaMedicine(realPharma, realMedicineList, realPharmaNewData)
		// 여기까진 올린 데이터가 진짜 있나 없나만 보는 거임
		// 밑은 올린 데이터가 마감 기한을 넘겼으면 이월 시키는 거임
		// 사진 데이터는 2024-11-11 자료라고 해도, 처리하는 지금은 2025-01-10 이면 마감일자를 1월 기준으로 봄
		// 그리고 pharma 의 year, month, day 는 처리 된 날짜를 말하는 거임
		// medicine 의 year, month, day 는 약가 기준일을 말함.
		val dueDateList = ediPharmaDueDateRepository.selectAllByPharmaInThisYearMonthDueDate(serverTimeYear, serverTimeMonth).filter { x -> x.pharmaPK in realPharmaNewData.map { y -> y.pharmaPK } }
		ediUploadModel.orgName = hospital.orgName
		ediUploadModel.pharmaList = carriedOverPharma(realPharmaNewData, dueDateList).toMutableList()
		ediUploadModel.ediState = if (ediUploadModel.pharmaList.count { x -> x.ediState == EDIState.Pending } > 0) EDIState.Pending else EDIState.None
		ediUploadModel.regDate = serverTime
		ediUploadModel.fileList.onEach {
			it.thisPK = UUID.randomUUID().toString()
			it.ediPK = ediUploadModel.thisPK
		}

		// 지금 Reject 상태가 아닌 pharma, year, month 가 있을 경우 제거함.
		// 아 병원 같은 경우도 있네
//		ediUploadModel.pharmaList.removeIf { it.medicineList.isEmpty() }
//		val notRejectPharma = ediUploadPharmaRepository.selectAllByMyNotReject(tokenUser.thisPK, ediUploadModel.year, ediUploadModel.month).map { Triple(it.pharmaPK, it.year, it.month) }
//		ediUploadModel.pharmaList = ediUploadModel.pharmaList.filterNot { Triple(it.pharmaPK, it.year, it.month) in notRejectPharma }.toMutableList()

		if (ediUploadModel.pharmaList.isEmpty()) {
			throw EDIUploadPharmaExistException()
		}

		val ret = ediUploadRepository.save(ediUploadModel)
		ediUploadPharmaRepository.saveAll(ediUploadModel.pharmaList)
		ediUploadPharmaMedicineRepository.saveAll(ediUploadModel.pharmaList.flatMap { it.medicineList })
		ediUploadFileRepository.saveAll(ediUploadModel.fileList)
		requestRepository.save(RequestModel().apply {
			requestUserPK = tokenUser.thisPK
			requestItemPK = ediUploadModel.thisPK
			requestUserName = tokenUser.name
			requestType = RequestType.EDIUpload
		})
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add edi upload : ${ediUploadModel.thisPK} ${ediUploadModel.year}-${ediUploadModel.month}-${ediUploadModel.day}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun postNewEDIData(token: String, ediUploadModel: EDIUploadModel): EDIUploadModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		if (!canIUseApplyDate(token, ediUploadModel.year, ediUploadModel.month)) {
			throw NotValidOperationException("${ediUploadModel.year}-${ediUploadModel.month}")
		}

		val serverTime = Date()
		val serverTimeYear = FExtensions.parseDateTimeString(serverTime, "yyyy") ?: throw NotValidOperationException()
		val serverTimeMonth = FExtensions.parseDateTimeString(serverTime, "MM") ?: throw NotValidOperationException()
		val serverTimeDay = FExtensions.parseDateTimeString(serverTime, "dd") ?: throw NotValidOperationException()
		ediUploadModel.thisPK = UUID.randomUUID().toString()
		ediUploadModel.day = serverTimeDay
		ediUploadModel.regDate = serverTime
		ediUploadModel.userPK = tokenUser.thisPK
		ediUploadModel.id = tokenUser.id
		ediUploadModel.name = tokenUser.name
		var hospital = hospitalRepository.selectByNewHospital()
		if (hospital == null) {
			hospital = hospitalRepository.save(HospitalModel().apply {
				code = FConstants.NEW_HOSPITAL_CODE
				orgName = FConstants.NEW_HOSPITAL_NAME
				innerName = FConstants.NEW_HOSPITAL_NAME
			})
		}
		val existPharmaList = pharmaRepository.findAllByThisPKIn(ediUploadModel.pharmaList.map { it.pharmaPK }).filter { !it.inVisible }
		val realPharma = realPharmaCheck(ediUploadModel.thisPK, ediUploadModel.pharmaList, existPharmaList)
		realPharma.onEach {
			it.thisPK = UUID.randomUUID().toString()
			it.ediPK = ediUploadModel.thisPK
			it.year = serverTimeYear
			it.month = serverTimeMonth
			it.day = ediUploadModel.day
			it.medicineList.clear()
		}
		val dueDateList = ediPharmaDueDateRepository.selectAllByPharmaInThisYearMonthDueDate(serverTimeYear, serverTimeMonth).filter { x -> x.pharmaPK in realPharma.map { y -> y.pharmaPK } }

		ediUploadModel.hospitalPK = hospital.thisPK
		ediUploadModel.orgName = hospital.orgName
		ediUploadModel.pharmaList = carriedOverPharma(realPharma, dueDateList).toMutableList()
		ediUploadModel.ediState = EDIState.None
		ediUploadModel.regDate = serverTime
		ediUploadModel.fileList.onEach {
			it.thisPK = UUID.randomUUID().toString()
			it.ediPK = ediUploadModel.thisPK
		}

		val ret = ediUploadRepository.save(ediUploadModel)
		ediUploadPharmaRepository.saveAll(ediUploadModel.pharmaList)
		ediUploadFileRepository.saveAll(ediUploadModel.fileList)
		requestRepository.save(RequestModel().apply {
			requestUserPK = tokenUser.thisPK
			requestItemPK = ediUploadModel.thisPK
			requestUserName = tokenUser.name
			requestType = RequestType.EDIUpload
		})
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add edi upload : ${ediUploadModel.thisPK} ${ediUploadModel.year}-${ediUploadModel.month}-${ediUploadModel.day}")
		logRepository.save(logModel)
		return ret
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun postEDIFileUpload(token: String, thisPK: String, ediUploadFileModel: List<EDIUploadFileModel>): List<EDIUploadFileModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		val data = ediUploadRepository.findByThisPK(thisPK) ?: throw EDIUploadNotExistException()
		if (data.ediState == EDIState.OK || data.ediState == EDIState.Reject) {
			throw NotValidOperationException()
		}

		val ret = ediUploadFileRepository.saveAll(ediUploadFileModel.map { x -> EDIUploadFileModel().apply {
			ediPK = data.thisPK
			blobUrl = x.blobUrl
			originalFilename = x.originalFilename
			mimeType = x.mimeType
		}})

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add edi file : ${ediUploadFileModel.count()}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun deleteEDIFile(token: String, thisPK: String): EDIUploadFileModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		val data = ediUploadFileRepository.findByThisPK(thisPK) ?: throw EDIFileNotExistException()
		val edi = ediUploadRepository.findByThisPK(data.ediPK) ?: throw EDIUploadNotExistException()
		if (edi.userPK != tokenUser.thisPK) {
			throw NotValidOperationException()
		}
		if (edi.ediState == EDIState.OK || edi.ediState == EDIState.Reject) {
			throw NotValidOperationException()
		}

		data.inVisible = true
		ediUploadFileRepository.save(data)
//		ediUploadFileRepository.delete(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "del edi file : ${data.originalFilename}")
		logRepository.save(logModel)
		return data
	}
}