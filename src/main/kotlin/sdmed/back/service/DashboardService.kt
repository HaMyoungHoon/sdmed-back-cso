package sdmed.back.service

import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.config.FServiceBase
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.RequestModel
import sdmed.back.model.sqlCSO.user.UserDataModel
import java.util.*

class DashboardService: FServiceBase() {

	fun getListByMyChildNoResponse(token: String): List<RequestModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}
		val childPKLIst = userChildPKRepository.selectAllByMotherPK(tokenUser.thisPK)

		return requestRepository.findAllByResponseTypeAndRequestUserPKInOrderByRequestDateDesc(requestUserPK = childPKLIst)
	}
	fun getListByNoResponse(token: String): List<RequestModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		return requestRepository.selectAllByNoResponse()
	}
	fun getListByDate(token: String, startDate: Date, endDate: Date): List<RequestModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		if (startDate > endDate) {
			return requestRepository.selectAllByBetweenRequestDate(endDate, startDate)
		}

		return requestRepository.selectAllByBetweenRequestDate(startDate, endDate)
	}

	fun getUserData(token: String, thisPK: String): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		return getUserDataPK(thisPK)
	}
}