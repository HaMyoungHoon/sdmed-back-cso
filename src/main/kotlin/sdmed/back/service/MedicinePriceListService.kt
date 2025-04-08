package sdmed.back.service

import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.medicine.MedicineIngredientModel
import sdmed.back.model.sqlCSO.medicine.MedicinePriceModel
import java.util.*

open class MedicinePriceListService: MedicineService() {

	fun getList(token: String, withAllPrice: Boolean = false) = getAllMedicine(token, withAllPrice)
	fun getMedicinePriceList(token: String, kdCode: String): List<MedicinePriceModel> {
		isValid(token)
		isLive(getUserDataByToken(token))
		return medicinePriceRepository.findAllByKdCodeOrderByApplyDateDesc(kdCode)
	}
	fun getPriceApplyDate(token: String): String {
		isValid(token)
		isLive(getUserDataByToken(token))
		return medicinePriceRepository.selectLatestDate()
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun medicinePriceUpload(token: String, applyDate: Date, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.MedicineChanger))) {
			throw AuthenticationEntryPointException()
		}
		isLive(tokenUser)

		val excelModel = excelFileParser.medicinePriceUploadExcelParse(tokenUser.id, applyDate, file)
		val already = medicinePriceRepository.selectAllByRecentData()
		val newData: MutableList<MedicinePriceModel> = mutableListOf()
		mergeMedicinePrice(already, excelModel, newData)
		if (excelModel.isEmpty()) {
			return "count : 0"
		}

		var retCount = 0
		newData.chunked(500).forEach { retCount += insertMedicinePriceAll(it) }
		if (retCount == 0) {
			return "count : $retCount"
		}

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add medicine price count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}
	@Transactional(value = CSOJPAConfig.TRANSACTION_MANAGER)
	open fun medicineIngredientUpload(token: String, file: MultipartFile): String {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.MedicineChanger))) {
			throw AuthenticationEntryPointException()
		}
		isLive(tokenUser)

		val excelModel = excelFileParser.medicineIngredientUploadExcelParse(tokenUser.id, file)
		val already: MutableList<MedicineIngredientModel> = mutableListOf()
		excelModel.chunked(500).forEach { x-> already.addAll(medicineIngredientRepository.findAllByMainIngredientCodeIn(x.map { y -> y.mainIngredientCode })) }
		excelModel.removeIf { x -> x.mainIngredientCode in already.map { y -> y.mainIngredientCode } }
		if (excelModel.isEmpty()) {
			return "count : 0"
		}

		var retCount = 0
		excelModel.distinctBy { x -> x.mainIngredientCode }.chunked(500).forEach { retCount += insertMedicineIngredientAll(it) }
		if (retCount == 0) {
			return "count : $retCount"
		}

		val stackTrace = Thread.currentThread().stackTrace
		val logModel = LogModel().build(tokenUser.thisPK, stackTrace[1].className, stackTrace[1].methodName, "add medicine count : $retCount")
		logRepository.save(logModel)
		return "count : $retCount"
	}
}