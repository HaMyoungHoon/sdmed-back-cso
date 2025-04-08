package sdmed.back.service.extra

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.EDIFileNotExistException
import sdmed.back.advice.exception.EDIUploadNotExistException
import sdmed.back.advice.exception.EDIUploadPharmaExistException
import sdmed.back.advice.exception.HospitalNotFoundException
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.RequestType
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.edi.EDIHosBuffModel
import sdmed.back.model.sqlCSO.edi.EDIMedicineBuffModel
import sdmed.back.model.sqlCSO.edi.EDIPharmaBuffModel
import sdmed.back.model.sqlCSO.edi.EDIState
import sdmed.back.model.sqlCSO.edi.EDIType
import sdmed.back.model.sqlCSO.edi.EDIUploadModel
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaFileModel
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaModel
import sdmed.back.model.sqlCSO.extra.ExtraEDIDueDateResponse
import sdmed.back.model.sqlCSO.hospital.HospitalModel
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import sdmed.back.model.sqlCSO.request.RequestModel
import sdmed.back.repository.sqlCSO.IUserRelationRepository
import java.util.Date
import java.util.UUID

class ExtraEDIRequestService: ExtraEDIService() {
    @Autowired lateinit var userRelationRepository: IUserRelationRepository
    fun getApplyDateList(token: String): List<ExtraEDIDueDateResponse> {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
            throw AuthenticationEntryPointException()
        }
        isLive(tokenUser)

        return extraEDIApplyDateRepository.selectAllByUse()
    }
    fun getHospitalList(token: String, applyDate: Date, withChild: Boolean = true): List<EDIHosBuffModel> {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
            throw AuthenticationEntryPointException()
        }
        isLive(tokenUser)
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
    fun getPharmaList(token: String, withMedicine: Boolean = false): List<EDIPharmaBuffModel> {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
            throw AuthenticationEntryPointException()
        }
        isLive(tokenUser)

        val ret = userRelationRepository.selectAllByInvisible().distinctBy { it.thisPK }
        if (withMedicine) {
//          val pharmaPK = ret.map { it.thisPK }
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

    @Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
    open fun postEDIData(token: String, ediUploadModel: EDIUploadModel): EDIUploadModel {
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
        ediUploadModel.pharmaList.forEach { x -> x.fileList.forEach { y -> y.initThisPK(x.thisPK) } }
        ediUploadModel.ediState = if (ediUploadModel.pharmaList.count { x -> x.ediState == EDIState.Pending } > 0) EDIState.Pending else EDIState.None
        ediUploadModel.regDate = serverTime

        // 지금 Reject 상태가 아닌 pharma, year, month 가 있을 경우 제거함.
        // 아 병원 같은 경우도 있네
//		ediUploadModel.pharmaList.removeIf { it.medicineList.isEmpty() }
//		val notRejectPharma = ediUploadPharmaRepository.selectAllByMyNotReject(tokenUser.thisPK, ediUploadModel.year, ediUploadModel.month).map { Triple(it.pharmaPK, it.year, it.month) }
//		ediUploadModel.pharmaList = ediUploadModel.pharmaList.filterNot { Triple(it.pharmaPK, it.year, it.month) in notRejectPharma }.toMutableList()

        if (ediUploadModel.pharmaList.isEmpty()) {
            throw EDIUploadPharmaExistException()
        }

        val ret = extraEDIUploadRepository.save(ediUploadModel)
        extraEDIUploadPharmaRepository.saveAll(ediUploadModel.pharmaList)
        extraEDIUploadPharmaMedicineRepository.saveAll(ediUploadModel.pharmaList.flatMap { it.medicineList })
        ediUploadModel.pharmaList.forEach { x -> extraEDIUploadPharmaFileRepository.saveAll(x.fileList) }
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
    open fun postNewEDIData(token: String, ediUploadModel: EDIUploadModel): EDIUploadModel {
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
        var hospital = if (ediUploadModel.ediType == EDIType.TRANSFER) {
            hospitalRepository.selectByTransferHospital()
        } else {
            hospitalRepository.selectByNewHospital()
        }
        if (hospital == null) {
            hospital = hospitalRepository.save(HospitalModel().apply {
                if(ediUploadModel.ediType == EDIType.TRANSFER) {
                    code = FConstants.TRANSFER_HOSPITAL_CODE
                    orgName = FConstants.TRANSFER_HOSPITAL_NAME
                    innerName = FConstants.TRANSFER_HOSPITAL_NAME
                } else {
                    code = FConstants.NEW_HOSPITAL_CODE
                    orgName = FConstants.NEW_HOSPITAL_NAME
                    innerName = FConstants.NEW_HOSPITAL_NAME
                }
            })
        }
        val existPharmaList = pharmaRepository.findAllByThisPKIn(ediUploadModel.pharmaList.map { it.pharmaPK }).filter { !it.inVisible }
        val realPharma = realPharmaCheck(ediUploadModel.thisPK, ediUploadModel.pharmaList, existPharmaList)
        realPharma.onEach {
            it.year = serverTimeYear
            it.month = serverTimeMonth
            it.day = ediUploadModel.day
            it.medicineList.clear()
        }
        val dueDateList = ediPharmaDueDateRepository.selectAllByPharmaInThisYearMonthDueDate(serverTimeYear, serverTimeMonth).filter { x -> x.pharmaPK in realPharma.map { y -> y.pharmaPK } }

        ediUploadModel.hospitalPK = hospital.thisPK
        ediUploadModel.orgName = hospital.orgName
        ediUploadModel.pharmaList = carriedOverPharma(realPharma, dueDateList).toMutableList()
        ediUploadModel.pharmaList.forEach { x -> x.fileList.forEach { y -> y.initThisPK(x.thisPK) } }
        ediUploadModel.ediState = EDIState.None
        ediUploadModel.regDate = serverTime

        val ret = extraEDIUploadRepository.save(ediUploadModel)
        extraEDIUploadPharmaRepository.saveAll(ediUploadModel.pharmaList)
        ediUploadModel.pharmaList.forEach { x -> extraEDIUploadPharmaFileRepository.saveAll(x.fileList) }
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
}