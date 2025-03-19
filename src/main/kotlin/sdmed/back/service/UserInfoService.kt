package sdmed.back.service

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.UserNotFoundException
import sdmed.back.config.FConstants
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserDept
import sdmed.back.model.common.user.UserDept.Companion.getFlag
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.user.UserChildPKModel
import sdmed.back.model.sqlCSO.user.UserDataModel
import java.util.stream.Collectors

open class UserInfoService: UserService() {
	fun getList(token: String): List<UserDataModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		val ret = if (haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			userDataRepository.findAllByOrderByNameDesc()
//			return userDataRepository.findAllByOrderByNameDesc().toMutableList().run {
//				filter { it.thisPK != tokenUser.thisPK }
//			}
		} else {
			userDataRepository.selectWhereDeptOrderByNameAsc(UserRoles.of(UserDept.TaxPayer, UserDept.Personal).getFlag()).run {
				filter { it.thisPK != tokenUser.thisPK }
			}
		}

		val userTrainingList = userTrainingRepository.selectAllByRecentDataUserPKIn(ret.map { it.thisPK })
		val userTrainingMap = userTrainingList.associateBy { it.userPK }
		for (user in ret) {
			userTrainingMap[user.thisPK]?.let {
				user.trainingList.add(it)
			}
		}

		return ret
	}
	fun getData(token: String, userPK: String, childView: Boolean = false, relationView: Boolean = false, pharmaOwnMedicineView: Boolean = false, relationMedicineView: Boolean = true, trainingModelView: Boolean = true): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		return getUserDataByPK(userPK, childView, relationView, pharmaOwnMedicineView, relationMedicineView, trainingModelView)
	}
	fun getListChildAble(token: String, thisPK: String): List<UserDataModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		return userDataRepository.selectAbleChild(thisPK)
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun userDataModify(token: String, userData: UserDataModel): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val buff = userDataRepository.findByThisPK(userData.thisPK) ?: throw UserNotFoundException()
		if (buff.id == "mhha") {
			return buff
		}
		buff.safeCopy(userData)

		var mask = UserRole.Admin.flag
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin))) {
			mask = mask or UserRole.CsoAdmin.flag
		}

		buff.role = buff.role and mask.inv()
		val ret = userDataRepository.save(buff)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${buff.id} modify : $buff")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun userUpload(token: String, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val excelModel = excelFileParser.userUploadExcelParse(tokenUser.id, file).distinctBy { x -> Pair(x.id, x.companyInnerName)  }
		val already: MutableList<UserDataModel> = mutableListOf()
		excelModel.chunked(500).forEach { x -> already.addAll(userDataRepository.findAllByIdIn(x.map { y -> y.id })) }
		var retCount = 0
		val saveList = excelModel.toMutableList()
		saveList.removeIf { x -> x.id in already.map { y -> y.id } }
		saveList.onEach { x ->
			x.pw = fAmhohwa.encrypt(x.pw)
		}
		saveList.chunked(500).forEach { x -> retCount += insertAll(x) }
		if (already.isNotEmpty()) {
			val buffMap = excelModel.associateBy { it.id }
			already.forEach { x ->
				val buff = buffMap[x.id]
				if (buff != null) {
					x.safeCopy(buff)
				}
			}
		}
		already.chunked(500).forEach { x -> retCount += updateAll(x) }
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add user count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun childModify(token: String, motherPK: String, childPK: List<String>): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val mother = getUserDataByPK(motherPK)
		val existChild = userDataRepository.findAllByThisPKIn(childPK).map { it.thisPK }.toMutableList()
		deleteMothersChild(motherPK)
		if (existChild.isEmpty()) {
			return "count 0"
		}
		val motherPKInString = "${existChild.joinToString(",") { "'${it}'" }}"
		userChildPKRepository.selectAllByMotherPKInOnlyOne(motherPKInString).let { x ->
			existChild.removeIf { y -> y in x }
		}
		val userChildPK = existChild.map { x -> UserChildPKModel().apply {
			this.motherPK = motherPK
			this.childPK = x
		} }
		if (userChildPK.isEmpty()) {
			return "count 0"
		}

		var retCount = 0
		userChildPK.chunked(500).forEach { retCount += insertMothersChild(it) }
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${mother.id} child modify count : $retCount")
		logRepository.save(logModel)
		return "count $retCount"
	}
	private fun insertAll(data: List<UserDataModel>): Int {
		if (data.isEmpty()) {
			return 0
		}
		val values: String = data.stream().map{it.insertString()}.collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_USER_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun updateAll(data: List<UserDataModel>): Int {
		if (data.isEmpty()) {
			return 0
		}

		data.forEach { x ->
			entityManager.merge(x)
		}
		entityManager.flush()
		entityManager.clear()
		return data.size
	}
	fun deleteMothersChild(motherPK: String) {
		val sqlString = "${FConstants.MODEL_USER_CHILD_DELETE_BY_MOTHER_PK} '$motherPK'"
		entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
	}
	fun insertMothersChild(data: List<UserChildPKModel>): Int {
		val values = data.stream().map { it.insertString() }.collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_USER_CHILD_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
}