package sdmed.back.model.sqlCSO.extra

import sdmed.back.model.sqlCSO.medicine.MedicineModel

data class ExtraMedicinePriceResponse(
    var thisPK: String = "",
    var mainIngredientCode: String = "",
    var mainIngredientName: String = "",
    var clientName: String? = null,
    var makerName: String? = null,
    var orgName: String = "",
    var kdCode: String = "",
    var customPrice: Int = 0,
    var maxPrice: Int = 0,
    var standard: String = "",
    var etc1: String = "",
) {
    constructor(buff: MedicineModel, makerName: String?, clientName: String?) : this() {
        this.thisPK = buff.thisPK
        this.orgName = buff.orgName
        this.kdCode = buff.kdCode
        this.standard = buff.standard
        this.etc1 = buff.etc1
        this.mainIngredientCode = buff.mainIngredientCode
        this.customPrice = buff.customPrice
        this.maxPrice = buff.maxPrice
        this.makerName = makerName
        this.clientName = clientName
    }
}