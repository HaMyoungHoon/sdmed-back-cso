package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.UserDataModel

@Repository
interface IUserDataRepository: JpaRepository<UserDataModel, String> {
	fun findAllByOrderByNameDesc(): List<UserDataModel>
	fun findAllByOrderByNameDesc(pageable: Pageable): Page<UserDataModel>
	fun findAllByIdIn(ids: List<String>): List<UserDataModel>
	fun findByThisPK(thisPK: String): UserDataModel?
	fun findAllByUserData(userDataModel: UserDataModel): List<UserDataModel>

	@Query("SELECT * from userDataModel " +
			"WHERE id = :id", nativeQuery = true)
	fun selectById(id: String): UserDataModel?
	@Query("SELECT * FROM userDataModel" +
			"WHERE dept & :flag > 0" +
			"ORDER BY name Asc", nativeQuery = true)
	fun selectWhereDeptOrderByNameAsc(flag: Int): List<UserDataModel>
	@Query("SELECT * FROM userDataModel" +
			"WHERE role & :flag > 0" +
			"ORDER BY name Asc", nativeQuery = true)
	fun selectWhereRoleOrderByNameAsc(flag: Int): List<UserDataModel>
}