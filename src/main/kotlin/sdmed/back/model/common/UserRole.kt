package sdmed.back.model.common

import java.util.*

enum class UserRole(var flag: Int, var desc: String) {
	None(0, "미지정"),
	Admin(1, "슈퍼관리자"),
	CsoAdmin(Admin.flag.shl(1), "관리자"),
	Employee(Admin.flag.shl(2), "직원"),
	BusinessMan(Admin.flag.shl(3), "영업"),
	PasswordChanger(Admin.flag.shl(4), "PasswordChanger"),
	StatusChanger(Admin.flag.shl(5), "StatusChanger"),
	RoleChanger(Admin.flag.shl(6), "RoleChanger"),
	DeptChanger(Admin.flag.shl(7), "DeptChanger"),
	UserChildChanger(Admin.flag.shl(8), "UserChildChanger"),
	UserFileUploader(Admin.flag.shl(9), "UserFileUploader"),
	CorrespondentFileUploader(Admin.flag.shl(10), "CorrespondentFileUploader"),
	PharmaFileUploader(Admin.flag.shl(11), "PharmaFileUploader"),
	HospitalFileUploader(Admin.flag.shl(12), "HospitalFileUploader"),
	MedicineFileUploader(Admin.flag.shl(13), "MedicineFileUploader");

	infix fun and(rhs: UserRole) = EnumSet.of(this, rhs)
	fun toS() = EnumSet.of(this)
	companion object {
		infix fun UserRoles.allOf(rhs: UserRoles) = this.containsAll(rhs)
		infix fun UserRoles.and(rhs: UserRole) = EnumSet.of(rhs, *this.toTypedArray())
		infix fun UserRoles.flag(rhs: UserRole): Int {
			val buff = this.toTypedArray()
			var ret = 0
			for (i in buff) {
				ret = ret or i.flag
			}
			ret = ret and rhs.flag
			return ret
		}
		fun UserRoles.getFlag(): Int {
			val buff = this.toTypedArray()
			var ret = 0
			for (i in buff) {
				ret = ret or i.flag
			}

			return ret
		}
		fun fromInt(flag: Int) = UserRole.entries.firstOrNull { it.flag == flag }
		fun fromFlag(flag: Int): UserRoles {
			var ret = EnumSet.of(None)
			UserRole.entries.forEach {
				if (it.flag and flag != 0) {
					ret = ret.and(it)
				}
			}
			return ret
		}
		fun parseString(data: String?) = entries.find { it.desc == data } ?: None
	}
}

internal typealias UserRoles = EnumSet<UserRole>