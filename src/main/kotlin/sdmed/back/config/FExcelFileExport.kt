package sdmed.back.config

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Component
import sdmed.back.model.sqlCSO.user.UserMappingExcelModel
import java.io.File
import java.io.FileOutputStream
import java.net.URI

@Component
class FExcelFileExport {
    fun userMappingExcelExport(userID: String, tableModel: List<UserMappingExcelModel>): URI {
        val copiedLocation = FExtensions.fileCreate(FExcelParserType.USER_MAPPING, userID)
        val workBook = XSSFWorkbook()
        val sheet = workBook.createSheet("mapping")
        var colNo = 0
        var rowNo = 0
        var row = sheet.createRow(rowNo++)
        var cell = row.createCell(colNo++)

        val titleGetBuff = UserMappingExcelModel()
        for (index in 0 until titleGetBuff.dataCount) {
            cell.setCellValue(titleGetBuff.titleGet(index))
            cell = row.createCell(colNo++)
        }

        tableModel.forEach {
            colNo = 0
            row = sheet.createRow(rowNo++)
            for (i in 0 .. titleGetBuff.dataCount) {
                cell = row.createCell(colNo++)
                cell.setCellValue(it.indexGet(i))
            }
        }
        val filePath = copiedLocation
        val file = File(filePath.toUri())
        val fos = FileOutputStream(file)
        workBook.write(fos)
        return filePath.toUri()
    }
}