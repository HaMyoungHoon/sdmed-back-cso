package sdmed.back.service

import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.ConfirmPasswordUnMatchException
import sdmed.back.advice.exception.CurrentPWNotMatchException
import sdmed.back.advice.exception.SignUpPWConditionException
import sdmed.back.advice.exception.UserTrainingFileUploadException
import sdmed.back.config.FExtensions
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.BlobUploadModel
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.user.UserDataModel
import sdmed.back.model.sqlCSO.user.UserTrainingModel
import java.util.Date

open class MyInfoService: UserService() {
	fun getMyData(token: String, childView: Boolean = false, relationView: Boolean = false, pharmaOwnMedicineView: Boolean = false, relationMedicineView: Boolean = true, trainingModelView: Boolean = true): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		isLive(tokenUser)
		return getUserDataByPK(tokenUser.thisPK, childView, relationView, pharmaOwnMedicineView, relationMedicineView, trainingModelView)
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun passwordChange(token: String, currentPW: String, afterPW: String, confirmPW: String): UserDataModel {
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

		val user = getUserDataByPK(tokenUser.thisPK)
		user.pw = fAmhohwa.encrypt(pwBuff)
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${user.id} password change")
		logRepository.save(logModel)
		return ret
	}
}