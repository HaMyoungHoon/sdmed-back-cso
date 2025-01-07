package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.user.UserFileModel
import sdmed.back.model.sqlCSO.user.UserFileType

@Repository
interface IUserFileRepository: JpaRepository<UserFileModel, String> {
	fun findAllByUserPK(userPK: String): List<UserFileModel>

	fun findByUserPKAndUserFileType(userPK: String, userFileType: UserFileType): UserFileModel?
}