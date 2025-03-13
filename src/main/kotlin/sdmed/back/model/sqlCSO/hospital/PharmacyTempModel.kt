package sdmed.back.model.sqlCSO.hospital

import jakarta.persistence.*
import sdmed.back.config.FConstants
import sdmed.back.config.FExtensions
import sdmed.back.model.sqlCSO.FExcelParseModel
import java.util.*
import kotlin.jvm.Transient

@Entity
data class PharmacyTempModel(
	@Id
	@Column(columnDefinition = "nvarchar(36)", updatable = false, nullable = false)
	var thisPK: String = UUID.randomUUID().toString(),
	@Column(columnDefinition = "nvarchar(100)", nullable = false, unique = true)
	var code: String = "",
	@Column(columnDefinition = "nvarchar(255)", nullable = false)
	var orgName: String = "",
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "nvarchar(20)")
	var hospitalTempTypeCode: HospitalTempTypeCode = HospitalTempTypeCode.CODE_00,
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "nvarchar(20)")
	var hospitalTempMetroCode: HospitalTempMetroCode = HospitalTempMetroCode.CODE_000000,
	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "nvarchar(20)")
	var hospitalTempCityCode: HospitalTempCityCode = HospitalTempCityCode.CODE_000000,
	@Column(columnDefinition = "nvarchar(50)")
	var hospitalTempLocalName: String = "",
	@Column
	var zipCode: Int = 0,
	@Column(columnDefinition = "nvarchar(255)")
	var address: String = "",
	@Column(columnDefinition = "nvarchar(255)")
	var phoneNumber: String = "",
	@Column
	var openDate: Date = Date(),
	@Column
	var longitude: Double = 0.0,
	@Column
	var latitude: Double = 0.0,
): FExcelParseModel() {
	@Transient
	override var dataCount = FConstants.MODEL_PHARMACY_TEMP_COUNT
	override fun indexSet(data: String?, index: Int) {
		data ?: return
		when (index) {
			0 -> code = data
			1 -> orgName = data
			2 -> hospitalTempTypeCode = HospitalTempTypeCode.parseCode(data)
			4 -> hospitalTempMetroCode = HospitalTempMetroCode.parseCode(data.toIntOrNull())
			6 -> hospitalTempCityCode = HospitalTempCityCode.parseCode(data.toIntOrNull())
			8 -> hospitalTempLocalName = data
			9 -> zipCode = data.toIntOrNull() ?: 0
			10 -> address = data
			11 -> phoneNumber = data
			12 -> openDate = FExtensions.parseStringToJavaDate(data, "yyyyMMdd")
			13 -> longitude = data.toDoubleOrNull() ?: 0.0
			14 -> latitude = data.toDoubleOrNull() ?: 0.0
		}
	}
	override fun titleGet(index: Int): String {
		return when (index) {
			0 -> FConstants.MODEL_PHARMACY_TEMP_CODE
			1 -> FConstants.MODEL_PHARMACY_TEMP_ORG_NAME
			2 -> FConstants.MODEL_PHARMACY_TEMP_TYPE_CODE
			4 -> FConstants.MODEL_PHARMACY_TEMP_METRO_CODE
			6 -> FConstants.MODEL_PHARMACY_TEMP_CITY_CODE
			8 -> FConstants.MODEL_PHARMACY_TEMP_LOCAL_NAME
			9 -> FConstants.MODEL_PHARMACY_TEMP_ZIP_CODE
			10 ->	FConstants.MODEL_PHARMACY_TEMP_ADDRESS
			11 ->	FConstants.MODEL_PHARMACY_TEMP_PHONE_NUMBER
			12 ->	FConstants.MODEL_PHARMACY_TEMP_OPEN_DATE
			13 ->	FConstants.MODEL_PHARMACY_TEMP_LONGITUDE
			14 ->	FConstants.MODEL_PHARMACY_TEMP_LATITUDE
			else -> ""
		}
	}
	override fun errorCondition(): Boolean {
		if (code.isBlank()) return true
		if (orgName.isBlank()) return true
		if (address.isBlank()) return true
		return false
	}
	override fun errorString() = "${FConstants.MODEL_PHARMACY_TEMP_CODE} : ${code}\n${FConstants.MODEL_PHARMACY_TEMP_ORG_NAME} : ${orgName}\n${FConstants.MODEL_PHARMACY_TEMP_ADDRESS} : ${address}"
	fun insertString(): String {
		val orgName = FExtensions.escapeString(this.orgName)
		val address = FExtensions.escapeString(this.address)
		val openDate = FExtensions.parseDateTimeString(this.openDate, "yyyy-MM-dd")
		return "('$thisPK', '$code','$orgName','$hospitalTempTypeCode','$hospitalTempMetroCode','$hospitalTempCityCode','$hospitalTempLocalName','$zipCode','$address','$phoneNumber','$openDate','$longitude','$latitude')"
	}
	fun safeCopy(rhs: PharmacyTempModel): PharmacyTempModel {
		this.hospitalTempTypeCode = rhs.hospitalTempTypeCode
		this.hospitalTempMetroCode = rhs.hospitalTempMetroCode
		this.hospitalTempCityCode = rhs.hospitalTempCityCode
		this.hospitalTempLocalName = rhs.hospitalTempLocalName
		this.zipCode = rhs.zipCode
		this.address = rhs.address
		this.phoneNumber = rhs.phoneNumber
		this.openDate = rhs.openDate
		this.longitude = rhs.longitude
		this.latitude = rhs.latitude
		return this
	}
}