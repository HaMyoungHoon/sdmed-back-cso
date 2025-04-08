package sdmed.back.service.extra

import sdmed.back.model.sqlCSO.medicine.MedicinePriceModel

open class ExtraMedicinePriceListService: ExtraMedicineService() {
    fun getList(token: String) = getAllMedicine(token)
    fun getLike(token: String, searchString: String) = getLikeMedicine(token, searchString)
    fun getPagingList(token: String, page: Int = 0, size: Int = 100) = getPagingAllMedicine(token, page, size)
    fun getPagingLike(token: String, searchString: String, page: Int = 0, size: Int = 100) = getPagingLikeMedicine(token, searchString, page, size)
    fun getMedicinePriceList(token: String, kdCode: String): List<MedicinePriceModel> {
        isValid(token)
        isLive(getUserDataByToken(token))
        return extraMedicinePriceRepository.findAllByKdCodeOrderByApplyDateDesc(kdCode)
    }
}