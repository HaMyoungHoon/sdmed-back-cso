package sdmed.back.config

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.HosDataFileUploadException
import sdmed.back.advice.exception.MedicineDataFileUploadException
import sdmed.back.advice.exception.PharmaDataFileUploadException
import sdmed.back.advice.exception.UserDataFileUploadException
import sdmed.back.model.sqlCSO.*
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
}