package sdmed.back.config

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.*
import sdmed.back.model.sqlCSO.edi.EDIPharmaDueDateModel
import sdmed.back.model.sqlCSO.hospital.HospitalModel
import sdmed.back.model.sqlCSO.hospital.HospitalTempModel
import sdmed.back.model.sqlCSO.hospital.PharmacyTempModel
import sdmed.back.model.sqlCSO.medicine.MedicineIngredientModel
import sdmed.back.model.sqlCSO.medicine.MedicineModel
import sdmed.back.model.sqlCSO.medicine.MedicinePriceModel
import sdmed.back.model.sqlCSO.pharma.PharmaMedicineExcelParsingModel
import sdmed.back.model.sqlCSO.pharma.PharmaModel
import sdmed.back.model.sqlCSO.user.UserDataModel
import sdmed.back.model.sqlCSO.user.UserMappingBuffModel
import java.util.*

@Component
class FExcelFileParser {
	fun userUploadExcelParse(uploaderID: String, file: MultipartFile): MutableList<UserDataModel> {
		FExtensions.folderExist(FExcelParserType.USER)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.USER, uploaderID)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (!UserDataModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw UserDataFileUploadException()
		}

		val ret: MutableList<UserDataModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = UserDataModel()
			val setRowRet = model.rowSet(x)
			if (setRowRet == null) {
				FExtensions.fileDelete(copiedLocation)
				throw UserDataFileUploadException(model.errorString())
			}
			if (setRowRet == false) {
				return@forEach
			}
			ret.add(model)
		}

		return ret
	}
	fun pharmaUploadExcelParse(uploaderID: String, file: MultipartFile): MutableList<PharmaModel> {
		FExtensions.folderExist(FExcelParserType.PHARMA)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.PHARMA, uploaderID)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (!PharmaModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw PharmaDataFileUploadException()
		}

		val ret: MutableList<PharmaModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = PharmaModel()
			val setRowRet = model.rowSet(x)
			if (setRowRet == null) {
				FExtensions.fileDelete(copiedLocation)
				throw PharmaDataFileUploadException(model.errorString())
			}
			if (setRowRet == false) {
				return@forEach
			}
			ret.add(model)
		}

		return ret
	}
	fun pharmaMedicineUploadExcelParse(uploaderID: String, file: MultipartFile): MutableList<PharmaMedicineExcelParsingModel> {
		FExtensions.folderExist(FExcelParserType.PHARMA_MEDICINE)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.PHARMA_MEDICINE, uploaderID)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (!PharmaMedicineExcelParsingModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw PharmaDataFileUploadException()
		}

		val ret: MutableList<PharmaMedicineExcelParsingModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = PharmaMedicineExcelParsingModel()
			val setRowRet = model.rowSet(x)
			if (setRowRet == null) {
				FExtensions.fileDelete(copiedLocation)
				throw PharmaDataFileUploadException(model.errorString())
			}
			if (setRowRet == false) {
				return@forEach
			}
			ret.add(model)
		}

		return ret.groupBy { it.pharmaCode }.map { (pharmaCode, group) ->
			val mergedSubCodeList = group.flatMap { it.medicineCodeList }.distinct().toMutableList()
			PharmaMedicineExcelParsingModel().apply {
				this.pharmaCode = pharmaCode
				this.medicineCodeList = mergedSubCodeList
			}
		}.toMutableList()
	}
	fun hospitalUploadExcelParse(uploaderID: String, file: MultipartFile): MutableList<HospitalModel> {
		FExtensions.folderExist(FExcelParserType.HOSPITAL)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.HOSPITAL, uploaderID)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (!HospitalModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw HosDataFileUploadException()
		}

		val ret: MutableList<HospitalModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = HospitalModel()
			val setRowRet = model.rowSet(x)
			if (setRowRet == null) {
				FExtensions.fileDelete(copiedLocation)
				throw HosDataFileUploadException(model.errorString())
			}
			if (setRowRet == false) {
				return@forEach
			}
			ret.add(model)
		}

		return ret
	}
	fun medicineUploadExcelParse(uploaderID: String, file: MultipartFile): MutableList<MedicineModel> {
		FExtensions.folderExist(FExcelParserType.MEDICINE)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.MEDICINE, uploaderID)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (!MedicineModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw MedicineDataFileUploadException()
		}

		val ret: MutableList<MedicineModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = MedicineModel()
			val setRowRet = model.rowSet(x)
			if (setRowRet == null) {
				FExtensions.fileDelete(copiedLocation)
				throw MedicineDataFileUploadException(model.errorString())
			}
			if (setRowRet == false) {
				return@forEach
			}
			ret.add(model)
		}

		return ret
	}
	fun medicineIngredientUploadExcelParse(uploaderID: String, file: MultipartFile): MutableList<MedicineIngredientModel> {
		FExtensions.folderExist(FExcelParserType.MEDICINE_INGREDIENT)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.MEDICINE_INGREDIENT, uploaderID)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (!MedicineIngredientModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw MedicineDataFileUploadException()
		}

		val ret: MutableList<MedicineIngredientModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = MedicineIngredientModel()
			val setRowRet = model.rowSet(x)
			if (setRowRet == null) {
				FExtensions.fileDelete(copiedLocation)
				throw MedicineDataFileUploadException(model.errorString())
			}
			if (setRowRet == false) {
				return@forEach
			}
			ret.add(model)
		}

		return ret
	}
	fun medicinePriceUploadExcelParse(uploaderID: String, applyDate: Date, file: MultipartFile): MutableList<MedicinePriceModel> {
		FExtensions.folderExist(FExcelParserType.MEDICINE_PRICE)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.MEDICINE_PRICE, uploaderID)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (!MedicinePriceModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw MedicineDataFileUploadException()
		}

		val ret: MutableList<MedicinePriceModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = MedicinePriceModel()
			val setRowRet = model.rowSet(x)
			if (setRowRet == null) {
				FExtensions.fileDelete(copiedLocation)
				throw MedicineDataFileUploadException(model.errorString())
			}
			if (setRowRet == false) {
				return@forEach
			}
			model.applyDate = applyDate
			ret.add(model)
		}

		return ret
	}
	fun ediDueDateUploadExcelParse(uploaderID: String, file: MultipartFile): MutableList<EDIPharmaDueDateModel> {
		FExtensions.folderExist(FExcelParserType.EDI_DUE_DATE)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.EDI_DUE_DATE, uploaderID)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (!EDIPharmaDueDateModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw EDIDueDateFileUploadException()
		}

		val ret: MutableList<EDIPharmaDueDateModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = EDIPharmaDueDateModel()
			val setRowRet = model.rowSet(x)
			if (setRowRet == null) {
				FExtensions.fileDelete(copiedLocation)
				throw EDIDueDateFileUploadException(model.errorString())
			}
			if (setRowRet == false) {
				return@forEach
			}
			ret.add(model)
		}

		return ret
	}
	fun userMappingDateUploadExcelParse(uploaderID: String, file: MultipartFile): MutableList<UserMappingBuffModel> {
		FExtensions.folderExist(FExcelParserType.USER_MAPPING)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.USER_MAPPING, uploaderID)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (!UserMappingBuffModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw UserMappingFileUploadException()
		}

		val ret: MutableList<UserMappingBuffModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = UserMappingBuffModel()
			val setRowRet = model.rowSet(x)
			if (setRowRet == null) {
				FExtensions.fileDelete(copiedLocation)
				throw UserMappingFileUploadException(model.errorString())
			}
			if (setRowRet == false) {
				return@forEach
			}
			ret.add(model)
		}

		return ret
	}
	fun hospitalTempUploadExcelParse(uploaderID: String, file: MultipartFile): MutableList<HospitalTempModel> {
		FExtensions.folderExist(FExcelParserType.HOSPITAL_TEMP)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.HOSPITAL_TEMP, uploaderID)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (!HospitalTempModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw HospitalTempFileUploadException()
		}

		val ret: MutableList<HospitalTempModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = HospitalTempModel()
			val setRowRet = model.rowSet(x)
			if (setRowRet == null) {
				FExtensions.fileDelete(copiedLocation)
				throw HospitalTempFileUploadException(model.errorString())
			}
			if (setRowRet == false) {
				return@forEach
			}
			ret.add(model)
		}

		return ret
	}
	fun pharmacyTempUploadExcelParse(uploaderID: String, file: MultipartFile): MutableList<PharmacyTempModel> {
		FExtensions.folderExist(FExcelParserType.PHARMACY_TEMP)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.PHARMACY_TEMP, uploaderID)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (!PharmacyTempModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw HospitalTempFileUploadException()
		}

		val ret: MutableList<PharmacyTempModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = PharmacyTempModel()
			val setRowRet = model.rowSet(x)
			if (setRowRet == null) {
				FExtensions.fileDelete(copiedLocation)
				throw HospitalTempFileUploadException(model.errorString())
			}
			if (setRowRet == false) {
				return@forEach
			}
			ret.add(model)
		}

		return ret
	}
}