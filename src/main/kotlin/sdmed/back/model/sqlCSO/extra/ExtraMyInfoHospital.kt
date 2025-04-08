package sdmed.back.model.sqlCSO.extra

data class ExtraMyInfoHospital(
    var thisPK: String = "",
    var orgName: String = "",
    var address: String = "",
    var pharmaList: MutableList<ExtraMyInfoPharma> = mutableListOf()
) {
    constructor(thisPK: String, orgName: String, address: String) : this() {
        this.thisPK = thisPK
        this.orgName = orgName
        this.address = address
    }

    fun clone(): ExtraMyInfoHospital {
        return ExtraMyInfoHospital(thisPK, orgName, address, pharmaList.toMutableList())
    }
}