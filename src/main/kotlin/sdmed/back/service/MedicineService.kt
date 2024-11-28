package sdmed.back.service

import jakarta.persistence.EntityManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.config.FConstants
import sdmed.back.config.FExcelFileParser
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.config.security.JwtTokenProvider
import sdmed.back.model.common.UserRole
import sdmed.back.model.common.UserRole.Companion.getFlag
import sdmed.back.model.common.UserRoles
import sdmed.back.model.common.UserStatus
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.MedicineModel
import sdmed.back.model.sqlCSO.MedicinePriceModel
import sdmed.back.model.sqlCSO.UserDataModel
import sdmed.back.repository.sqlCSO.ILogRepository
import sdmed.back.repository.sqlCSO.IMedicinePriceRepository
import sdmed.back.repository.sqlCSO.IMedicineRepository
import sdmed.back.repository.sqlCSO.IUserDataRepository
import java.util.*
import java.util.stream.Collectors.joining

@Service
class MedicineService {
	@Autowired lateinit var jwtTokenProvider: JwtTokenProvider
	@Autowired lateinit var logRepository: ILogRepository
	@Autowired lateinit var excelFileParser: FExcelFileParser
	@Autowired lateinit var userDataRepository: IUserDataRepository
	@Autowired lateinit var medicineRepository: IMedicineRepository
	@Autowired lateinit var medicinePriceRepository: IMedicinePriceRepository
	@Autowired lateinit var entityManager: EntityManager

	fun getMedicine(token: String): List<MedicineModel> {
		isValid(token)
		return medicineRepository.findAll()
	}
	fun getMedicine(token: String, page: Int, size: Int): Page<MedicineModel> {
		isValid(token)
		return medicineRepository.findAllByOrderByName(PageRequest.of(page, size))
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun medicineUpload(token: String, applyDate: Date, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token) ?: throw AuthenticationEntryPointException()
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.MedicineFileUploader))) {
			throw AuthenticationEntryPointException()
		}

		val excelModel = excelFileParser.medicineUploadExcelParse(tokenUser.id, applyDate, file)
		val already = medicineRepository.findAll()
		val priceModel = excelModel.flatMap { it.medicinePriceModel }
		val priceAlready = medicinePriceRepository.selectAllByRecentData()
		val newData: MutableList<MedicineModel> = mutableListOf()
		val newPriceData: MutableList<MedicinePriceModel> = mutableListOf()
		mergeMedicine(already, excelModel, newData)
		mergeMedicinePrice(priceAlready, priceModel, newPriceData)

		var retCount = 0
		newData.chunked(500).forEach { insertMedicineAll(it) }
		newPriceData.chunked(500).forEach { retCount += insertMedicinePriceAll(it) }
		if (retCount == 0) {
			return "count : $retCount"
		}

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add medicine count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}
	private fun mergeMedicine(lhsList: List<MedicineModel>, rhsList: MutableList<MedicineModel>, newData: MutableList<MedicineModel>) {
		val lhsMap = lhsList.associateBy { it.kdCode }
		for (rhs in rhsList) {
			val lhs = lhsMap[rhs.kdCode]
			if (lhs == null) {
				newData.add(rhs)
			}
		}
	}
	private fun mergeMedicinePrice(lhsList: List<MedicinePriceModel>, rhsList: List<MedicinePriceModel>, newData: MutableList<MedicinePriceModel>) {
		val lhsMap = lhsList.associateBy { it.medicineModel?.kdCode }
		for (rhs in rhsList) {
			val lhs = lhsMap[rhs.medicineModel?.kdCode]
			if (lhs != null) {
				if (lhs.maxPrice != rhs.maxPrice) {
					rhs.medicineModel = lhs.medicineModel
					newData.add(rhs)
				}
			} else {
				newData.add(rhs)
			}
		}
	}

	private fun insertMedicineAll(data: List<MedicineModel>): Int {
		val values: String = data.stream().map(this::renderSqlForInsertMedicineModel).collect(joining(","))
		val sqlString = "${FConstants.MODEL_DRUG_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun renderSqlForInsertMedicineModel(data: MedicineModel) = data.insertString()
	private fun insertMedicinePriceAll(data: List<MedicinePriceModel>): Int {
		val values = data.stream().map(this::renderSqlForInsertMedicinePriceModel).collect(joining(","))
		val sqlString = "${FConstants.MODEL_DRUG_PRICE_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun renderSqlForInsertMedicinePriceModel(data: MedicinePriceModel) = data.insertString()
	fun getUserData(id: String) = userDataRepository.selectById(id)
	fun getUserDataByToken(token: String) = userDataRepository.selectById(jwtTokenProvider.getAllClaimsFromToken(token).subject)
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