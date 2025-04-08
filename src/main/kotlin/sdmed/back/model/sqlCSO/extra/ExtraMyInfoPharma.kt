package sdmed.back.model.sqlCSO.extra

data class ExtraMyInfoPharma(
    var thisPK: String = "",
    var orgName: String = "",
    var address: String = ""
) {
    fun clone(): ExtraMyInfoPharma {
        return ExtraMyInfoPharma(thisPK, orgName, address)
    }
}