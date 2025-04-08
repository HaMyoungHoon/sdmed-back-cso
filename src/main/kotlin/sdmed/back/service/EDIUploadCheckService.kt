package sdmed.back.service

import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.edi.EDIUploadCheckModel
import sdmed.back.model.sqlCSO.edi.EDIUploadCheckSubModel
import sdmed.back.model.sqlCSO.user.UserDataModel
import java.util.*


class EDIUploadCheckService: EDIService() {
	fun getList(token: String, date: Date, isEDIDate: Boolean = true, isMyChild: Boolean = true): List<EDIUploadCheckModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger))) {
			throw AuthenticationEntryPointException()
		}
		isLive(tokenUser)
		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()
		val userList = getUserList(token, isMyChild).map { it.thisPK }
		val ret = ediUploadRepository.selectCheckModelNullIn(userList)
		val subNullList = ediUploadRepository.selectCheckSubModelNullIn(userList)
		val subUploadList = if (isEDIDate) {
			ediUploadRepository.selectCheckSubModelEDIDateIn2(userList, year, month, ret.map { it.hospitalPK })
		} else {
			ediUploadRepository.selectCheckSubModelApplyDateIn2(userList, year, month, ret.map { it.hospitalPK })
		}.filter { x -> !x.isNullValue() }
		val retSub = mutableListOf<EDIUploadCheckSubModel>()
		retSub.addAll(subUploadList)
		retSub.addAll(subNullList.filterNot { x ->
			subUploadList.any { y -> x.userPK == y.userPK && x.hospitalPK == y.hospitalPK && x.pharmaPK == y.pharmaPK
			}
		})
		val checkSubModelMap = retSub.groupBy { Pair(it.userPK, it.hospitalPK) }
		ret.forEach { x ->
			val mapBuff = checkSubModelMap[Pair(x.userPK, x.hospitalPK)]
			if (!mapBuff.isNullOrEmpty()) {
				x.subModel.addAll(mapBuff)
			}
		}

		return ret
	}
	fun getData(token: String, date: Date, userPK: String, isEDIDate: Boolean = true): List<EDIUploadCheckModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger))) {
			throw AuthenticationEntryPointException()
		}
		isLive(tokenUser)
		val targetUser = getUserDataPK(userPK)
		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()
		val ret = ediUploadRepository.selectCheckModelNull(targetUser.thisPK).toMutableList()
		hospitalRepository.findByCode(FConstants.NEW_HOSPITAL_CODE)?.let {
			ret.add(EDIUploadCheckModel(targetUser.id, targetUser.name, targetUser.thisPK, it.thisPK, it.orgName, it.innerName))
		}
		hospitalRepository.findByCode(FConstants.TRANSFER_HOSPITAL_CODE)?.let {
			ret.add(EDIUploadCheckModel(targetUser.id, targetUser.name, targetUser.thisPK, it.thisPK, it.orgName, it.innerName))
		}

		val subNullList = ediUploadRepository.selectCheckSubModelNull(targetUser.thisPK)
		val subUploadList = if (isEDIDate) {
			ediUploadRepository.selectCheckSubModelEDIDate2(targetUser.thisPK, year, month, ret.map { it.hospitalPK })
		} else {
			ediUploadRepository.selectCheckSubModelApplyDate2(targetUser.thisPK, year, month, ret.map { it.hospitalPK })
		}.filter { x -> !x.isNullValue() }
		val retSub = mutableListOf<EDIUploadCheckSubModel>()
		retSub.addAll(subUploadList)
		retSub.addAll(subNullList.filterNot { x ->
			subUploadList.any { y -> x.userPK == y.userPK && x.hospitalPK == y.hospitalPK && x.pharmaPK == y.pharmaPK
			}
		})
		val checkSubModelMap = retSub.groupBy { it.hospitalPK }
		ret.forEach { x ->
			val mapBuff = checkSubModelMap[x.hospitalPK]
			if (!mapBuff.isNullOrEmpty()) {
				x.subModel.addAll(mapBuff)
			}
		}

		return ret
	}
	fun getUserList(token: String, isMyChild: Boolean = true): List<UserDataModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.EdiChanger))) {
			throw AuthenticationEntryPointException()
		}

		if (isMyChild) {
			return userChildPKRepository.selectAllByMotherPK(tokenUser.thisPK).let {
				if (it.isNotEmpty()) {
					userDataRepository.findAllByThisPKIn(it).toMutableList()
				} else {
					mutableListOf()
				}
			}
		}
		return userDataRepository.findAll()
	}
}