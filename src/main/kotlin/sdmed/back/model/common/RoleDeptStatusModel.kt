package sdmed.back.model.common

data class RoleDeptStatusModel(
	var roles: MutableList<UserRole> = mutableListOf(),
	var depts: MutableList<UserDept> = mutableListOf(),
	var status: UserStatus = UserStatus.None,
) {
}