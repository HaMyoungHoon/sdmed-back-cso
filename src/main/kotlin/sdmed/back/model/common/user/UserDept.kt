package sdmed.back.model.common.user

import java.util.*

enum class UserDept(var flag: Int, var desc: String) {
	None(0, "미지정"),
	Employee(1, "직원"),
	Quitter(Employee.flag.shl(1), "퇴사자"),
	TaxPayer(Employee.flag.shl(2), "사업자"),
	Personal(Employee.flag.shl(3), "개인"),
	Terminate(Employee.flag.shl(4), "계약종료");

	infix fun and(rhs: UserDept) = EnumSet.of(this, rhs)
	companion object {
		infix fun UserDepts.allOf(rhs: UserDepts) = this.containsAll(rhs)
		infix fun UserDepts.and(rhs: UserDept) = EnumSet.of(rhs, *this.toTypedArray())
		infix fun UserDepts.flag(rhs: UserDept): Int {
			val buff = this.toTypedArray()
			var ret = 0
			for (i in buff) {
				ret = ret or i.flag
			}
			ret = ret and rhs.flag
			return ret
		}
		fun UserDepts.getFlag(): Int {
			val buff = this.toTypedArray()
			var ret = 0
			for (i in buff) {
				ret = ret or i.flag
			}

			return ret
		}
		fun fromInt(flag: Int) = UserDept.entries.firstOrNull { it.flag == flag }
		fun fromFlag(flag: Int): UserDepts {
			var ret = EnumSet.of(None)
			UserDept.entries.forEach {
				if (it.flag and flag != 0) {
					ret = ret.and(it)
				}
			}
			return ret
		}
		fun parseString(data: String?) = entries.find { it.desc == data } ?: None
	}
}
internal typealias UserDepts = EnumSet<UserDept>