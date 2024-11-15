package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.UserDataModel

@Repository
interface IUserDataRepository: JpaRepository<UserDataModel, Long> {
	fun findById(id: String): UserDataModel?
}