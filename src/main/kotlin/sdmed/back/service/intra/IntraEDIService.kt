package sdmed.back.service.intra

import org.springframework.beans.factory.annotation.Autowired
import sdmed.back.config.FServiceBase
import sdmed.back.model.sqlCSO.edi.EDIUploadModel
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaFileModel
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaMedicineModel
import sdmed.back.model.sqlCSO.edi.EDIUploadPharmaModel
import sdmed.back.repository.intra.IntraEDIApplyDateRepository
import sdmed.back.repository.intra.IntraEDIPharmaDueDateRepository
import sdmed.back.repository.intra.IntraEDIUploadPharmaFileRepository
import sdmed.back.repository.intra.IntraEDIUploadPharmaMedicineRepository
import sdmed.back.repository.intra.IntraEDIUploadPharmaRepository
import sdmed.back.repository.intra.IntraEDIUploadRepository
import sdmed.back.repository.intra.IntraEDIUploadResponseRepository
import sdmed.back.repository.sqlCSO.IHospitalRepository
import sdmed.back.repository.sqlCSO.IPharmaRepository
import kotlin.collections.isNullOrEmpty

class IntraEDIService: FServiceBase() {
    @Autowired lateinit var intraEDIUploadRepository: IntraEDIUploadRepository
    @Autowired lateinit var intraEDIUploadPharmaRepository: IntraEDIUploadPharmaRepository
    @Autowired lateinit var intraEDIUploadPharmaFileRepository: IntraEDIUploadPharmaFileRepository
    @Autowired lateinit var intraEDIUploadPharmaMedicineRepository: IntraEDIUploadPharmaMedicineRepository
    @Autowired lateinit var intraEDIUploadResponseRepository: IntraEDIUploadResponseRepository
    @Autowired lateinit var intraEDIApplyDateRepository: IntraEDIApplyDateRepository
    @Autowired lateinit var intraEDIPharmaDueDateRepository: IntraEDIPharmaDueDateRepository

    @Autowired lateinit var hospitalRepository: IHospitalRepository
    @Autowired lateinit var pharmaRepository: IPharmaRepository
    protected fun parseEDIUploadModel(data: EDIUploadModel, inVisible: Boolean = false) = data.apply {
        this.responseList = intraEDIUploadResponseRepository.findAllByEdiPKOrderByRegDate(thisPK).toMutableList()
        val pharmaList = intraEDIUploadPharmaRepository.findAllByEdiPKOrderByPharmaPK(thisPK)
        val medicineList = intraEDIUploadPharmaMedicineRepository.findAllByEdiPKAndInVisibleAndPharmaPKInOrderByMedicinePK(thisPK, inVisible, pharmaList.map { it.pharmaPK })
        val newData: MutableList<EDIUploadPharmaModel> = mutableListOf()
        mergeEDIPharmaMedicine(pharmaList, medicineList, newData)
        val pharmaFileList = intraEDIUploadPharmaFileRepository.findAllByEdiPharmaPKIn(newData.map { it.thisPK })
        mergeEDIPharmaFile(newData, pharmaFileList)
        this.pharmaList = newData
    }
    protected fun mergeEDIPharmaMedicine(pharmaList: List<EDIUploadPharmaModel>, medicineList: List<EDIUploadPharmaMedicineModel>, newData: MutableList<EDIUploadPharmaModel>) {
        val pharmaMap = pharmaList.associateBy { it.pharmaPK }
        for (medicine in medicineList) {
            val pharma = pharmaMap[medicine.pharmaPK]
            pharma?.medicineList?.add(medicine)
        }
        newData.addAll(pharmaList)
    }
    protected fun mergeEDIPharmaFile(pharmaList: List<EDIUploadPharmaModel>, fileList: List<EDIUploadPharmaFileModel>) {
        val fileMap = fileList.groupBy { it.ediPharmaPK }
        for (pharma in pharmaList) {
            val file = fileMap[pharma.thisPK]
            if (!file.isNullOrEmpty()) {
                pharma.fileList.addAll(file)
            }
        }
    }
}