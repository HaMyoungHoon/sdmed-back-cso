package sdmed.back.config

import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sdmed.back.advice.exception.AccessDeniedException
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.UserNotFoundException
import sdmed.back.config.security.JwtTokenProvider
import sdmed.back.model.common.user.UserRole.Companion.getFlag
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.common.user.UserStatus
import sdmed.back.model.sqlCSO.medicine.MedicineIngredientModel
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import sdmed.back.model.sqlCSO.user.UserDataModel
import sdmed.back.repository.sqlCSO.*

@Service
open class FServiceBase {
	@Autowired lateinit var jwtTokenProvider: JwtTokenProvider
	@Autowired lateinit var userDataRepository: IUserDataRepository
	@Autowired lateinit var userFileRepository: IUserFileRepository
	@Autowired lateinit var userChildPKRepository: IUserChildPKRepository
	@Autowired lateinit var requestRepository: IRequestRepository
	@Autowired lateinit var logRepository: ILogRepository
	@Autowired lateinit var fAmhohwa: FAmhohwa
	@Autowired lateinit var excelFileParser: FExcelFileParser
	@Autowired lateinit var entityManager: EntityManager

	fun getUserDataID(id: String) = userDataRepository.selectById(id) ?: throw UserNotFoundException()
	fun getUserDataPK(thisPK: String) = userDataRepository.selectByPK(thisPK) ?: throw UserNotFoundException()
	fun getUserDataByToken(token: String) = getUserDataID(jwtTokenProvider.getAllClaimsFromToken(token).subject)
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

	protected fun medicineMerge(mother: List<MedicineModel>, ingredient: List<MedicineIngredientModel>) {
		val ingredientMap = ingredient.associateBy { it.mainIngredientCode }
		mother.map { x ->
			ingredientMap[x.mainIngredientCode]?.let { y -> x.medicineIngredientModel = y }
		}
	}
}