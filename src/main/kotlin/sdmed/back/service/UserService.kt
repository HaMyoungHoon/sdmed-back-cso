package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.*
import sdmed.back.config.FAmhohwa
import sdmed.back.config.FExcelFileParser
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.config.security.JwtTokenProvider
import sdmed.back.model.common.*
import sdmed.back.model.common.UserDept.Companion.getFlag
import sdmed.back.model.common.UserRole.Companion.getFlag
import sdmed.back.model.common.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.UserDataModel
import sdmed.back.model.sqlCSO.UserDataSubModel
import sdmed.back.repository.sqlCSO.ILogRepository
import sdmed.back.repository.sqlCSO.IUserDataRepository
import sdmed.back.repository.sqlCSO.IUserSubDataRepository
import java.sql.Timestamp
import java.util.*

@Service
class UserService {
	@Autowired lateinit var jwtTokenProvider: JwtTokenProvider
	@Autowired lateinit var userDataRepository: IUserDataRepository
	@Autowired lateinit var userSubDataRepository: IUserSubDataRepository
	@Autowired lateinit var logRepository: ILogRepository
	@Autowired lateinit var fAmhohwa: FAmhohwa
	@Autowired lateinit var excelFileParser: FExcelFileParser

	fun getAllUser(token: String): List<UserDataModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw UserNotFoundException()
		if (haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin))) {
			return userDataRepository.findAllByOrderByNameDesc().onEach { it.pw = "" }
		}

		return userDataRepository.selectWhereDeptOrderByNameAsc(UserDepts.of(UserDept.TaxPayer, UserDept.Personal).getFlag()).onEach { it.pw = "" }
	}
	fun getAllUser(token: String, page: Int, size: Int): Page<UserDataModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw UserNotFoundException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val pageable = PageRequest.of(page, size)
		return userDataRepository.findAllByOrderByNameDesc(pageable).onEach { it.pw = "" }
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun signIn(id: String, pw: String): String {
		val user = getUserData(id) ?: throw SignInFailedException()
		val encryptPW = fAmhohwa.encrypt(pw)
		if (user.pw != encryptPW) {
			throw SignInFailedException()
		}
		if (!isLive(user, false)) {
			throw SignInFailedException()
		}

		user.lastLoginDate = Timestamp(Date().time)
		userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(null, stackTrace[1].className, stackTrace[1].methodName, "$id login")
		logRepository.save(logModel)
		return jwtTokenProvider.createToken(user)
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun signUp(confirmPW: String, data: UserDataModel): UserDataModel {
		if (data.id.length < 3 || data.pw.length < 3) {
			throw SignUpFailedException()
		}
		if (data.pw.isEmpty()) {
			throw SignUpFailedException()
		}
		if (data.pw != confirmPW) {
			throw SignUpFailedException()
		}
		val existUser = userDataRepository.findById(data.id)
		if (existUser != null) {
			throw SignUpFailedException()
		}

		data.pw = fAmhohwa.encrypt(data.pw)
		if (data.id == "mhha") {
			data.role = UserRole.Admin.flag
			data.dept = UserDept.Admin.flag
			data.status = UserStatus.Live
		}
		data.subData = UserDataSubModel().apply {
			mother = data
		}
		userSubDataRepository.save(data.subData!!)
		val ret = userDataRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(null, stackTrace[1].className, stackTrace[1].methodName, "${data.id} ${data.name} signUp")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun passwordChange(token: String, id: String, changePW: String): UserDataModel {
		if (changePW.length < 4) {
			throw AuthenticationEntryPointException()
		}

		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw UserNotFoundException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.PasswordChanger))) {
			throw AuthenticationEntryPointException()
		}

		val user = getUserData(id) ?: throw UserNotFoundException()
		user.pw = fAmhohwa.encrypt(changePW)
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisIndex, stackTrace[1].className, stackTrace[1].methodName, "${user.id} password change")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun tokenRefresh(token: String): String {
		isValid(token)
		val user = getUserDataByToken(token) ?: throw UserNotFoundException()
		if (!isLive(user, false)) {
			throw AuthenticationEntryPointException()
		}
		user.lastLoginDate = Timestamp(Date().time)
		userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(null, stackTrace[1].className, stackTrace[1].methodName, "${user.id} tokenRefresh")
		logRepository.save(logModel)

		return jwtTokenProvider.createToken(user)
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userStatusModify(token: String, id: String, status: UserStatus): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.StatusChanger))) {
			throw AuthenticationEntryPointException()
		}

		val user = getUserData(id) ?: throw UserNotFoundException()
		user.status = status
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisIndex, stackTrace[1].className, stackTrace[1].methodName, "$id role : ${user.role}")
		logRepository.save(logModel)
		return ret
	}
	fun getUserStatusList() = UserStatus.entries
	fun getUserRoleList() = UserRole.entries.filterNot { it in listOf(UserRole.Admin, UserRole.CsoAdmin) }
	fun getUserDeptList() = UserDept.entries.filterNot { it in listOf(UserDept.Admin, UserDept.CsoAdmin) }
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userRoleModify(token: String, id: String, roleList: List<UserRole>): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.RoleChanger))) {
			throw AuthenticationEntryPointException()
		}
		var mask = UserRole.Admin.flag
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin))) {
			mask = mask or UserRole.CsoAdmin.flag
		}
		val user = userDataRepository.findById(id) ?: throw UserNotFoundException()
		user.role = roleList.fold(0) { acc, x -> acc or x.flag } and mask.inv()
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisIndex, stackTrace[1].className, stackTrace[1].methodName, "$id role : ${user.role}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userDeptModify(token: String, id: String, deptList: List<UserDept>): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.DeptChanger))) {
			throw AuthenticationEntryPointException()
		}
		var mask = UserDept.Admin.flag
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin))) {
			mask = mask or UserDept.CsoAdmin.flag
		}
		val user = userDataRepository.findById(id) ?: throw UserNotFoundException()
		user.dept = deptList.fold(0) { acc, x -> acc or x.flag } and mask.inv()
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisIndex, stackTrace[1].className, stackTrace[1].methodName, "$id dept : ${user.dept}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun addChild(token: String, motherID: String, childID: List<String>): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChildChanger))) {
			throw AuthenticationEntryPointException()
		}

		val mother = userDataRepository.findById(motherID)?.apply { init() }?.setChild() ?: throw UserNotFoundException()
		val motherChild = mother.children?.map { it.id } ?: arrayListOf()
		val childBuff = childID.toMutableList().distinct().toMutableList().apply {
			remove(motherID)
		}.filterNot { it in motherChild }
		val child = userDataRepository.findAllByIdIn(childBuff).onEach { it.init(); }
		if (child.isEmpty()) {
			return mother
		}
		mother.addChild(child)
		val ret = userDataRepository.save(mother)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisIndex, stackTrace[1].className, stackTrace[1].methodName, "${mother.id} add child : ${child.joinToString(", ") { it.id }}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun delChild(token: String, motherID: String, childID: List<String>): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChildChanger))) {
			throw AuthenticationEntryPointException()
		}

		val mother = userDataRepository.findById(motherID)?.apply { init() } ?: throw UserNotFoundException()
		val motherChild = mother.children?.map { it.id } ?: arrayListOf()
		val childBuff = childID.toMutableList().distinct().filter { it in motherChild }
		val child = userDataRepository.findAllByIdIn(childBuff).onEach { it.init(); it.userData = null }
		mother.children?.removeIf { it.id in childBuff }
		val ret = userDataRepository.save(mother)
		userDataRepository.saveAll(child)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisIndex, stackTrace[1].className, stackTrace[1].methodName, "${mother.id} delete child : ${childBuff.joinToString(",")}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userUpload(token: String, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserFileUploader))) {
			throw AuthenticationEntryPointException()
		}

		var index = 0
		val userDataModel = excelFileParser.userUploadExcelParse(tokenUser.id, file)
		val already: MutableList<UserDataModel> = mutableListOf()
		while (true) {
			if (userDataModel.count() > index + 500) {
				already.addAll(userDataRepository.findAllByIdIn(userDataModel.subList(index, index + 500).map { it.id }))
			} else {
				already.addAll(userDataRepository.findAllByIdIn(userDataModel.subList(index, userDataModel.count()).map { it.id }))
				break
			}
			index += 500
		}
		userDataModel.removeIf { x -> x.id in already.map { y -> y.id } }
		if (userDataModel.isEmpty()) {
			return "count : 0"
		}
		userDataModel.onEach { x ->
			x.pw = fAmhohwa.encrypt(x.pw)
			x.subData = UserDataSubModel().apply {
				mother = x
			}
		}
		index = 0
		var retCount = 0
		while (true) {
			if (userDataModel.count() > index + 500) {
				userSubDataRepository.saveAll(userDataModel.subList(index, index + 500).map { it.subData })
				retCount += userDataRepository.saveAll(userDataModel.subList(index, index + 500)).count()
			} else {
				userSubDataRepository.saveAll(userDataModel.subList(index, userDataModel.count()).map { it.subData })
				retCount += userDataRepository.saveAll(userDataModel.subList(index, userDataModel.count())).count()
				break
			}
			index += 500
		}
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisIndex, stackTrace[1].className, stackTrace[1].methodName, "add user count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}

	fun getUserData(id: String) = userDataRepository.findById(id)
	fun getUserDataByToken(token: String) = userDataRepository.findById(jwtTokenProvider.getAllClaimsFromToken(token).subject)
	fun isValid(token: String) {
		if (!jwtTokenProvider.validateToken(token)) {
			throw AuthenticationEntryPointException()
		}
	}
	fun isLive(user: UserDataModel, notLiveThrow: Boolean = true): Boolean {
		return if (user.status == UserStatus.Live) true
		else if (notLiveThrow) throw NotValidOperationException()
		else false
	}
	fun haveRole(user: UserDataModel, targetRole: UserRoles): Boolean {
		return targetRole.getFlag() and user.role != 0
	}
	fun haveRole(token: String, targetRole: UserRoles): Boolean {
		val user = UserDataModel().buildData(jwtTokenProvider.getAllClaimsFromToken(token))
		return haveRole(user, targetRole)
	}
}