package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.config.FExtensions
import sdmed.back.config.FServiceBase
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.HowMuchHospitalModel
import sdmed.back.model.sqlCSO.HowMuchMedicineModel
import sdmed.back.model.sqlCSO.HowMuchModel
import sdmed.back.model.sqlCSO.HowMuchPharmaModel
import sdmed.back.model.sqlCSO.edi.EDIState
import sdmed.back.repository.sqlCSO.IEDIUploadPharmaMedicineRepository
import java.util.Date

class ExtraDashboardService: FServiceBase() {
	@Autowired lateinit var ediPharmaMedicineRepository: IEDIUploadPharmaMedicineRepository
	fun getMoneyList(token: String, date: Date): List<HowMuchModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		isLive(tokenUser)

		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()

		return ediPharmaMedicineRepository.selectAllByUserYearMonth(tokenUser.thisPK, year, month)
	}
	fun getMoneyHosList(token: String, date: Date, partial: Boolean = false): List<HowMuchHospitalModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		isLive(tokenUser)

		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()

		val buff = if (partial) ediPharmaMedicineRepository.selectAllByUserYearMonth(tokenUser.thisPK, year, month)
		else ediPharmaMedicineRepository.selectAllByUserYearMonthOK(tokenUser.thisPK, year, month)
		val ret = mutableListOf<HowMuchHospitalModel>()
		val ediGroup = buff.groupBy { it.hosPK }
		ediGroup.forEach { x -> ret.add(gatheringItemByHos(x.key, x.value)) }
		return ret
	}
	fun getMoneyPharmaList(token: String, date: Date): List<HowMuchPharmaModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		isLive(tokenUser)

		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()

		val buff = ediPharmaMedicineRepository.selectAllByUserYearMonth(tokenUser.thisPK, year, month)
		val ret = mutableListOf<HowMuchPharmaModel>()
		val ediGroup = buff.groupBy { it.pharmaPK }
		ediGroup.forEach { x -> ret.add(gatheringItemByPharma(x.key, x.value)) }
		return ret
	}
	fun getMoneyMedicineList(token: String, date: Date, partial: Boolean = false): List<HowMuchMedicineModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		isLive(tokenUser)

		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()

		val buff = if (partial) ediPharmaMedicineRepository.selectAllByUserYearMonth(tokenUser.thisPK, year, month)
		else ediPharmaMedicineRepository.selectAllByUserYearMonthOK(tokenUser.thisPK, year, month)
		val ret = mutableListOf<HowMuchMedicineModel>()
		val ediGroup = buff.groupBy { it.medicinePK }
		ediGroup.forEach { x -> ret.add(gatheringItemByMedicine(x.key, x.value)) }
		return ret
	}
	fun getMoneyHosDetailList(token: String, hosPK: String, date: Date): List<HowMuchModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		isLive(tokenUser)

		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()

		return ediPharmaMedicineRepository.selectAllByUserHosYearMonth(tokenUser.thisPK, hosPK, year, month)
	}
	fun getMoneyPharmaDetailList(token: String, hosPK: String, date: Date): List<HowMuchModel> {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.BusinessMan))) {
			throw AuthenticationEntryPointException()
		}
		isLive(tokenUser)

		val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()

		return ediPharmaMedicineRepository.selectAllByUserPharmaYearMonth(tokenUser.thisPK, hosPK, year, month)
	}

	private fun gatheringItemByHos(hosPK: String, howMuchModel: List<HowMuchModel>): HowMuchHospitalModel {
		return HowMuchHospitalModel().apply {
			this.thisPK = hosPK
			this.name = howMuchModel.first().hospitalName
			this.count = howMuchModel.sumOf { it.count }
			this.price = howMuchModel.sumOf { it.count * it.price.toDouble() * it.charge / 100 }
			this.ediState = FExtensions.ediStateParse(howMuchModel.map { it.ediState })
		}
	}
	private fun gatheringItemByPharma(pharmaPK: String, howMuchModel: List<HowMuchModel>): HowMuchPharmaModel {
		return HowMuchPharmaModel().apply {
			this.thisPK = pharmaPK
			this.name = howMuchModel.first().pharmaName
			this.count = howMuchModel.sumOf { it.count }
			this.price = howMuchModel.sumOf { it.count * it.price.toDouble() * it.charge / 100 }
			this.ediState = FExtensions.ediStateParse(howMuchModel.map { it.ediState })
		}
	}
	private fun gatheringItemByMedicine(medicinePK: String, howMuchModel: List<HowMuchModel>): HowMuchMedicineModel {
		return HowMuchMedicineModel().apply {
			this.thisPK = medicinePK
			this.name = howMuchModel.first().medicineName
			this.count = howMuchModel.sumOf { it.count }
			this.price = howMuchModel.sumOf { it.count * it.price.toDouble() * it.charge / 100 }
			this.ediState = FExtensions.ediStateParse(howMuchModel.map { it.ediState })
		}
	}
}