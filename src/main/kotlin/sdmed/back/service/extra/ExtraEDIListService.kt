package sdmed.back.service.extra

import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.EDIFileNotExistException
import sdmed.back.advice.exception.EDIUploadNotExistException
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.config.FExtensions
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.edi.EDIState
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaFileModel
import sdmed.back.model.sqlCSO.extra.ExtraEDIDetailResponse
import sdmed.back.model.sqlCSO.extra.ExtraEDIListResponse
import java.util.Date

class ExtraEDIListService: ExtraEDIService() {
    fun getEDIUploadList(token: String, startDate: Date, endDate: Date): List<ExtraEDIListResponse> {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
            throw AuthenticationEntryPointException()
        }

        val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)
        val ret = extraEDIUploadRepository.selectAllEDIUploadList(tokenUser.thisPK, queryDate.first, queryDate.second)
        val pharma = extraEDIUploadPharmaRepository.selectAllEDIPKIn(ret.map { it.thisPK })
        val pharmaGroup = pharma.groupBy { it.ediPK }
        for (edi in ret) {
            val pharmaMap = pharmaGroup[edi.thisPK]
            if (!pharmaMap.isNullOrEmpty()) {
                edi.pharmaList.addAll(pharmaMap.map { it.orgName })
            }
        }

        return ret
    }
    fun getEDIUploadDetail(token: String, thisPK: String): ExtraEDIDetailResponse {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
            throw AuthenticationEntryPointException()
        }
        val data = extraEDIUploadRepository.findByUserPKAndThisPK(tokenUser.thisPK, thisPK) ?: throw EDIUploadNotExistException()
        return parseExtraEDIDetail(data)
    }
    @Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
    open fun postEDIPharmaFileUpload(token: String, ediPK: String, ediPharmaPK: String, ediUploadPharmaFileModel: List<EDIUploadPharmaFileModel>): List<EDIUploadPharmaFileModel> {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
            throw AuthenticationEntryPointException()
        }
        if (ediUploadPharmaFileModel.isEmpty()) {
            return emptyList()
        }

        val data = extraEDIUploadRepository.findByThisPK(ediPK) ?: throw EDIUploadNotExistException()
        if (data.ediState == EDIState.OK || data.ediState == EDIState.Reject) {
            throw NotValidOperationException()
        }
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin))) {
            if (data.userPK != tokenUser.thisPK ) {
                throw AuthenticationEntryPointException()
            }
        }

        val targetEDIPharma = extraEDIUploadPharmaRepository.findAllByEdiPKOrderByPharmaPK(ediPK).find { it.thisPK == ediPharmaPK } ?: return emptyList()
        if (targetEDIPharma.ediState == EDIState.OK || targetEDIPharma.ediState == EDIState.Reject) {
            throw NotValidOperationException()
        }

        val ret = extraEDIUploadPharmaFileRepository.saveAll(ediUploadPharmaFileModel.map { x -> EDIUploadPharmaFileModel().apply {
            this.ediPharmaPK = ediPharmaPK
            this.pharmaPK = x.pharmaPK
            this.blobUrl = x.blobUrl
            this.originalFilename = x.originalFilename
            this.mimeType = x.mimeType
        }})

        val stackTrace = Thread.currentThread().stackTrace
        val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add edi pharma file : target = $ediPharmaPK ${ediUploadPharmaFileModel.count()}")
        logRepository.save(logModel)
        return ret
    }
    @Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
    open fun deleteEDIPharmaFile(token: String, thisPK: String): EDIUploadPharmaFileModel {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger, UserRole.BusinessMan))) {
            throw AuthenticationEntryPointException()
        }

        val data = extraEDIUploadPharmaFileRepository.findByThisPK(thisPK) ?: throw EDIFileNotExistException()
        val edi = extraEDIUploadRepository.selectByPharmaPK(data.pharmaPK) ?: throw EDIUploadNotExistException()
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin))) {
            if (edi.userPK != tokenUser.thisPK) {
                throw NotValidOperationException()
            }
        }
        if (edi.ediState == EDIState.OK || edi.ediState == EDIState.Reject) {
            throw NotValidOperationException()
        }

        data.inVisible = true
        extraEDIUploadPharmaFileRepository.save(data)
//		extraEDIUploadPharmaFileRepository.delete(data)
        val stackTrace = Thread.currentThread().stackTrace
        val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "del edi pharma file : target = $thisPK ${data.originalFilename}")
        logRepository.save(logModel)
        return data
    }
}