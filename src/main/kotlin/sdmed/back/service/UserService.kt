package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.*
import sdmed.back.config.FExtensions
import sdmed.back.config.FServiceBase
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.RequestType
import sdmed.back.model.common.ResponseType
import sdmed.back.model.common.user.*
import sdmed.back.model.sqlCSO.*
import sdmed.back.model.sqlCSO.hospital.HospitalModel
import sdmed.back.model.sqlCSO.request.RequestModel
import sdmed.back.model.sqlCSO.user.UserDataModel
import sdmed.back.model.sqlCSO.user.UserFileModel
import sdmed.back.model.sqlCSO.user.UserFileType
import sdmed.back.repository.sqlCSO.*
import java.sql.Timestamp
import java.util.*

class UserService: FServiceBase() {
	@Autowired lateinit var hospitalRepository: IHospitalRepository
	@Autowired lateinit var pharmaRepository: IPharmaRepository
	@Autowired lateinit var medicineRepository: IMedicineRepository
	@Autowired lateinit var medicineSubRepository: IMedicineSubRepository
	@Autowired lateinit var medicineIngredientRepository: IMedicineIngredientRepository
	@Autowired lateinit var userRelationRepository: IUserRelationRepository
	@Autowired lateinit var pharmaMedicineRelationRepository: IPharmaMedicineRelationRepository

	fun getUserData(token: String, userPK: String, childView: Boolean = false, relationView: Boolean = false, pharmaOwnMedicineView: Boolean = false): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}
		if (userPK.isBlank()) {
			return getUserDataByPK(tokenUser.thisPK, childView, relationView, pharmaOwnMedicineView)
		}
		return getUserDataByPK(userPK, childView, relationView, pharmaOwnMedicineView)
	}
	fun getUserDataByID(id: String, childView: Boolean = false, relationView: Boolean = false, pharmaOwnMedicineView: Boolean = false): UserDataModel {
		val ret = userDataRepository.selectById(id) ?: throw UserNotFoundException()
		if (childView) {
			userChildPKRepository.selectAllByMotherPK(ret.thisPK).let {
				if (it.isNotEmpty()) {
					ret.children = userDataRepository.findAllByThisPKIn(it).toMutableList()
				}
			}
		}
		if (relationView) {
			ret.hosList = mergeRel(ret.thisPK, pharmaOwnMedicineView)
		}

		return ret
	}
	fun getUserDataByPK(thisPK: String, childView: Boolean = false, relationView: Boolean = false, pharmaOwnMedicineView: Boolean = false): UserDataModel {
		val ret = userDataRepository.findByThisPK(thisPK) ?: throw UserNotFoundException()
		ret.fileList = userFileRepository.findAllByUserPK(ret.thisPK).toMutableList()
		if (childView) {
			userChildPKRepository.selectAllByMotherPK(thisPK).let {
				if (it.isNotEmpty()) {
					ret.children = userDataRepository.findAllByThisPKIn(it).toMutableList()
				}
			}
		}
		if (relationView) {
			ret.hosList = mergeRel(ret.thisPK, pharmaOwnMedicineView)
		}

		return ret
	}
	fun getUserDataWithRelationByPK(thisPK: String) = getUserDataByPK(thisPK).apply { hosList = mergeRel(thisPK) }

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
	fun newUser(confirmPW: String, data: UserDataModel): UserDataModel {
		data.id = data.id.trim()
		data.pw = data.pw.trim()
		if (FExtensions.regexIdCheck(data.id) != true) {
			throw SignUpIDConditionException()
		}
		if (FExtensions.regexPasswordCheck(data.pw) != true) {
			throw SignUpFailedException()
		}
		if (data.pw != confirmPW) {
			throw SignUpPWConditionException()
		}
		val existUser = userDataRepository.selectById(data.id)
		if (existUser != null) {
			throw SignUpIDExistException()
		}

		data.thisPK = UUID.randomUUID().toString()
		data.pw = fAmhohwa.encrypt(data.pw)
		if (data.id == "mhha") {
			data.role = UserRole.Admin.flag
			data.status = UserStatus.Live
		}
		val ret = userDataRepository.save(data)
		requestRepository.save(RequestModel().apply {
			requestUserPK = data.thisPK
			requestUserName = data.id
			requestItemPK = data.thisPK
			requestType = RequestType.SignUp
			if (data.id == "mhha") {
				responseType = ResponseType.OK
			}
		})
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(null, stackTrace[1].className, stackTrace[1].methodName, "${data.id} ${data.name} signUp")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun newUser(token: String, confirmPW: String, data: UserDataModel): UserDataModel {
		data.id = data.id.trim()
		data.pw = data.pw.trim()
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		if (FExtensions.regexIdCheck(data.id) != true) {
			throw SignUpIDConditionException()
		}
		if (FExtensions.regexPasswordCheck(data.pw) != true) {
			throw SignUpFailedException()
		}
		if (data.pw != confirmPW) {
			throw SignUpPWConditionException()
		}
		val existUser = userDataRepository.selectById(data.id)
		if (existUser != null) {
			throw SignUpIDExistException()
		}

		data.thisPK = UUID.randomUUID().toString()
		data.pw = fAmhohwa.encrypt(data.pw)
		var mask = UserRole.Admin.flag
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin))) {
			mask = mask or UserRole.CsoAdmin.flag
		}
		data.role = data.role and mask.inv()
		if (data.id == "mhha") {
			data.role = UserRole.Admin.flag
			data.status = UserStatus.Live
		}
		val ret = userDataRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${data.id} ${data.name} new user")
		logRepository.save(logModel)
		return ret
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun passwordChangeByID(token: String, id: String, changePW: String): UserDataModel {
		if (FExtensions.regexPasswordCheck(changePW) != true) {
			throw AuthenticationEntryPointException()
		}

		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val pwBuff = changePW.trim()
		val user = getUserDataByID(id)
		user.pw = fAmhohwa.encrypt(pwBuff)
		val ret = userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${user.id} password change")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun passwordChangeByPK(token: String, userPK: String, changePW: String): UserDataModel {
		if (FExtensions.regexPasswordCheck(changePW) != true) {
			throw AuthenticationEntryPointException()
		}

		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val pwBuff = changePW.trim()
		val user = getUserDataByPK(userPK)
		user.pw = fAmhohwa.encrypt(pwBuff)
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
	fun userFileUrlModify(token: String, userPK: String, blobModel: BlobUploadModel, userFileType: UserFileType): UserFileModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}
		val user = getUserDataByPK(userPK)
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
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun passwordInit(token: String, userPK: String): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.UserChanger))) {
			throw AuthenticationEntryPointException()
		}

		val password = "123456789a"
		val user = getUserDataByPK(userPK)
		user.pw = fAmhohwa.encrypt(password)
		userDataRepository.save(user)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "password init : ${user.id}")
		logRepository.save(logModel)
		return password
	}

	fun mergeRel(userPK: String, pharmaOwnMedicineView: Boolean = false): MutableList<HospitalModel> {
		var ret: MutableList<HospitalModel> = mutableListOf()
		val userRelationModel = userRelationRepository.findAllByUserPK(userPK)
		val hosMap = hospitalRepository.findAllByThisPKIn(userRelationModel.map { it.hosPK }).associateBy { it.thisPK }
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
}