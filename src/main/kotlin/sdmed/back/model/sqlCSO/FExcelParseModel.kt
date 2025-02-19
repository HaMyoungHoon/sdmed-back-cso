package sdmed.back.model.sqlCSO

abstract class FExcelParseModel {
	abstract var dataCount: Int
	open fun findHeader(data: List<String>): Boolean {
		if (data.size < dataCount) {
			return false
		}
		for (index in 0 until dataCount) {
			if (data[index] != titleGet(index)) {
				return false
			}
		}
		return true
	}
	open fun rowSet(data: List<String>): Boolean? {
		if (data.size <= 1) {
			return false
		}
		return try {
			for ((index, value) in data.withIndex()) {
				indexSet(value, index)
			}
			if (errorCondition()) {
				return false
			}
			true
		} catch (_: Exception) {
			null
		}
	}
	abstract fun indexSet(data: String?, index: Int)
	abstract fun titleGet(index: Int): String
	abstract fun errorCondition(): Boolean
	open fun errorString() = ""
}