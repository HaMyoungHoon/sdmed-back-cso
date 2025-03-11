package sdmed.back.service

import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.*
import sdmed.back.config.FExtensions
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.ResponseType
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.edi.*
import sdmed.back.model.sqlCSO.hospital.HospitalTempModel
import java.util.*

class EDIListService: EDIService() {
	fun getEDIUploadList(token: String, startDate: Date, endDate: Date): List<EDIUploadModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)
		val ret = if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			ediUploadRepository.selectAllByMe(tokenUser.thisPK, queryDate.first, queryDate.second)
		} else {
			ediUploadRepository.selectAllByDate(queryDate.first, queryDate.second)
		}

		val pharma = ediUploadPharmaRepository.findAllByEdiPKIn(ret.map { it.thisPK })
		val pharmaGroup = pharma.groupBy { it.ediPK }
		for (edi in ret) {
			val pharmaMap = pharmaGroup[edi.thisPK]
			if (!pharmaMap.isNullOrEmpty()) {
				edi.pharmaList.addAll(pharmaMap)
			}
		}

		return ret
	}
	fun getEDIUploadListMyChild(token: String, startDate: Date, endDate: Date): List<EDIUploadModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)
		val ret = if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			ediUploadRepository.selectAllByMe(tokenUser.thisPK, queryDate.first, queryDate.second)
		} else {
			val myChildPK = userChildPKRepository.selectAllByMotherPK(tokenUser.thisPK)
			if (myChildPK.isEmpty()) {
				arrayListOf()
			} else {
				ediUploadRepository.selectAllByMyChildAndDate(queryDate.first, queryDate.second).filter { it.userPK in myChildPK }
			}
		}

		val pharma = ediUploadPharmaRepository.findAllByEdiPKIn(ret.map { it.thisPK })
		val pharmaGroup = pharma.groupBy { it.ediPK }
		for (edi in ret) {
			val pharmaMap = pharmaGroup[edi.thisPK]
			if (!pharmaMap.isNullOrEmpty()) {
				edi.pharmaList.addAll(pharmaMap)
			}
		}
		return ret
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
	fun postEDIResponseData(token: String, ediPharmaPK: String, responseData: EDIUploadResponseModel): EDIUploadModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val data = ediUploadPharmaRepository.findByThisPK(ediPharmaPK) ?: throw EDIUploadNotExistException()
		data.ediState = responseData.ediState
		ediUploadPharmaRepository.save(data)
		val mother = ediUploadRepository.findByThisPK(data.ediPK) ?: throw EDIUploadNotExistException()
		mother.ediState = FExtensions.ediStateParse(ediUploadPharmaRepository.findAllByEdiPKOrderByPharmaPK(mother.thisPK).map { it.ediState })
		val ret = ediUploadRepository.save(mother)
		ediUploadResponseRepository.save(EDIUploadResponseModel().apply {
			this.ediPK = mother.thisPK
			this.pharmaPK = data.thisPK
			this.pharmaName = data.orgName
			this.userPK = tokenUser.thisPK
			this.userName = tokenUser.name
			this.etc = responseData.etc
			this.ediState = responseData.ediState
		})
		val request = requestRepository.findByRequestItemPK(mother.thisPK)
		if (request != null) {
			request.responseUserPK = tokenUser.thisPK
			request.responseUserName = tokenUser.name
			request.responseDate = Date()
			request.responseType = FExtensions.ediStateToResponseType(mother.ediState)
			requestRepository.save(request)
		}
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add response : ${responseData.thisPK}, ${responseData.ediState}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun postEDINewResponseData(token: String, ediPK: String, responseData: EDIUploadResponseModel): EDIUploadModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val ret = ediUploadRepository.findByThisPK(ediPK) ?: throw EDIUploadNotExistException()
		ret.ediState = responseData.ediState
		ediUploadResponseRepository.save(EDIUploadResponseModel().apply {
			this.ediPK = ediPK
			this.pharmaName = "신규처"
			this.userPK = tokenUser.thisPK
			this.userName = tokenUser.name
			this.etc = responseData.etc
			this.ediState = responseData.ediState
		})
		val request = requestRepository.findByRequestItemPK(ediPK)
		if (request != null) {
			request.responseUserPK = tokenUser.thisPK
			request.responseUserName = tokenUser.name
			request.responseDate = Date()
			request.responseType = FExtensions.ediStateToResponseType(responseData.ediState)
			requestRepository.save(request)
		}
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add response : ${responseData.thisPK}")
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

		val ret = ediUploadRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "modify edi : ${data.thisPK}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun putEDIUpload(token: String, thisPK: String, hospitalTempModel: HospitalTempModel): EDIUploadModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}
		val data = ediUploadRepository.findByThisPK(thisPK) ?: throw EDIUploadNotExistException()
		data.tempHospitalPK = hospitalTempModel.thisPK
		data.tempOrgName = hospitalTempModel.orgName

		val ret = ediUploadRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "modify edi place : ${data.thisPK} ${hospitalTempModel.thisPK} ${hospitalTempModel.orgName}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun putEDIPharma(token: String, thisPK: String, pharma: EDIUploadPharmaModel): EDIUploadPharmaModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val data = ediUploadPharmaRepository.findByThisPK(thisPK) ?: throw EDIUploadPharmaNotExistException()
		data.medicineList = ediUploadPharmaMedicineRepository.findByThisPKIn(pharma.medicineList.map { x -> x.thisPK }).toMutableList()
		data.safeCopy(pharma)
		ediUploadPharmaMedicineRepository.saveAll(data.medicineList)
		val ret = ediUploadPharmaRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "modify edi pharma : ${data.thisPK} ${data.orgName}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun putEDIPharmaState(token: String, thisPK: String, pharma: EDIUploadPharmaModel): EDIUploadPharmaModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val data = ediUploadPharmaRepository.findByThisPK(thisPK) ?: throw EDIUploadPharmaNotExistException()
		data.ediState = pharma.ediState
		val ret = ediUploadPharmaRepository.save(data)
		val mother = ediUploadRepository.findByThisPK(data.ediPK) ?: throw EDIUploadNotExistException()
		val allChildEdiState = ediUploadPharmaRepository.findAllByEdiPKOrderByPharmaPK(mother.thisPK).map { x -> x.ediState }
		var responseType = ResponseType.None
		if (allChildEdiState.count { x -> x == EDIState.None } == allChildEdiState.count()) {
			mother.ediState = EDIState.None
		} else if (allChildEdiState.count { x -> x == EDIState.Reject } > 0) {
			mother.ediState = EDIState.Reject
			responseType = ResponseType.Reject
		} else if (allChildEdiState.count { x -> x == EDIState.OK } == allChildEdiState.count()) {
			mother.ediState = EDIState.OK
			responseType = ResponseType.OK
		} else if (allChildEdiState.count { x -> x == EDIState.OK } > 0 ){
			mother.ediState = EDIState.Partial
			responseType = ResponseType.Recep
		} else {
			mother.ediState = EDIState.Pending
			responseType = ResponseType.Pending
		}
		ediUploadRepository.save(mother)
		val request = requestRepository.findByRequestItemPK(mother.thisPK)
		if (request != null) {
			request.responseUserPK = tokenUser.thisPK
			request.responseUserName = tokenUser.name
			request.responseDate = Date()
			request.responseType = responseType
			requestRepository.save(request)
		}
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
		if (edi.ediState == EDIState.OK || edi.ediState == EDIState.Reject) {
			throw NotValidOperationException()
		}

		data.inVisible = true
		ediUploadPharmaMedicineRepository.save(data)
//		ediUploadPharmaMedicineRepository.delete(data)
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
		deletableData.onEach { x -> x.inVisible = true }
		ediUploadPharmaMedicineRepository.saveAll(deletableData)
//		ediUploadPharmaMedicineRepository.deleteAll(deletableData)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "del edi pharma medicine : ${deletableData.count()}")
		logRepository.save(logModel)
		return "count: ${deletableData.count()}"
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun deleteEDIFile(token: String, thisPK: String): EDIUploadFileModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger))) {
			throw AuthenticationEntryPointException()
		}

		val data = ediUploadFileRepository.findByThisPK(thisPK) ?: throw EDIFileNotExistException()
		val edi = ediUploadRepository.findByThisPK(data.ediPK) ?: throw EDIUploadNotExistException()
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