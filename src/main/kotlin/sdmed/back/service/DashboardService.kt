package sdmed.back.service

import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.RequestModelNotFoundException
import sdmed.back.config.FExtensions
import sdmed.back.config.FServiceBase
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.ResponseType
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.request.RequestModel
import sdmed.back.model.sqlCSO.request.RequestUserCountModel
import sdmed.back.model.sqlCSO.request.ResponseCountModel
import sdmed.back.model.sqlCSO.user.UserDataModel
import java.util.*

class DashboardService: FServiceBase() {

	fun getListByMyChildNoResponse(token: String): List<RequestModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}
		val childPKList = userChildPKRepository.selectAllByMotherPK(tokenUser.thisPK)

		return requestRepository.findAllByResponseTypeNotAndRequestUserPKInOrderByRequestDateDesc(requestUserPK = childPKList)
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

		val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)
		return requestRepository.selectAllByBetweenRequestDate(queryDate.first, queryDate.second)
	}

	fun getUserData(token: String, thisPK: String): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		return getUserDataPK(thisPK)
	}

	fun getCountOfResponseType(token: String, startDate: Date, endDate: Date): List<ResponseCountModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)
		return requestRepository.selectCountOfResponseType(queryDate.first, queryDate.second)
	}

	fun getTop10RequestUser(token: String, startDate: Date, endDate: Date): List<RequestUserCountModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)
		return requestRepository.selectTop10RequestUser(queryDate.first, queryDate.second)
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun putRequestRecep(token: String, thisPK: String): RequestModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val requestModel = requestRepository.findByThisPK(thisPK) ?: throw RequestModelNotFoundException()
		if (requestModel.responseType != ResponseType.None) {
			return requestModel
		}

		requestModel.responseType = ResponseType.Recep
		requestModel.responseUserPK = tokenUser.thisPK
		requestModel.responseUserName = tokenUser.name
		requestModel.responseDate = Date()
		return requestRepository.save(requestModel)
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun putRequestModelResponseData(token: String, thisPK: String, responseType: ResponseType): RequestModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val data = requestRepository.findByThisPK(thisPK) ?: throw RequestModelNotFoundException()

		data.responseType = responseType
		data.responseDate = Date()
		data.responseUserPK = tokenUser.thisPK
		data.responseUserName = tokenUser.name
		return requestRepository.save(data)
	}
}