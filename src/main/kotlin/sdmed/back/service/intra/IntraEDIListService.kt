package sdmed.back.service.intra

import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.EDIFileNotExistException
import sdmed.back.advice.exception.EDIUploadNotExistException
import sdmed.back.advice.exception.EDIUploadPharmaMedicineNotExistException
import sdmed.back.advice.exception.EDIUploadPharmaNotExistException
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.config.FExtensions
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.ResponseType
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.edi.EDIState
import sdmed.back.model.sqlCSO.edi.EDIUploadModel
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaFileModel
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaMedicineModel
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaModel
import sdmed.back.model.sqlCSO.edi.EDIUploadResponseModel
import sdmed.back.model.sqlCSO.hospital.HospitalTempModel
import java.util.Date
import kotlin.collections.isNullOrEmpty

class IntraEDIListService: IntraEDIService() {
    fun getEDIUploadList(token: String, withFile: Boolean, startDate: Date, endDate: Date): List<EDIUploadModel> {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        isLive(tokenUser)
        val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
            throw AuthenticationEntryPointException()
        }
        val ret = intraEDIUploadRepository.selectAllByDate(queryDate.first, queryDate.second)
        val pharma = intraEDIUploadPharmaRepository.findAllByEdiPKIn(ret.map { it.thisPK })
        if (withFile) {
            val pharmaFile = intraEDIUploadPharmaFileRepository.findAllByEdiPharmaPKIn(pharma.map { it.thisPK })
            mergeEDIPharmaFile(pharma, pharmaFile)
        }
        val pharmaGroup = pharma.groupBy { it.ediPK }
        for (edi in ret) {
            val pharmaMap = pharmaGroup[edi.thisPK]
            if (!pharmaMap.isNullOrEmpty()) {
                edi.pharmaList.addAll(pharmaMap)
            }
        }

        return ret
    }
    fun getEDIUploadListMyChild(token: String, withFile: Boolean, startDate: Date, endDate: Date): List<EDIUploadModel> {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        isLive(tokenUser)
        val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
            throw AuthenticationEntryPointException()
        }
        val myChildPK = userChildPKRepository.selectAllByMotherPK(tokenUser.thisPK)
        val ret = intraEDIUploadRepository.selectAllByMyChildAndDate(queryDate.first, queryDate.second).filter { it.userPK in myChildPK }

        val pharma = intraEDIUploadPharmaRepository.findAllByEdiPKIn(ret.map { it.thisPK })
        if (withFile) {
            val pharmaFile = intraEDIUploadPharmaFileRepository.findAllByEdiPharmaPKIn(pharma.map { it.thisPK })
            mergeEDIPharmaFile(pharma, pharmaFile)
        }
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
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
            throw AuthenticationEntryPointException()
        }
        val data = intraEDIUploadRepository.findByThisPK(thisPK) ?: throw EDIUploadNotExistException()
        return parseEDIUploadModel(data)
    }

    @Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
    open fun postEDIResponseData(token: String, ediPharmaPK: String, responseData: EDIUploadResponseModel): EDIUploadModel {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
            throw AuthenticationEntryPointException()
        }

        val data = intraEDIUploadPharmaRepository.findByThisPK(ediPharmaPK) ?: throw EDIUploadNotExistException()
        data.ediState = responseData.ediState
        intraEDIUploadPharmaRepository.save(data)
        val mother = intraEDIUploadRepository.findByThisPK(data.ediPK) ?: throw EDIUploadNotExistException()
        mother.ediState = FExtensions.ediStateParse(intraEDIUploadPharmaRepository.findAllByEdiPKOrderByPharmaPK(mother.thisPK).map { it.ediState })
        val ret = intraEDIUploadRepository.save(mother)
        intraEDIUploadResponseRepository.save(EDIUploadResponseModel().apply {
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
    open fun postEDINewResponseData(token: String, ediPK: String, responseData: EDIUploadResponseModel): EDIUploadModel {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
            throw AuthenticationEntryPointException()
        }

        val ret = intraEDIUploadRepository.findByThisPK(ediPK) ?: throw EDIUploadNotExistException()
        ret.ediState = responseData.ediState
        intraEDIUploadResponseRepository.save(EDIUploadResponseModel().apply {
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
    open fun putEDIUpload(token: String, thisPK: String, ediUploadModel: EDIUploadModel): EDIUploadModel {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
            throw AuthenticationEntryPointException()
        }

        val data = parseEDIUploadModel(intraEDIUploadRepository.findByThisPK(thisPK) ?: throw EDIUploadNotExistException())
        data.safeCopy(ediUploadModel)
        intraEDIUploadPharmaMedicineRepository.saveAll(data.pharmaList.flatMap { it.medicineList })
        intraEDIUploadPharmaRepository.saveAll(data.pharmaList)

        val ret = intraEDIUploadRepository.save(data)
        val stackTrace = Thread.currentThread().stackTrace
        val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "modify edi : ${data.thisPK}")
        logRepository.save(logModel)
        return ret
    }
    @Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
    open fun putEDIUpload(token: String, thisPK: String, hospitalTempModel: HospitalTempModel): EDIUploadModel {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
            throw AuthenticationEntryPointException()
        }
        val data = intraEDIUploadRepository.findByThisPK(thisPK) ?: throw EDIUploadNotExistException()
        data.tempHospitalPK = hospitalTempModel.thisPK
        data.tempOrgName = hospitalTempModel.orgName

        val ret = intraEDIUploadRepository.save(data)
        val stackTrace = Thread.currentThread().stackTrace
        val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "modify edi place : ${data.thisPK} ${hospitalTempModel.thisPK} ${hospitalTempModel.orgName}")
        logRepository.save(logModel)
        return ret
    }
    @Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
    open fun putEDIPharma(token: String, thisPK: String, pharma: EDIUploadPharmaModel): EDIUploadPharmaModel {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
            throw AuthenticationEntryPointException()
        }

        val data = intraEDIUploadPharmaRepository.findByThisPK(thisPK) ?: throw EDIUploadPharmaNotExistException()
        data.medicineList = intraEDIUploadPharmaMedicineRepository.findByThisPKIn(pharma.medicineList.map { x -> x.thisPK }).toMutableList()
        data.safeCopy(pharma)
        intraEDIUploadPharmaMedicineRepository.saveAll(data.medicineList)
        val ret = intraEDIUploadPharmaRepository.save(data)
        val stackTrace = Thread.currentThread().stackTrace
        val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "modify edi pharma : ${data.thisPK} ${data.orgName}")
        logRepository.save(logModel)
        return ret
    }
    @Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
    open fun putEDIPharmaState(token: String, thisPK: String, pharma: EDIUploadPharmaModel): EDIUploadPharmaModel {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
            throw AuthenticationEntryPointException()
        }

        val data = intraEDIUploadPharmaRepository.findByThisPK(thisPK) ?: throw EDIUploadPharmaNotExistException()
        data.ediState = pharma.ediState
        val ret = intraEDIUploadPharmaRepository.save(data)
        val mother = intraEDIUploadRepository.findByThisPK(data.ediPK) ?: throw EDIUploadNotExistException()
        val allChildEdiState = intraEDIUploadPharmaRepository.findAllByEdiPKOrderByPharmaPK(mother.thisPK).map { x -> x.ediState }
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
        intraEDIUploadRepository.save(mother)
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
    open fun putEDIMedicine(token: String, thisPK: String, medicine: EDIUploadPharmaMedicineModel): EDIUploadPharmaMedicineModel {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
            throw AuthenticationEntryPointException()
        }

        val data = intraEDIUploadPharmaMedicineRepository.findByThisPK(thisPK) ?: throw EDIUploadPharmaMedicineNotExistException()
        data.safeCopy(medicine)
        val ret = intraEDIUploadPharmaMedicineRepository.save(data)
        val stackTrace = Thread.currentThread().stackTrace
        val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "modify edi pharma medicine : ${data.thisPK} ${data.name}")
        logRepository.save(logModel)
        return ret
    }
    @Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
    open fun deleteEDIMedicine(token: String, thisPK: String): EDIUploadPharmaMedicineModel {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
            throw AuthenticationEntryPointException()
        }

        val data = intraEDIUploadPharmaMedicineRepository.findByThisPK(thisPK) ?: throw EDIUploadPharmaMedicineNotExistException()
        val edi = intraEDIUploadRepository.findByThisPK(data.ediPK) ?: throw EDIUploadNotExistException()
        if (edi.ediState == EDIState.OK || edi.ediState == EDIState.Reject) {
            throw NotValidOperationException()
        }

        data.inVisible = true
        intraEDIUploadPharmaMedicineRepository.save(data)
//		intraEDIUploadPharmaMedicineRepository.delete(data)
        val stackTrace = Thread.currentThread().stackTrace
        val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "del edi pharma medicine : ${data.thisPK} ${data.name}")
        logRepository.save(logModel)
        return data
    }
    @Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
    open fun deleteEDIMedicine(token: String, thisPK: List<String>): String {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
            throw AuthenticationEntryPointException()
        }

        val data = intraEDIUploadPharmaMedicineRepository.findByThisPKIn(thisPK)
        val edis = intraEDIUploadRepository.findByThisPKIn(data.map { it.ediPK }).filter { it.ediState == EDIState.None }
        // 근데 edi 가 여러 개일 수가 있나?
        val deletableData = data.filter { x -> x.ediPK in edis.map { it.thisPK } }
        if (deletableData.isEmpty()) {
            return "count 0"
        }
        deletableData.onEach { x -> x.inVisible = true }
        intraEDIUploadPharmaMedicineRepository.saveAll(deletableData)
//		intraEDIUploadPharmaMedicineRepository.deleteAll(deletableData)
        val stackTrace = Thread.currentThread().stackTrace
        val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "del edi pharma medicine : ${deletableData.count()}")
        logRepository.save(logModel)
        return "count: ${deletableData.count()}"
    }
    @Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
    open fun deleteEDIPharmaFile(token: String, thisPK: String): EDIUploadPharmaFileModel {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger, UserRole.BusinessMan))) {
            throw AuthenticationEntryPointException()
        }

        val data = intraEDIUploadPharmaFileRepository.findByThisPK(thisPK) ?: throw EDIFileNotExistException()
        val edi = intraEDIUploadRepository.selectByPharmaPK(data.pharmaPK) ?: throw EDIUploadNotExistException()
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin))) {
            if (edi.userPK != tokenUser.thisPK) {
                throw NotValidOperationException()
            }
        }
        if (edi.ediState == EDIState.OK || edi.ediState == EDIState.Reject) {
            throw NotValidOperationException()
        }

        data.inVisible = true
        intraEDIUploadPharmaFileRepository.save(data)
//		intraEDIUploadPharmaFileRepository.delete(data)
        val stackTrace = Thread.currentThread().stackTrace
        val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "del edi pharma file : target = $thisPK ${data.originalFilename}")
        logRepository.save(logModel)
        return data
    }
}