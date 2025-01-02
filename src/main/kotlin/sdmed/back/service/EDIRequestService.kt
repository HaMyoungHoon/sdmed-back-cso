package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.EDIUploadNotExistException
import sdmed.back.advice.exception.HospitalNotFoundException
import sdmed.back.advice.exception.NotValidOperationException
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
	fun getHospitalList(token: String): List<HospitalModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		return userRelationRepository.selectAllMyHospital(tokenUser.thisPK)
	}
	fun getPharmaList(token: String, hosPK: String, withMedicine: Boolean = true): List<EDIPharmaBuffModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		val ret = userRelationRepository.selectAllMyPharma(tokenUser.thisPK, hosPK).distinctBy { it.thisPK }
		if (withMedicine) {
			val pharmaPKString = ret.map { it.thisPK }.joinToString(",") { it }
			val medicine = userRelationRepository.selectAllMyMedicine(tokenUser.thisPK, hosPK, pharmaPKString).distinctBy { it.thisPK }
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

		val pharmaPKString = pharmaPK.joinToString(",") { it }
		return userRelationRepository.selectAllMyMedicine(tokenUser.thisPK, hosPK, pharmaPKString)
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
		ediUploadModel.name = tokenUser.name
		val hospital = hospitalRepository.findByThisPK(ediUploadModel.hospitalPK) ?: throw HospitalNotFoundException()
		val existPharmaList = pharmaRepository.findAllByThisPKIn(ediUploadModel.pharmaList.map { it.pharmaPK })

		val realPharma = realPharmaCheck(ediUploadModel.thisPK, ediUploadModel.pharmaList, existPharmaList)
		val existMedicineList = medicineRepository.findAllByThisPKIn(realPharma.flatMap { x -> x.medicineList.map { y -> y.medicinePK } })

		val kdCodeString = existMedicineList.map { it.kdCode } // .joinToString { "$it" }
		val yearMonthDay = "${ediUploadModel.year}-${ediUploadModel.month}-${ediUploadModel.day}"
		val medicinePriceList = medicinePriceRepository.selectAllByRecentDataKDCodeInAndYearMonth(kdCodeString, yearMonthDay)
		val existMedicineNewData = mutableListOf<MedicineModel>()
		mergeMedicinePrice(existMedicineList, medicinePriceList, existMedicineNewData)

		val realMedicineList = realMedicineCheck(ediUploadModel.thisPK, realPharma.flatMap { it.medicineList }, existMedicineNewData)
		realPharma.onEach {
			it.thisPK = UUID.randomUUID().toString()
			it.ediPK = ediUploadModel.thisPK
			it.year = ediUploadModel.year
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
		val pharmaPKString = realPharmaNewData.map { it.pharmaPK }.joinToString { it }
		val dueDateList = ediPharmaDueDateRepository.selectAllByPharmaInThisYearMonthDueDate(pharmaPKString, serverTimeYear, serverTimeMonth)
		ediUploadModel.orgName = hospital.orgName
		ediUploadModel.pharmaList = carriedOverPharma(realPharmaNewData, dueDateList).toMutableList()
		ediUploadModel.ediState = if (ediUploadModel.pharmaList.count { x -> x.ediState == EDIState.Pending } > 0) EDIState.Pending else EDIState.None
		ediUploadModel.regDate = serverTime
		ediUploadModel.fileList.onEach {
			it.thisPK = UUID.randomUUID().toString()
			it.ediPK = ediUploadModel.thisPK
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
	fun postEDIFileUpload(token: String, thisPK: String, ediUploadFileModel: EDIUploadFileModel): EDIUploadFileModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}

		val data = ediUploadRepository.findByThisPK(thisPK) ?: throw EDIUploadNotExistException()
		if (data.ediState == EDIState.OK) {
			throw NotValidOperationException()
		}

		val ret = ediUploadFileRepository.save(EDIUploadFileModel().apply {
			ediPK = data.thisPK
			blobUrl = ediUploadFileModel.blobUrl
			originalFilename = ediUploadFileModel.originalFilename
			mimeType = ediUploadFileModel.mimeType
		})

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add edi file : ${ediUploadFileModel.blobUrl}")
		logRepository.save(logModel)
		return ret
	}
}