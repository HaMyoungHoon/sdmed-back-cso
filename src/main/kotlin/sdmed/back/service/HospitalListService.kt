package sdmed.back.service

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.HospitalExistException
import sdmed.back.advice.exception.HospitalNotFoundException
import sdmed.back.config.FConstants
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.hospital.HospitalModel
import java.util.*
import java.util.stream.Collectors

class HospitalListService: HospitalService() {
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
	fun addHospitalData(token: String, data: HospitalModel): HospitalModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
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
	fun hospitalUpload(token: String, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
		}

		val hospitalDataModel = excelFileParser.hospitalUploadExcelParse(tokenUser.id, file)
		var index = 0
		val already: MutableList<HospitalModel> = mutableListOf()
		while (true) {
			if (hospitalDataModel.count() > index + 500) {
				already.addAll(hospitalRepository.findAllByCodeIn(hospitalDataModel.subList(index, index + 500).map { it.code }))
			} else {
				already.addAll(hospitalRepository.findAllByCodeIn(hospitalDataModel.subList(index, hospitalDataModel.count()).map { it.code }))
				break
			}
			index += 500
		}
		hospitalDataModel.removeIf { x -> x.code in already.map { y -> y.code } }
		if (hospitalDataModel.isEmpty()) {
			return "count : 0"
		}
		index = 0
		var retCount = 0
		while (true) {
			if (hospitalDataModel.count() > index + 500) {
				retCount += insertAll(hospitalDataModel.subList(index, index + 500))
			} else {
				retCount += insertAll(hospitalDataModel.subList(index, hospitalDataModel.count()))
				break
			}
			index += 500
		}
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add hospital count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}

	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	fun hospitalDataModify(token: String, data: HospitalModel): HospitalModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
		}

		hospitalRepository.findByThisPK(data.thisPK) ?: throw HospitalNotFoundException()
		val ret = hospitalRepository.save(data)

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "hospital modify")
		logRepository.save(logModel)
		return ret
	}

	private fun insertAll(data: List<HospitalModel>): Int {
		val values: String = data.stream().map{it.insertString()}.collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_HOS_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
}