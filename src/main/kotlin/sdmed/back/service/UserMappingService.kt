package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.config.FConstants
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.common.user.UserStatus
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.user.HosPharmaMedicinePairModel
import sdmed.back.model.sqlCSO.user.UserDataModel
import sdmed.back.model.sqlCSO.user.UserHosPharmaMedicinePairModel
import java.util.stream.Collectors

class UserMappingService: UserService() {
	@Autowired lateinit var hospitalService: HospitalService
	@Autowired lateinit var pharmaService: PharmaService

	fun getList(token: String, exceptMe: Boolean = true): List<UserDataModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			return userDataRepository.selectByRoleAndStatusOrderByNameAsc(UserRole.BusinessMan.flag, UserStatus.Live.index).toMutableList().filter { it.thisPK != tokenUser.thisPK }
		}

		return arrayListOf()
	}

	fun getHospitalAllSearch(token: String, searchString: String, pharmaOwnMedicineView: Boolean = false) =
		hospitalService.getHospitalAllSearch(token, searchString, pharmaOwnMedicineView)
	fun getPharmaAllSearch(token: String, searchString: String, isSearchTypeCode: Boolean = false) =
		pharmaService.getPharmaAllSearch(token, searchString, isSearchTypeCode)
	fun getPharmaData(token: String, pharmaPK: String, pharmaOwnMedicineView: Boolean = false) =
		pharmaService.getPharmaData(token, pharmaPK, pharmaOwnMedicineView)

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun userRelationModify(token: String, userPK: String, hosPharmaMedicinePairModel: List<HosPharmaMedicinePairModel>): UserDataModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			throw AuthenticationEntryPointException()
		}

		// 약품 없이도 넣어달라고 했는데
		// post 하는 쪾에서 상당히 문제가 됨 미사용 약품이나 잘못된 코드로 약품을 넣어도 들어가게끔 되어버림 왜냐면 진짜 그 약품 있는 거임? 체크 하고
		// 없으면 삭제하고 있으면 가격이력 검색하는데
		// 삭제 하는 과정에서 제약사 데이터도 날아감
		val userData = getUserDataByPK(userPK)
		val existHos = hospitalRepository.findAllByThisPKIn(hosPharmaMedicinePairModel.map { it.hosPK })
		var realPair = hosPharmaMedicinePairModel.filter { x -> x.hosPK in existHos.map { y -> y.thisPK } }
		val existPharma = pharmaRepository.findAllByThisPKIn(realPair.map { it.pharmaPK })
		realPair = realPair.filter { x -> x.pharmaPK in existPharma.map { y -> y.thisPK } }
//		val emptyMedicinePair = realPair.filter { x -> x.medicinePK == "" }
		val existMedicine = medicineRepository.findAllByThisPKIn(realPair.map { it.medicinePK })
		realPair = realPair.filter { x -> x.medicinePK in existMedicine.map { y -> y.thisPK } }

		deleteRelationByUserPK(userData.thisPK)
		if (realPair.isEmpty()) {
//		if (realPair.isEmpty() && emptyMedicinePair.isEmpty()) {
			return userData
		}
		insertRelation(realPair.map { x -> UserHosPharmaMedicinePairModel().apply {
			this.userPK = userData.thisPK
			this.hosPK = x.hosPK
			this.pharmaPK = x.pharmaPK
			this.medicinePK = x.medicinePK
		}})
//		if (realPair.isNotEmpty()) {
//			insertRelation(realPair.map { x -> UserHosPharmaMedicinePairModel().apply {
//				this.userPK = userData.thisPK
//				this.hosPK = x.hosPK
//				this.pharmaPK = x.pharmaPK
//				this.medicinePK = x.medicinePK
//			}})
//		}
//		if (emptyMedicinePair.isNotEmpty()) {
//			insertRelation(emptyMedicinePair.map { x -> UserHosPharmaMedicinePairModel().apply {
//				this.userPK = userData.thisPK
//				this.hosPK = x.hosPK
//				this.pharmaPK = x.pharmaPK
//				this.medicinePK = x.medicinePK
//			}})
//		}

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
		val values = data.stream().map{it.insertString()}.collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_USER_RELATIONS_INSERT_INTO}$values"
		entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
	}
}