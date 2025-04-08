package sdmed.back.service.extra

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.ConfirmPasswordUnMatchException
import sdmed.back.advice.exception.CurrentPWNotMatchException
import sdmed.back.advice.exception.SignUpPWConditionException
import sdmed.back.advice.exception.UserNotFoundException
import sdmed.back.advice.exception.UserTrainingFileUploadException
import sdmed.back.config.FExtensions
import sdmed.back.config.FServiceBase
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.BlobUploadModel
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.extra.ExtraMyInfoResponse
import sdmed.back.model.sqlCSO.user.UserFileModel
import sdmed.back.model.sqlCSO.user.UserFileType
import sdmed.back.model.sqlCSO.user.UserTrainingModel
import sdmed.back.repository.extra.ExtraHospitalRepository
import sdmed.back.repository.extra.ExtraPharmaRepository
import sdmed.back.repository.extra.ExtraUserRelationRepository
import sdmed.back.repository.extra.ExtraUserTrainingRepository
import java.util.Date

class ExtraMyInfoService: FServiceBase() {
    @Autowired lateinit var extraUserRelationRepository: ExtraUserRelationRepository
    @Autowired lateinit var extraHospitalRepository: ExtraHospitalRepository
    @Autowired lateinit var extraPharmaRepository: ExtraPharmaRepository
    @Autowired lateinit var extraUserTrainingRepository: ExtraUserTrainingRepository
    fun getMyData(token: String): ExtraMyInfoResponse {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        isLive(tokenUser)
        val ret = ExtraMyInfoResponse().parse(userDataRepository.selectByPK(tokenUser.thisPK)) ?: throw UserNotFoundException()
        val userRelationModel = extraUserRelationRepository.findAllByUserPK(tokenUser.thisPK)
        val hosBuff = extraHospitalRepository.selectAllMyHospital(userRelationModel.map { it.hosPK }).toMutableList()
        val pharmaBuff = extraPharmaRepository.selectAllMyPharma(userRelationModel.map { it.pharmaPK }).toMutableList()
        for (hos in hosBuff) {
            val pharmaRel = userRelationModel.filter { x -> x.hosPK == hos.thisPK }
            for (rel in pharmaRel) {
                val pharma = hos.pharmaList.find { x -> x.thisPK == rel.pharmaPK } ?: pharmaBuff.find { x -> x.thisPK == rel.pharmaPK }?.clone() ?: continue
                if (hos.pharmaList.find { x -> x.thisPK == rel.pharmaPK } == null) {
                    hos.pharmaList.add(pharma)
                }
            }
            ret.hosList.add(hos)
        }
        ret.fileList = userFileRepository.findAllByUserPK(ret.thisPK).toMutableList()
        ret.trainingList = extraUserTrainingRepository.findAllByUserPKOrderByTrainingDateDesc(ret.thisPK).toMutableList()
        return ret
    }
    @Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
    open fun addMyTrainingModel(token: String, userPK: String, trainingDate: Date, uploadModel: BlobUploadModel): UserTrainingModel {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        isLive(tokenUser)
        if (tokenUser.thisPK != userPK) {
            throw AuthenticationEntryPointException()
        }
        val user = userDataRepository.selectByPK(tokenUser.thisPK) ?: throw UserNotFoundException()
        val buff = UserTrainingModel().safeCopy(uploadModel).apply {
            this.userPK = userPK
            this.trainingDate = trainingDate
        }
        if (extraUserTrainingRepository.findByUserPKAndTrainingDate(buff.userPK, buff.trainingDate) != null) {
            throw UserTrainingFileUploadException("already exist")
        }

        val ret = extraUserTrainingRepository.save(buff)

        val stackTrace = Thread.currentThread().stackTrace
        val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${user.id} ${buff.blobUrl} : ${buff.trainingDate}")
        logRepository.save(logModel)
        return ret
    }
    @Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
    open fun passwordChange(token: String, currentPW: String, afterPW: String, confirmPW: String): ExtraMyInfoResponse {
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
            throw AuthenticationEntryPointException()
        }
        val pwBuff = afterPW.trim()
        val pwBuffConfirm = confirmPW.trim()
        if (tokenUser.pw != fAmhohwa.encrypt(currentPW)) {
            throw CurrentPWNotMatchException()
        }
        if (FExtensions.regexPasswordCheck(afterPW) != true) {
            throw SignUpPWConditionException()
        }
        if (pwBuff != pwBuffConfirm) {
            throw ConfirmPasswordUnMatchException()
        }

        val user = userDataRepository.selectByPK(tokenUser.thisPK) ?: throw UserNotFoundException()
        user.pw = fAmhohwa.encrypt(pwBuff)
        val ret = userDataRepository.save(user)
        val stackTrace = Thread.currentThread().stackTrace
        val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${user.id} password change")
        logRepository.save(logModel)
        return ExtraMyInfoResponse().parse(ret) ?: ExtraMyInfoResponse()
    }
    @Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
    open fun userFileUrlModify(token: String, userPK: String, blobModel: BlobUploadModel, userFileType: UserFileType): UserFileModel {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (tokenUser.thisPK != userPK) {
            if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
                throw AuthenticationEntryPointException()
            }
        }
        isLive(tokenUser)
        val user = userDataRepository.selectByPK(tokenUser.thisPK) ?: throw UserNotFoundException()
        val userFile = userFileRepository.findByUserPKAndUserFileType(user.thisPK, userFileType)
        val ret = if (userFile == null) {
            userFileRepository.save(UserFileModel().apply {
                this.userPK = user.thisPK
                this.blobUrl = blobModel.blobUrl
                this.originalFilename = blobModel.originalFilename
                this.mimeType = blobModel.mimeType
                this.userFileType = userFileType
            })
        } else {
            userFileRepository.save(userFile.apply {
                this.blobUrl = blobModel.blobUrl
                this.originalFilename = blobModel.originalFilename
                this.mimeType = blobModel.mimeType
            })
        }
        val stackTrace = Thread.currentThread().stackTrace
        val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${user.id} ${userFileType} : ${blobModel.blobUrl}")
        logRepository.save(logModel)
        return ret
    }
}