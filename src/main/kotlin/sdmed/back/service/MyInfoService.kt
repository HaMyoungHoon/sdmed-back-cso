package sdmed.back.service

import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.ConfirmPasswordUnMatchException
import sdmed.back.advice.exception.CurrentPWNotMatchException
import sdmed.back.advice.exception.SignUpPWConditionException
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.user.UserDataModel

class MyInfoService: UserService() {
	fun getMyData(token: String, childView: Boolean = false, relationView: Boolean = false, pharmaOwnMedicineView: Boolean = false): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}
		return getUserDataByPK(tokenUser.thisPK, childView, relationView, pharmaOwnMedicineView)
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun passwordChange(token: String, currentPW: String, afterPW: String, confirmPW: String): UserDataModel {
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}
		if (tokenUser.pw != fAmhohwa.encrypt(currentPW)) {
			throw CurrentPWNotMatchException()
		}
		if (afterPW.length < 4) {
			throw SignUpPWConditionException()
		}
		if (afterPW != confirmPW) {
			throw ConfirmPasswordUnMatchException()
		}

		val user = getUserDataByPK(tokenUser.thisPK)
		user.pw = fAmhohwa.encrypt(afterPW)
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${user.id} password change")
		logRepository.save(logModel)
		return ret
	}
}