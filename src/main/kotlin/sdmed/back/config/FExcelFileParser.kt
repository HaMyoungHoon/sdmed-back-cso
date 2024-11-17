package sdmed.back.config

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.UserDataFileUploadException
import sdmed.back.model.sqlCSO.CorrespondentModel
import sdmed.back.model.sqlCSO.HospitalModel
import sdmed.back.model.sqlCSO.PharmaceuticalModel
import sdmed.back.model.sqlCSO.UserDataModel

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
			val setRowRet = model.setRows(x)
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
	fun pharmaUploadExcelParse(uploaderID: String, file: MultipartFile): MutableList<PharmaceuticalModel> {
		FExtensions.folderExist(FExcelParserType.PHARMA)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.PHARMA, uploaderID)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (!PharmaceuticalModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw UserDataFileUploadException()
		}

		val ret: MutableList<PharmaceuticalModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = PharmaceuticalModel()
			val setRowRet = model.setRows(x)
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
	fun hospitalUploadExcelParse(uploaderID: String, file: MultipartFile): MutableList<HospitalModel> {
		FExtensions.folderExist(FExcelParserType.HOSPITAL)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.HOSPITAL, uploaderID)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (!HospitalModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw UserDataFileUploadException()
		}

		val ret: MutableList<HospitalModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = HospitalModel()
			val setRowRet = model.setRows(x)
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

	fun correspondentUploadExcelParse(uploaderID: String, file: MultipartFile): MutableList<CorrespondentModel> {
		FExtensions.folderExist(FExcelParserType.CORRESPONDENT)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.CORRESPONDENT, uploaderID)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (!CorrespondentModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw UserDataFileUploadException()
		}

		val ret: MutableList<CorrespondentModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = CorrespondentModel()
			val setRowRet = model.setRows(x)
			if (setRowRet == null) {
				FExtensions.fileDelete(copiedLocation)
				throw UserDataFileUploadException(model.getErrorString())
			}
			if (setRowRet == false) {
				return@forEach
			}
			ret.add(model)
		}

		return ret
	}
}