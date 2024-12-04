package sdmed.back.service

import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.*
import sdmed.back.config.FAmhohwa
import sdmed.back.config.FConstants
import sdmed.back.config.FExcelFileParser
import sdmed.back.config.FExtensions
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.config.security.JwtTokenProvider
import sdmed.back.model.common.*
import sdmed.back.model.common.UserDept.Companion.getFlag
import sdmed.back.model.common.UserRole.Companion.getFlag
import sdmed.back.model.common.UserRoles
import sdmed.back.model.sqlCSO.*
import sdmed.back.repository.sqlCSO.*
import java.sql.Timestamp
import java.util.*
import java.util.stream.Collectors

@Service
class UserService {
	@Autowired lateinit var jwtTokenProvider: JwtTokenProvider
	@Autowired lateinit var userDataRepository: IUserDataRepository
	@Autowired lateinit var hospitalRepository: IHospitalRepository
	@Autowired lateinit var pharmaRepository: IPharmaRepository
	@Autowired lateinit var medicineRepository: IMedicineRepository
	@Autowired lateinit var medicineSubRepository: IMedicineSubRepository
	@Autowired lateinit var medicineIngredientRepository: IMedicineIngredientRepository
	@Autowired lateinit var userRelationRepository: IUserRelationRepository
	@Autowired lateinit var pharmaMedicineRelationRepository: IPharmaMedicineRelationRepository
	@Autowired lateinit var logRepository: ILogRepository
	@Autowired lateinit var fAmhohwa: FAmhohwa
	@Autowired lateinit var excelFileParser: FExcelFileParser
	@Autowired lateinit var entityManager: EntityManager

	fun getAllUser(token: String, exceptMe: Boolean = true): List<UserDataModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			return userDataRepository.findAllByOrderByNameDesc().onEach { it.lazyHide() }.toMutableList().apply {
				if (exceptMe) {
					filter { it.thisPK != tokenUser.thisPK }
				}
			}
		}

		return userDataRepository.selectWhereDeptOrderByNameAsc(UserDepts.of(UserDept.TaxPayer, UserDept.Personal).getFlag()).filter { it.thisPK != tokenUser.thisPK }
	}
	fun getAllUserBusiness(token: String, exceptMe: Boolean = true): List<UserDataModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			return userDataRepository.selectByRoleAndStatusOrderByNameAsc(UserRole.BusinessMan.flag, UserStatus.Live.index).onEach { it.lazyHide() }.toMutableList().filter { it.thisPK != tokenUser.thisPK }
		}

		return userDataRepository.selectWhereDeptOrderByNameAsc(UserDepts.of(UserDept.TaxPayer, UserDept.Personal).getFlag()).filter { it.thisPK != tokenUser.thisPK }
	}
	fun getAllUser(token: String, page: Int, size: Int): Page<UserDataModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val pageable = PageRequest.of(page, size)
		return userDataRepository.findAllByOrderByNameDesc(pageable).onEach { it.lazyHide() }
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun signIn(id: String, pw: String): String {
		val user = getUserDataByID(id)
		val encryptPW = fAmhohwa.encrypt(pw)
		if (user.pw != encryptPW) {
			throw ConfirmPasswordUnMatchException()
		}
		isLive(user)

		user.lastLoginDate = Timestamp(Date().time)
		userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(null, stackTrace[1].className, stackTrace[1].methodName, "$id login")
		logRepository.save(logModel)
		return jwtTokenProvider.createToken(user)
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun signUp(confirmPW: String, data: UserDataModel): UserDataModel {
		if (data.id.length < 3) {
			throw SignUpIDConditionException()
		}
		if (data.pw.length < 3) {
			throw SignUpFailedException()
		}
		if (data.pw != confirmPW) {
			throw SignUpPWConditionException()
		}
		val existUser = userDataRepository.selectById(data.id)
		if (existUser != null) {
			throw SignUpFailedException()
		}

		data.pw = fAmhohwa.encrypt(data.pw)
		if (data.id == "mhha") {
			data.role = UserRole.Admin.flag
			data.status = UserStatus.Live
		}
		val ret = userDataRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(null, stackTrace[1].className, stackTrace[1].methodName, "${data.id} ${data.name} signUp")
		logRepository.save(logModel)
		return ret
	}
	fun userDataSetFamily(userData: UserDataModel) {
		val parent = userDataRepository.selectMyParent(userData.thisPK)
		userData.setChild(parent)
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun passwordChangeByID(token: String, id: String, changePW: String): UserDataModel {
		if (changePW.length < 4) {
			throw AuthenticationEntryPointException()
		}

		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val user = getUserDataByID(id)
		user.pw = fAmhohwa.encrypt(changePW)
		userDataSetFamily(user)
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${user.id} password change")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun passwordChangeByPK(token: String, userPK: String, changePW: String): UserDataModel {
		if (changePW.length < 4) {
			throw AuthenticationEntryPointException()
		}

		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val user = getUserDataByPK(userPK)
		user.pw = fAmhohwa.encrypt(changePW)
		userDataSetFamily(user)
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${user.id} password change")
		logRepository.save(logModel)
		return ret
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun tokenRefresh(token: String): String {
		isValid(token)
		val user = getUserDataByToken(token)
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
	fun userStatusModifyByID(token: String, id: String, status: UserStatus): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val user = getUserDataByID(id)
		user.status = status
		userDataSetFamily(user)
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "$id role : ${user.role}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userStatusModifyByPK(token: String, userPK: String, status: UserStatus): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val user = getUserDataByPK(userPK)
		if (user.id == "mhha") {
			throw AuthenticationEntryPointException()
		}

		user.status = status
		userDataSetFamily(user)
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${user.id} role : ${user.role}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userNameMailPhoneModifyByPK(token: String, userPK: String, name: String, mail: String, phoneNumber: String): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val user = getUserDataByPK(userPK)
		if (user.id == "mhha") {
			throw AuthenticationEntryPointException()
		}
		user.name = FExtensions.regexSpecialCharRemove(name)
		user.mail = FExtensions.escapeString(mail)
		user.phoneNumber = FExtensions.escapeString(phoneNumber)
		userDataSetFamily(user)
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${user.id} role : ${user.role}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userRoleDeptStatusModifyByPK(token: String, userPK: String, data: RoleDeptStatusModel): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val user = getUserDataByPK(userPK)
		if (user.id == "mhha") {
			throw AuthenticationEntryPointException()
		}
		var mask = UserRole.Admin.flag
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin))) {
			mask = mask or UserRole.CsoAdmin.flag
		}

		user.status = data.status
		user.role = data.roles.fold(0) { acc, x -> acc or x.flag } and mask.inv()
		user.dept = data.depts.fold(0) { acc, x -> acc or x.flag }
		userDataSetFamily(user)
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${user.id} role : ${user.role}")
		logRepository.save(logModel)
		return ret
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userDataModify(token: String, userData: UserDataModel): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		userDataSetFamily(userData)
		val ret = userDataRepository.save(userData)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${userData.id} modify : $userData")
		logRepository.save(logModel)
		return ret
	}
	fun getUserStatusList() = UserStatus.entries
	fun getUserRoleList() = UserRole.entries.filterNot { it in listOf(UserRole.Admin, UserRole.CsoAdmin) }
	fun getUserDeptList() = UserDept.entries
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userRoleModifyByID(token: String, id: String, roleList: List<UserRole>): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}
		var mask = UserRole.Admin.flag
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin))) {
			mask = mask or UserRole.CsoAdmin.flag
		}
		val user = getUserDataByID(id)
		user.role = roleList.fold(0) { acc, x -> acc or x.flag } and mask.inv()
		userDataSetFamily(user)
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "$id role : ${user.role}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userRoleModifyByPK(token: String, userPK: String, roleList: List<UserRole>): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}
		var mask = UserRole.Admin.flag
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin))) {
			mask = mask or UserRole.CsoAdmin.flag
		}
		val user = userDataRepository.findByThisPK(userPK) ?: throw UserNotFoundException()
		if (user.id == "mhha") {
			throw AuthenticationEntryPointException()
		}

		user.role = roleList.fold(0) { acc, x -> acc or x.flag } and mask.inv()
		userDataSetFamily(user)
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${user.id} role : ${user.role}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userDeptModifyByID(token: String, id: String, deptList: List<UserDept>): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}
		val user = getUserDataByID(id)
		user.dept = deptList.fold(0) { acc, x -> acc or x.flag }
		userDataSetFamily(user)
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "$id dept : ${user.dept}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userDeptModifyByPK(token: String, userPK: String, deptList: List<UserDept>): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}
		val user = getUserDataByPK(userPK)
		if (user.id == "mhha") {
			throw AuthenticationEntryPointException()
		}
		user.dept = deptList.fold(0) { acc, x -> acc or x.flag }
		userDataSetFamily(user)
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${user.id} dept : ${user.dept}")
		logRepository.save(logModel)
		return ret
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun addChild(token: String, motherID: String, childID: List<String>): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val mother = getUserDataByID(motherID)
		val motherChild = mother.children.map { it.id }
		val childBuff = childID.toMutableList().distinct().toMutableList().apply {
			remove(motherID)
		}.filterNot { it in motherChild }
		val child = userDataRepository.findAllByIdIn(childBuff)
		if (child.isEmpty()) {
			return mother
		}
		mother.addChild(child)
		val ret = userDataRepository.save(mother)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${mother.id} add child : ${child.joinToString(", ") { it.id }}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun delChild(token: String, motherID: String, childID: List<String>): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val mother = getUserDataByID(motherID)
		val motherChild = mother.children.map { it.id }
		val childBuff = childID.toMutableList().distinct().filter { it in motherChild }
		val child = userDataRepository.findAllByIdIn(childBuff)
		mother.children.removeIf { it.id in childBuff }
		val ret = userDataRepository.save(mother)
		userDataRepository.saveAll(child)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${mother.id} delete child : ${childBuff.joinToString(",")}")
		logRepository.save(logModel)
		return ret
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userUpload(token: String, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
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
		}
		index = 0
		var retCount = 0
		while (true) {
			if (userDataModel.count() > index + 500) {
				retCount += insertAll(userDataModel.subList(index, index + 500))
			} else {
				retCount += insertAll(userDataModel.subList(index, userDataModel.count()))
				break
			}
			index += 500
		}
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add user count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}
	private fun insertAll(data: List<UserDataModel>): Int {
		val values: String = data.stream().map(this::renderSqlUserPharmaModel).collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_USER_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun renderSqlUserPharmaModel(data: UserDataModel) = data.insertString()

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userRelationModify(token: String, userPK: String, hosPharmaMedicinePairModel: List<HosPharmaMedicinePairModel>): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		val userData = getUserDataByPK(userPK)
		val existHos = hospitalRepository.findAllByThisPKIn(hosPharmaMedicinePairModel.map { it.hosPK })
		var realPair = hosPharmaMedicinePairModel.filter { x -> x.hosPK in existHos.map { y -> y.thisPK } }
		val existPharma = pharmaRepository.findAllByThisPKIn(realPair.map { it.pharmaPK })
		realPair = realPair.filter { x -> x.pharmaPK in existPharma.map { y -> y.thisPK } }
		val existMedicine = medicineRepository.findAllByThisPKIn(realPair.map { it.medicinePK })
		realPair = realPair.filter { x -> x.medicinePK in existMedicine.map { y -> y.thisPK } }

		deleteRelationByUserPK(userData.thisPK)
		if (realPair.isEmpty()) {
			return userData
		}
		insertRelation(realPair.map { x -> UserHosPharmaMedicinePairModel().apply {
			this.userPK = userData.thisPK
			this.hosPK = x.hosPK
			this.pharmaPK = x.pharmaPK
			this.medicinePK = x.medicinePK
		}})

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${userData.id} hos-pharma-medicine relation change")
		logRepository.save(logModel)
		return getUserDataWithRelationByPK(userPK)
	}
	fun deleteRelationByUserPK(userPK: String) {
		val sqlString = "${FConstants.MODEL_USER_RELATIONS_DELETE_WHERE_USER_PK} '$userPK'"
		entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
	}
	fun insertRelation(data: List<UserHosPharmaMedicinePairModel>) {
		val values = data.stream().map(this::renderSqlInsertRelation).collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_USER_RELATIONS_INSERT_INTO}$values"
		entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
	}
	fun renderSqlInsertRelation(data: UserHosPharmaMedicinePairModel) = data.insertString()

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun getUserDataByID(id: String, childView: Boolean = false, relationView: Boolean = false, pharmaOwnMedicineView: Boolean = false): UserDataModel {
		val ret = userDataRepository.selectById(id) ?: throw UserNotFoundException()
		if (childView) {
			ret.children = userDataRepository.findAllByUserData(ret).toMutableList().onEach { it.lazyHide() }
		}
		ret.lazyHide()
		if (relationView) {
			ret.hosList = mergeRel(ret.thisPK, pharmaOwnMedicineView)
		}

		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun getUserDataByPK(thisPK: String, childView: Boolean = false, relationView: Boolean = false, pharmaOwnMedicineView: Boolean = false): UserDataModel {
		val ret = userDataRepository.findByThisPK(thisPK) ?: throw UserNotFoundException()
		if (childView) {
			ret.children = userDataRepository.findAllByUserData(ret).toMutableList().onEach { it.lazyHide() }
		}
		ret.lazyHide()
		if (relationView) {
			ret.hosList = mergeRel(ret.thisPK, pharmaOwnMedicineView)
		}

		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun getUserDataWithRelationByPK(thisPK: String) = getUserDataByPK(thisPK).apply { hosList = mergeRel(thisPK) }
	fun mergeRel(userPK: String, pharmaOwnMedicineView: Boolean = false): MutableList<HospitalModel> {
		var ret: MutableList<HospitalModel> = mutableListOf()
		val userRelationModel = userRelationRepository.findAllByUserPK(userPK)
		val hosMap = hospitalRepository.findAllByThisPKIn(userRelationModel.map { it.hosPK }).onEach { it.lazyHide() }.associateBy { it.thisPK }
		val pharmaMap = pharmaRepository.findAllByThisPKIn(userRelationModel.map { it.pharmaPK }).onEach {
			if (pharmaOwnMedicineView) {
				val relation = pharmaMedicineRelationRepository.findAllByPharmaPK(it.thisPK)
				it.medicineList = medicineRepository.findAllByThisPKIn(relation.map { x -> x.medicinePK }).toMutableList()
			}
		}.associateBy { it.thisPK }
		val medicineBuff = medicineRepository.findAllByThisPKIn(userRelationModel.map { it.medicinePK })
		val sub = medicineSubRepository.findALlByCodeInOrderByCode(medicineBuff.map { it.code })
		val ingredient = medicineIngredientRepository.findAllByMainIngredientCodeIn(medicineBuff.map { it.mainIngredientCode })
		medicineMerge(medicineBuff, sub, ingredient)
		val medicineMap = medicineBuff.associateBy { it.thisPK }
		for (rel in userRelationModel) {
			val hos = hosMap[rel.hosPK] ?: continue
			val pharma = pharmaMap[rel.pharmaPK]
			val medicine = medicineMap[rel.medicinePK]
			if (pharma != null) {
				if (medicine != null) {
					pharma.relationMedicineList.add(medicine)
				}
				hos.pharmaList.add(pharma)
			}
			ret.add(hos)
		}
		ret = ret.distinct().toMutableList()
		ret.onEach { x ->
			x.pharmaList = x.pharmaList.distinct().toMutableList()
			x.pharmaList.onEach { y ->
				y.relationMedicineList = y.relationMedicineList.distinct().toMutableList()
			}
		}

		return ret
	}

	fun medicineMerge(mother: List<MedicineModel>, sub: List<MedicineSubModel>, ingredient: List<MedicineIngredientModel>) {
		val subMap = sub.associateBy { it.code }
		mother.map { x ->
			subMap[x.code]?.let { y -> x.medicineSubModel = y }
		}
		val ingredientMap = ingredient.associateBy { it.mainIngredientCode }
		mother.map { x ->
			ingredientMap[x.mainIngredientCode]?.let { y -> x.medicineIngredientModel = y }
		}
	}

	fun getUserDataByToken(token: String) = getUserDataByID(jwtTokenProvider.getAllClaimsFromToken(token).subject)
	fun isValid(token: String) {
		if (!jwtTokenProvider.validateToken(token)) {
			throw AuthenticationEntryPointException()
		}
	}
	fun isLive(user: UserDataModel, notLiveThrow: Boolean = true): Boolean {
		return if (user.status == UserStatus.Live) true
		else if (notLiveThrow) throw AccessDeniedException()
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