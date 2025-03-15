package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.HospitalNotFoundException
import sdmed.back.config.FConstants
import sdmed.back.config.FServiceBase
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.hospital.HospitalTempFileModel
import sdmed.back.model.sqlCSO.hospital.HospitalTempModel
import sdmed.back.model.sqlCSO.hospital.PharmacyTempModel
import sdmed.back.repository.sqlCSO.IHospitalTempFileRepository
import sdmed.back.repository.sqlCSO.IHospitalTempRepository
import sdmed.back.repository.sqlCSO.IPharmacyTempRepository
import java.util.stream.Collectors

open class HospitalTempService: FServiceBase() {
	@Autowired lateinit var hospitalTempRepository: IHospitalTempRepository
	@Autowired lateinit var hospitalTempFileRepository: IHospitalTempFileRepository
	@Autowired lateinit var pharmacyTempRepository: IPharmacyTempRepository

	fun getHospitalList(token: String, page: Int = 0, size: Int = 100): Page<HospitalTempModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)
		val pageable = PageRequest.of(page, size)
		return hospitalTempRepository.findAllByOrderByOrgNameAsc(pageable)
	}
	fun getHospitalDetail(token: String, thisPK: String): HospitalTempModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)
		val ret = hospitalTempRepository.findByThisPK(thisPK) ?: throw HospitalNotFoundException()
		ret.fileList.addAll(hospitalTempFileRepository.findAllByHospitalTempPK(thisPK))
		return ret
	}
	fun getHospitalContains(token: String, searchString: String): List<HospitalTempModel> {
		if (searchString.isBlank()) {
			return mutableListOf()
		}
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)
		return hospitalTempRepository.selectAllContains(searchString, searchString)
	}
	fun getNearbyHospital(token: String, latitude: Double, longitude: Double, distance: Int = 1000): List<HospitalTempModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)
		return hospitalTempRepository.selectAllNearby(latitude, longitude, distance)
	}
	fun getNearbyPharmacy(token: String, latitude: Double, longitude: Double, distance: Int = 1000): List<PharmacyTempModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		isLive(tokenUser)
		return pharmacyTempRepository.selectAllNearby(latitude, longitude, distance)
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun hospitalTempUpload(token: String, file: MultipartFile, alreadyUpdate: Boolean = false): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
		}

		val excelModel = excelFileParser.hospitalTempUploadExcelParse(tokenUser.id, file).distinctBy { it.code }
		val already = mutableListOf<HospitalTempModel>()
		if (alreadyUpdate) {
			excelModel.chunked(500).forEach { x -> already.addAll(hospitalTempRepository.findAllByCodeInOrderByOrgNameAsc(x.map { y -> y.code })) }
		}
		var retCount = 0
		val saveList = excelModel.toMutableList()
		saveList.removeIf { x -> x.code in already.map { y -> y.code } }
		saveList.chunked(500).forEach { x -> retCount += insertHospitalAll(x) }
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
		already.chunked(500).forEach { x -> retCount += updateHospitalAll(x) }
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add hospital count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun hospitalTempFileUpload(token: String, hospitalTempFileModel: HospitalTempFileModel): HospitalTempFileModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
		}

		hospitalTempRepository.findByThisPK(hospitalTempFileModel.hospitalTempPK) ?: throw HospitalNotFoundException()
		val ret = hospitalTempFileRepository.save(hospitalTempFileModel)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add hospital file : ${ret.hospitalTempPK}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun hospitalTempFileUpload(token: String, thisPK: String, hospitalTempFileModel: List<HospitalTempFileModel>): List<HospitalTempFileModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
		}

		hospitalTempRepository.findByThisPK(thisPK) ?: throw HospitalNotFoundException()
		val ret = hospitalTempFileRepository.saveAll(hospitalTempFileModel)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add hospital file : ${ret.count()}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun pharmacyTempUpload(token: String, file: MultipartFile, alreadyUpdate: Boolean = false): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.HospitalChanger))) {
			throw AuthenticationEntryPointException()
		}

		val excelModel = excelFileParser.pharmacyTempUploadExcelParse(tokenUser.id, file).distinctBy { it.code }
		val already = mutableListOf<PharmacyTempModel>()
		if (alreadyUpdate) {
			excelModel.chunked(500).forEach { x -> already.addAll(pharmacyTempRepository.findAllByCodeInOrderByOrgNameAsc(x.map { y -> y.code })) }
		}
		var retCount = 0
		val saveList = excelModel.toMutableList()
		saveList.removeIf { x -> x.code in already.map { y -> y.code } }
		saveList.chunked(500).forEach { x -> retCount += insertPharmacyAll(x) }
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
		already.chunked(500).forEach { x -> retCount += updatePharmacyAll(x) }
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add pharmacy count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}

	private fun insertHospitalAll(data: List<HospitalTempModel>): Int {
		if (data.isEmpty()) {
			return 0
		}

		val values: String = data.stream().map{it.insertString()}.collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_HOSPITAL_TEMP_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun updateHospitalAll(data: List<HospitalTempModel>): Int {
		if (data.isEmpty()) {
			return 0
		}
		data.forEach { x -> entityManager.merge(x) }
		entityManager.flush()
		entityManager.clear()
		return data.size
	}
	private fun insertPharmacyAll(data: List<PharmacyTempModel>): Int {
		if (data.isEmpty()) {
			return 0
		}

		val values: String = data.stream().map{it.insertString()}.collect(Collectors.joining(","))
		val sqlString = "${FConstants.MODEL_PHARMACY_TEMP_INSERT_INTO}$values"
		val ret = entityManager.createNativeQuery(sqlString).executeUpdate()
		entityManager.flush()
		entityManager.clear()
		return ret
	}
	private fun updatePharmacyAll(data: List<PharmacyTempModel>): Int {
		if (data.isEmpty()) {
			return 0
		}
		data.forEach { x -> entityManager.merge(x) }
		entityManager.flush()
		entityManager.clear()
		return data.size
	}
}