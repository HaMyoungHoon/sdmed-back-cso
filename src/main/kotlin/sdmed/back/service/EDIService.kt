package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import sdmed.back.advice.exception.*
import sdmed.back.config.FExtensions
import sdmed.back.config.FServiceBase
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.edi.*
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import sdmed.back.model.sqlCSO.medicine.MedicinePriceModel
import sdmed.back.model.sqlCSO.pharma.PharmaModel
import sdmed.back.repository.sqlCSO.*
import java.util.*

class EDIService: FServiceBase() {
	@Autowired lateinit var ediUploadRepository: IEDIUploadRepository
	@Autowired lateinit var ediUploadPharmaRepository: IEDIUploadPharmaRepository
	@Autowired lateinit var ediUploadPharmaMedicineRepository: IEDIUploadPharmaMedicineRepository
	@Autowired lateinit var ediUploadFileRepository: IEDIUploadFileRepository
	@Autowired lateinit var ediUploadResponseRepository: IEDIUploadResponseRepository
	@Autowired lateinit var ediApplyDateRepository: IEDIApplyDateRepository
	@Autowired lateinit var ediPharmaDueDateRepository: IEDIPharmaDueDateRepository

	@Autowired lateinit var hospitalRepository: IHospitalRepository
	@Autowired lateinit var pharmaRepository: IPharmaRepository
	@Autowired lateinit var medicineRepository: IMedicineRepository
	@Autowired lateinit var medicinePriceRepository: IMedicinePriceRepository

	protected fun parseEDIUploadModel(data: EDIUploadModel, inVisible: Boolean = false) = data.apply {
		this.responseList = ediUploadResponseRepository.findAllByEdiPKOrderByRegDate(thisPK).toMutableList()
		this.fileList = ediUploadFileRepository.findAllByEdiPKOrderByThisPK(thisPK).toMutableList()
		val pharmaList = ediUploadPharmaRepository.findALlByEdiPKOrderByPharmaPK(thisPK)
		val medicineList = ediUploadPharmaMedicineRepository.findAllByEdiPKAndInVisibleAndPharmaPKInOrderByMedicinePK(thisPK, inVisible, pharmaList.map { it.pharmaPK })
		val newData: MutableList<EDIUploadPharmaModel> = mutableListOf()
		mergeEDIPharmaMedicine(pharmaList, medicineList, newData)
		this.pharmaList = newData
	}
	protected fun carriedOverPharma(pharmaList: List<EDIUploadPharmaModel>, dueDateList: List<EDIPharmaDueDateModel>): List<EDIUploadPharmaModel> {
		val dueDateMap = dueDateList.associateBy { it.pharmaPK }
		return pharmaList.map { x ->
			val buff = dueDateMap[x.pharmaPK]
			if (buff != null) {
				var isCarriedOver = false
				var pharmaYear = x.year.toInt()
				var pharmaMonth = x.month.toInt()
				var pharmaDay = x.day.toInt()
				val buffYear = buff.year.toInt()
				val buffMonth = buff.month.toInt()
				val buffDay = buff.day.toInt()
				while (pharmaYear == buffYear && pharmaMonth == buffMonth && pharmaDay > buffDay) {
					pharmaMonth++
					if (pharmaMonth > 12) {
						pharmaMonth = 1
						pharmaYear++
					}
					pharmaDay = 1
					isCarriedOver = true
				}

				x.dateCopy("%04d".format(pharmaYear), "%02d".format(pharmaMonth), "%02d".format(pharmaDay)).apply {
					this.isCarriedOver = isCarriedOver
					ediState = if (isCarriedOver) EDIState.Pending else EDIState.None
				}
			} else {
				x
			}
		}
	}
	protected fun realPharmaCheck(ediPK: String, pharmaList: List<EDIUploadPharmaModel>, existPharmaList: List<PharmaModel>): MutableList<EDIUploadPharmaModel> {
		val ret: MutableList<EDIUploadPharmaModel> = mutableListOf()
		val pharmaMap = pharmaList.associateBy { it.pharmaPK }
		for (existPharma in existPharmaList) {
			val pharma = pharmaMap[existPharma.thisPK]
			if (pharma != null) {
				ret.add(pharma.apply {
					this.ediPK = ediPK
					thisPK = UUID.randomUUID().toString()
					orgName = existPharma.orgName
				})
			}
		}
		return ret
	}
	protected fun realMedicineCheck(ediPK: String, medicineList: List<EDIUploadPharmaMedicineModel>, existMedicineList: List<MedicineModel>): MutableList<EDIUploadPharmaMedicineModel> {
		val ret: MutableList<EDIUploadPharmaMedicineModel> = mutableListOf()
		val medicineMap = medicineList.associateBy { it.medicinePK }
		for (existMedicine in existMedicineList) {
			val medicine = medicineMap[existMedicine.thisPK]
			if (medicine != null) {
				ret.add(medicine.apply {
					this.ediPK = ediPK
					if (this.price == 0) {
						this.price = if (existMedicine.customPrice != 0) existMedicine.customPrice else existMedicine.maxPrice
					}
					this.charge = existMedicine.charge
					this.name = existMedicine.name
					this.makerCode = existMedicine.makerCode
				})
			}
		}
		return ret
	}
	protected fun mergePharmaMedicine(pharmaList: List<EDIPharmaBuffModel>, medicineList: List<EDIMedicineBuffModel>) {
		val medicineMap = medicineList.groupBy { it.pharmaPK }
		for (pharma in pharmaList) {
			val buff = medicineMap[pharma.thisPK]
			if (!buff.isNullOrEmpty()) {
				pharma.medicineList.addAll(buff)
			}
		}
	}
	protected fun mergeMedicinePrice(medicineList: List<MedicineModel>, medicinePriceList: List<MedicinePriceModel>, newData: MutableList<MedicineModel>) {
		val medicinePriceMap = medicinePriceList.associateBy { it.kdCode }
		for (medicine in medicineList) {
			val buff = medicinePriceMap[medicine.kdCode]
			if (buff != null) {
				newData.add(medicine.apply {
					maxPrice = buff.maxPrice
				})
			} else {
				newData.add(medicine)
			}
		}
	}
	protected fun mergeEDIPharmaMedicine(pharmaList: List<EDIUploadPharmaModel>, medicineList: List<EDIUploadPharmaMedicineModel>, newData: MutableList<EDIUploadPharmaModel>) {
		val pharmaMap = pharmaList.associateBy { it.pharmaPK }
		for (medicine in medicineList) {
			val pharma = pharmaMap[medicine.pharmaPK]
			if (pharma != null) {
				val buff = newData.find { x -> x.pharmaPK == medicine.pharmaPK }
				if (buff != null) {
					buff.medicineList.add(medicine)
				} else {
					newData.add(EDIUploadPharmaModel().copy(pharma).apply {
						this.medicineList.add(medicine)
					})
				}
			}
		}
	}
	protected fun canIUseApplyDate(token: String, applyDate: Date): Boolean {
		val year = FExtensions.parseDateTimeString(applyDate, "yyyy") ?: throw NotValidOperationException()
		val month = FExtensions.parseDateTimeString(applyDate, "MM") ?: throw NotValidOperationException()
		return canIUseApplyDate(token, year, month)
	}
	protected fun canIUseApplyDate(token: String, year: String, month: String): Boolean {
		isValid(token)
		val tokenUser = getUserDataByToken(token)
		if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee))) {
			return ediApplyDateRepository.selectByApplyDateAndUse(year, month) != null
		}
		return ediApplyDateRepository.selectByApplyDate(year, month) != null
	}
}