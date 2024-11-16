package sdmed.back.config

import org.apache.poi.openxml4j.opc.OPCPackage
import org.apache.poi.ss.util.CellReference
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable
import org.apache.poi.xssf.eventusermodel.XSSFReader
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler
import org.apache.poi.xssf.usermodel.XSSFComment
import org.apache.xmlbeans.XmlOptions
import org.apache.xmlbeans.impl.common.SAXHelper
import org.xml.sax.InputSource
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.net.URI

class ExcelSheetHandler: XSSFSheetXMLHandler.SheetContentsHandler {
	var currentCol: Int = - 1
	var currentRowNum: Int = 0
	var filePath: String = ""
	var rows: MutableList<MutableList<String>> = arrayListOf()
	var row: MutableList<String> = arrayListOf()
	var header: MutableList<String> = arrayListOf()

	companion object {
		fun readExcel(fileURI: URI, sheetIndex: Int = 0) = getSheetHandler(File(fileURI), sheetIndex)
		fun readExcel(file: File, sheetIndex: Int = 0) = getSheetHandler(file, sheetIndex)

		private fun getSheetHandler(file: File, sheetIndex: Int = 0): ExcelSheetHandler {
			val sheetHandler = ExcelSheetHandler()
			val opc = OPCPackage.open(FileInputStream(file))
			val reader = XSSFReader(opc)
			val stringTable = ReadOnlySharedStringsTable(opc)
			for (i in 0 until sheetIndex) {
				if (!reader.sheetsData.hasNext()) {
					opc.close()
					return sheetHandler
				}
				reader.sheetsData.next()
			}

			if (!reader.sheetsData.hasNext()) {
				opc.close()
				return sheetHandler
			}

			val inputStream : InputStream = reader.sheetsData.next()
			val inputSource = InputSource(inputStream)
			val handle = XSSFSheetXMLHandler(reader.stylesTable, stringTable, sheetHandler, false)
			val xmlReader = SAXHelper.newXMLReader(XmlOptions())
			xmlReader.contentHandler = handle
			xmlReader.parse(inputSource)
			inputStream.close()
			opc.close()
			return sheetHandler
		}
	}

	override fun startRow(rowNum: Int) {
		this.currentCol = -1
		this.currentRowNum = rowNum
	}

	override fun endRow(rowNum: Int) {
		if (rowNum == 0) {
			header = ArrayList(row)
		} else {
			if (row.size < header.size) {
				for (i in row.size until header.size)
					row.add("")
			}
			rows.add(ArrayList(row))
		}
		row.clear()
	}
	override fun cell(cellReference: String?, formattedValue: String?, comment: XSSFComment?) {
		val col = CellReference(cellReference).col
		val emptyCol = col - currentCol - 1

		for (i in 0 until emptyCol)
			row.add("")
		currentCol = col.toInt()
		row.add(formattedValue ?: "")
	}
	override fun headerFooter(text: String?, isHeader: Boolean, tagName: String?) {
	}
}