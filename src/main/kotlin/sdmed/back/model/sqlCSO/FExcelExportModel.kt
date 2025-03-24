package sdmed.back.model.sqlCSO

abstract class FExcelExportModel {
    abstract var dataCount: Int
    abstract fun indexGet(index: Int): String
    abstract fun titleGet(index: Int): String
}