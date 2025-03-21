package sdmed.back.service

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.HospitalExistException
import sdmed.back.advice.exception.HospitalNotFoundException
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.config.FConstants
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.hospital.HospitalModel
import java.util.*
import java.util.stream.Collectors

open class HospitalListService: HospitalService() {
	fun getList(token: String): List<HospitalModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)

		return hospitalRepository.selectAllByInVisibleOrderByCode()
	}
	fun getData(token: String, thisPK: String): HospitalModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)

		return hospitalRepository.findByThisPK(thisPK) ?: throw HospitalNotFoundException()
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun addHospitalData(token: String, data: HospitalModel): HospitalModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
		}
		isLive(tokenUser)
		if (data.code == FConstants.NEW_HOSPITAL_CODE) {
			throw NotValidOperationException()
		}

		val exist = hospitalRepository.findByCode(data.code)
		if (exist != null) {
			throw HospitalExistException()
		}
		data.thisPK = UUID.randomUUID().toString()

		val ret = hospitalRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add hospital : ${data.thisPK}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun hospitalUpload(token: String, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
		}

		val excelModel = excelFileParser.hospitalUploadExcelParse(tokenUser.id, file)
		val already: MutableList<HospitalModel> = mutableListOf()
		excelModel.chunked(500).forEach { x -> already.addAll(hospitalRepository.findAllByCodeIn(x.map { y -> y.code })) }
		var retCount = 0
		val saveList = excelModel.toMutableList()
		saveList.removeIf { x -> x.code in already.map { y -> y.code } }
		saveList.chunked(500).forEach { x -> retCount += insertAll(x) }
		if (already.isNotEmpty()) {
			val buffMap = excelModel.associateBy { it.code }
			if (already.isNotEmpty()) {
				already.forEach { x ->
					val buff = buffMap[x.code]
					if (buff != null) {
						x.safeCopy(buff)
					}
				}
			}
		}
		already.chunked(500).forEach { x -> retCount += updateAll(x) }
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add hospital count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun hospitalDataModify(token: String, data: HospitalModel): HospitalModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
		}
		if (data.code == FConstants.NEW_HOSPITAL_CODE) {
			throw NotValidOperationException()
		}

		hospitalRepository.findByThisPK(data.thisPK) ?: throw HospitalNotFoundException()
		val ret = hospitalRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "hospital modify")
		logRepository.save(logModel)
		return ret
	}

	private fun insertAll(data: List<HospitalModel>): Int {
		if (data.isEmpty()) {
			return 0
		}

		val values: String = data.stream().map{it.insertString()}.collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_HOS_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun updateAll(data: List<HospitalModel>): Int {
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
}