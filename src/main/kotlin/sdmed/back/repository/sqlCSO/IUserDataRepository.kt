package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.UserDataModel

@Repository
interface IUserDataRepository: JpaRepository<UserDataModel, Long> {
	fun findAllByOrderByNameDesc(): List<UserDataModel>
	fun findAllByOrderByNameDesc(pageable: Pageable): Page<UserDataModel>
	fun findById(id: String): UserDataModel?
	fun findAllByIdIn(ids: List<String>): List<UserDataModel>


	@Query("SELECT * FROM userDataModel" +
			"WHERE dept & :flag > 0" +
			"ORDER BY name Asc", nativeQuery = true)
	fun selectWhereDeptOrderByNameAsc(flag: Int): List<UserDataModel>
	@Query("SELECT * FROM userDataModel" +
			"WHERE role & :flag > 0" +
			"ORDER BY name Asc", nativeQuery = true)
	fun selectWhereRoleOrderByNameAsc(flag: Int): List<UserDataModel>
}