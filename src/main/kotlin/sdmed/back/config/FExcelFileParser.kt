package sdmed.back.config

import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import sdmed.back.advice.exception.UserDataFileUploadException
import sdmed.back.model.sqlCSO.CorrespondentModel
import sdmed.back.model.sqlCSO.UserDataModel

@Component
class FExcelFileParser {
	fun userUploadExcelParse(file: MultipartFile): MutableList<UserDataModel> {
		FExtensions.folderExist(FExcelParserType.USER)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.USER)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (UserDataModel().findHeader(excelSheetHandler.header)) {
			FExtensions.fileDelete(copiedLocation)
			throw UserDataFileUploadException()
		}

		val ret: MutableList<UserDataModel> = mutableListOf()
		excelSheetHandler.rows.forEach { x ->
			val model = UserDataModel()
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

	fun correspondentUploadExcelParse(file: MultipartFile): MutableList<CorrespondentModel> {
		FExtensions.folderExist(FExcelParserType.CORRESPONDENT)
		val copiedLocation = FExtensions.fileCopy(file, FExcelParserType.CORRESPONDENT)
		val excelSheetHandler = ExcelSheetHandler.readExcel(copiedLocation.toFile())

		if (CorrespondentModel().findHeader(excelSheetHandler.header)) {
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