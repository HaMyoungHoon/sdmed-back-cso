package sdmed.back.service.extra

import org.springframework.beans.factory.annotation.Autowired
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.config.FExtensions
import sdmed.back.config.FServiceBase
import sdmed.back.model.sqlCSO.edi.EDIHosBuffModel
import sdmed.back.model.sqlCSO.edi.EDIMedicineBuffModel
import sdmed.back.model.sqlCSO.edi.EDIPharmaBuffModel
import sdmed.back.model.sqlCSO.edi.EDIPharmaDueDateModel
import sdmed.back.model.sqlCSO.edi.EDIState
import sdmed.back.model.sqlCSO.edi.EDIUploadModel
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaFileModel
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaMedicineModel
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaModel
import sdmed.back.model.sqlCSO.extra.ExtraEDIDetailResponse
import sdmed.back.model.sqlCSO.extra.ExtraEDIPharma
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import sdmed.back.model.sqlCSO.medicine.MedicinePriceModel
import sdmed.back.model.sqlCSO.pharma.PharmaModel
import sdmed.back.repository.extra.ExtraApplyDateRepository
import sdmed.back.repository.extra.ExtraEDIUploadPharmaFileRepository
import sdmed.back.repository.extra.ExtraEDIUploadPharmaMedicineRepository
import sdmed.back.repository.extra.ExtraEDIUploadPharmaRepository
import sdmed.back.repository.extra.ExtraEDIUploadRepository
import sdmed.back.repository.extra.ExtraEDIUploadResponseRepository
import sdmed.back.repository.sqlCSO.IEDIPharmaDueDateRepository
import sdmed.back.repository.sqlCSO.IHospitalRepository
import sdmed.back.repository.sqlCSO.IMedicinePriceRepository
import sdmed.back.repository.sqlCSO.IMedicineRepository
import sdmed.back.repository.sqlCSO.IPharmaRepository
import java.util.Date
import java.util.UUID
import kotlin.collections.filter
import kotlin.collections.isNullOrEmpty

open class ExtraEDIService: FServiceBase() {
    @Autowired lateinit var extraEDIUploadRepository: ExtraEDIUploadRepository
    @Autowired lateinit var extraEDIUploadPharmaRepository: ExtraEDIUploadPharmaRepository
    @Autowired lateinit var extraEDIUploadPharmaMedicineRepository: ExtraEDIUploadPharmaMedicineRepository
    @Autowired lateinit var extraEDIUploadPharmaFileRepository: ExtraEDIUploadPharmaFileRepository
    @Autowired lateinit var extraEDIUploadResponseRepository: ExtraEDIUploadResponseRepository
    @Autowired lateinit var extraEDIApplyDateRepository: ExtraApplyDateRepository
    @Autowired lateinit var ediPharmaDueDateRepository: IEDIPharmaDueDateRepository

    @Autowired lateinit var hospitalRepository: IHospitalRepository
    @Autowired lateinit var pharmaRepository: IPharmaRepository
    @Autowired lateinit var medicineRepository: IMedicineRepository
    @Autowired lateinit var medicinePriceRepository: IMedicinePriceRepository
    protected fun parseExtraEDIDetail(data: EDIUploadModel): ExtraEDIDetailResponse {
        val ret = ExtraEDIDetailResponse().parse(data)
        ret.responseList = extraEDIUploadResponseRepository.selectAllEDIPK(ret.thisPK).toMutableList()
        val pharmaList = extraEDIUploadPharmaRepository.selectAllByEDIPK(ret.thisPK)
//        val medicineList = extraEDIUploadPharmaMedicineRepository.findAllByEdiPKAndInVisibleAndPharmaPKInOrderByMedicinePK(thisPK, inVisible, pharmaList.map { it.pharmaPK })
//        mergeEDIPharmaMedicine(pharmaList, medicineList)
        val pharmaFileList = extraEDIUploadPharmaFileRepository.findAllByEdiPharmaPKIn(pharmaList.map { it.thisPK })
        mergeEDIPharmaFile(pharmaList, pharmaFileList)
        ret.pharmaList = pharmaList.toMutableList()
        return ret
    }
    protected fun mergePharmaMedicine(pharmaList: List<EDIPharmaBuffModel>, medicineList: List<EDIMedicineBuffModel>) {
        val medicineMap = medicineList.groupBy { it.pharmaPK }
        for (pharma in pharmaList) {
            val buff = medicineMap[pharma.thisPK]?.filter { it.hosPK == pharma.hosPK }
            if (!buff.isNullOrEmpty()) {
                pharma.medicineList.addAll(buff)
            }
        }
    }
    protected fun mergeHosPharma(hosList: List<EDIHosBuffModel>, pharmaList: MutableList<EDIPharmaBuffModel>) {
        pharmaList.removeIf { it.medicineList.isEmpty() }
        val pharmaMap = pharmaList.groupBy { it.hosPK }
        for (hos in hosList) {
            val buff = pharmaMap[hos.thisPK]
            if (!buff.isNullOrEmpty()) {
                hos.pharmaList.addAll(buff)
            }
        }
    }
    protected fun mergeEDIPharmaFile(pharmaList: List<ExtraEDIPharma>, fileList: List<EDIUploadPharmaFileModel>) {
        val fileMap = fileList.groupBy { it.ediPharmaPK }
        for (pharma in pharmaList) {
            val file = fileMap[pharma.thisPK]
            if (!file.isNullOrEmpty()) {
                pharma.fileList.addAll(file)
            }
        }
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
                    this.fileList = pharma.fileList
                })
            }
        }
        return ret
    }
    protected fun realMedicineCheck(ediPK: String, medicineList: List<EDIUploadPharmaMedicineModel>, existMedicineList: List<MedicineModel>): MutableList<EDIUploadPharmaMedicineModel> {
        val ret: MutableList<EDIUploadPharmaMedicineModel> = mutableListOf()
        val existMedicineMap = existMedicineList.associateBy { it.thisPK }
        for (medicine in medicineList) {
            val existMedicine = existMedicineMap[medicine.medicinePK]
            if (existMedicine != null) {
                ret.add(medicine.apply {
                    this.ediPK = ediPK
                    if (this.price == 0) {
                        this.price = if (existMedicine.customPrice != 0) existMedicine.customPrice else existMedicine.maxPrice
                    }
                    this.charge = existMedicine.charge
                    this.kdCode = existMedicine.kdCode
                    this.name = existMedicine.orgName
                    this.makerCode = existMedicine.makerCode
                })
            }
        }
        return ret
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
            pharma?.medicineList?.add(medicine)
        }
        newData.addAll(pharmaList)
    }
    protected fun canIUseApplyDate(token: String, applyDate: Date): Boolean {
        val year = FExtensions.parseDateTimeString(applyDate, "yyyy") ?: throw NotValidOperationException()
        val month = FExtensions.parseDateTimeString(applyDate, "MM") ?: throw NotValidOperationException()
        return canIUseApplyDate(token, year, month)
    }
    protected fun canIUseApplyDate(token: String, year: String, month: String): Boolean {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        isLive(tokenUser)
        return extraEDIApplyDateRepository.selectByApplyDateAndUse(year, month) != null
    }
}