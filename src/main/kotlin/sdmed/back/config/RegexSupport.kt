package sdmed.back.config

class RegexSupport {
	companion object {
		val REGEX_NUM: Regex = Regex("\\d+(\\.?\\d*)\$")
		val REGEX_yyyyMMdd: Regex = Regex("(^\\d{4}(-)\\d{2}(-)\\d{2})")
		val REGEX_ddMMyyyy: Regex = Regex("(^\\d{2}(-)\\d{2}(-)\\d{4})")
		val REGEX_Mdyy: Regex = Regex("(^\\d(/)\\d(/)\\d{2})")
		val REGEX_Mddyy: Regex = Regex("(^\\d(/)\\d{2}(/)\\d{2})")
		val REGEX_MMdyy: Regex = Regex("(^\\d{2}(/)\\d(/)\\d{2})")
		val REGEX_MMddyy: Regex = Regex("(^\\d{2}(/)\\d{2}(/)\\d{2})")
	}
}
