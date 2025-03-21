package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.MedicineExistException
import sdmed.back.advice.exception.MedicineNotFoundException
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.medicine.MedicineIngredientModel
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import sdmed.back.model.sqlCSO.pharma.PharmaModel
import sdmed.back.repository.sqlCSO.IPharmaRepository
import java.util.*

open class MedicineListService: MedicineService() {
	@Autowired lateinit var pharmaRepository: IPharmaRepository

	fun getList(token: String, withAllPrice: Boolean = false) = getAllMedicine(token, withAllPrice)
	fun getMedicineData(token: String, thisPK: String, withAllPrice: Boolean = false): MedicineModel {
		isValid(token)
		isLive(getUserDataByToken(token))

		val ret = medicineRepository.findByThisPK(thisPK) ?: throw MedicineNotFoundException()
		medicineIngredientRepository.findByMainIngredientCode(ret.mainIngredientCode)?.let { ret.medicineIngredientModel = it }
		ret.medicinePriceModel = if (withAllPrice) {
			medicinePriceRepository.findAllByKdCodeOrderByApplyDateDesc(ret.kdCode).toMutableList()
		} else {
			medicinePriceRepository.findAllByKdCodeOrderByApplyDateDesc(ret.kdCode).toMutableList()
		}
		ret.init()
		return ret
	}
	fun getMainIngredientList(token: String): List<MedicineIngredientModel> {
		isValid(token)
		isLive(getUserDataByToken(token))
		return medicineIngredientRepository.findAll()
	}
	fun getPharmaList(token: String): List<PharmaModel> {
		isValid(token)
		isLive(getUserDataByToken(token))
		return pharmaRepository.selectAllByInvisibleOrderByCode()
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun medicineUpload(token: String, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.MedicineChanger))) {
			throw AuthenticationEntryPointException()
		}
		isLive(tokenUser)

		val excelModel = excelFileParser.medicineUploadExcelParse(tokenUser.id, file)
		val already: MutableList<MedicineModel> = mutableListOf()
		excelModel.chunked(500).forEach { x-> already.addAll(medicineRepository.findAllByCodeIn(x.map { y -> y.code })) }
		val insertData = excelModel.toMutableList()
		insertData.removeIf { x -> x.code in already.map { y -> y.code } }
		var retCount = 0
		if (insertData.isNotEmpty()) {
			insertData.chunked(500).forEach { retCount += insertMedicineAll(it) }
		}
		val excelMap = excelModel.associateBy { it.code }
		if (already.isNotEmpty()) {
			already.forEach { x ->
				val buff = excelMap[x.code]
				if (buff != null) {
					x.safeCopy(buff)
				}
			}
			already.chunked(500).forEach { retCount += updateMedicineAll(it) }
		}

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add medicine count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun addMedicineData(token: String, data: MedicineModel): MedicineModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.MedicineChanger))) {
			throw AuthenticationEntryPointException()
		}

		val exist = medicineRepository.findByCode(data.code)
		if (exist != null) {
			throw MedicineExistException()
		}

		data.thisPK = UUID.randomUUID().toString()
		val ret = medicineRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add medicine : ${data.thisPK}")
		logRepository.save(logModel)
		return ret
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun medicineDataModify(token: String, data: MedicineModel): MedicineModel {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.MedicineChanger))) {
			throw AuthenticationEntryPointException()
		}

		val ret = medicineRepository.save(data)
		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "${data.orgName} modify")
		logRepository.save(logModel)
		return ret
	}
}