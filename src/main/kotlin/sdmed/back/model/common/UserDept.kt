package sdmed.back.model.common

import java.util.*

enum class UserDept(var flag: Int, var desc: String) {
	None(0, "미지정"),
	Admin(1, "슈퍼관리자"),
	CsoAdmin(Admin.flag.shl(1), "관리자"),
	Employee(Admin.flag.shl(2), "직원"),
	Quitter(Admin.flag.shl(3), "퇴사자"),
	TaxPayer(Admin.flag.shl(4), "사업자"),
	Personal(Admin.flag.shl(5), "개인"),
	Terminate(Admin.flag.shl(6), "계약종료");

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