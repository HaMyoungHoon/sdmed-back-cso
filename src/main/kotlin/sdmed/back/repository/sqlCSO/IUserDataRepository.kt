package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.user.UserDataModel

@Repository
interface IUserDataRepository: JpaRepository<UserDataModel, String> {
	fun findAllByOrderByNameDesc(): List<UserDataModel>
	fun findAllByOrderByNameDesc(pageable: Pageable): Page<UserDataModel>
	fun findAllByIdIn(ids: List<String>): List<UserDataModel>
	fun findByThisPK(thisPK: String): UserDataModel?
	fun findAllByThisPKIn(thisPK: List<String>): List<UserDataModel>

	@Query("SELECT a FROM UserDataModel a " +
			"WHERE a.id = :id")
	fun selectById(id: String): UserDataModel?
	@Query("SELECT a FROM UserDataModel a " +
			"WHERE a.thisPK = :thisPK")
	fun selectByPK(thisPK: String): UserDataModel?
	@Query("SELECT * FROM UserDataModel " +
			"WHERE dept & :flag > 0 " +
			"ORDER BY name Asc", nativeQuery = true)
	fun selectWhereDeptOrderByNameAsc(flag: Int): List<UserDataModel>
	@Query("SELECT * FROM UserDataModel " +
			"WHERE role & :flag > 0 " +
			"ORDER BY name Asc", nativeQuery = true)
	fun selectWhereRoleOrderByNameAsc(flag: Int): List<UserDataModel>

	@Query("SELECT * FROM UserDataModel " +
			"WHERE role & :role > 0 And status = :status " +
			"ORDER BY name Asc", nativeQuery = true)
	fun selectByRoleAndStatusOrderByNameAsc(role: Int, status: Int): List<UserDataModel>

	@Query("SELECT a FROM UserDataModel a " +
			"WHERE NOT EXISTS ( " +
			"SELECT 1 FROM UserChildPKModel b " +
			"WHERE a.thisPK = b.motherPK)" +
			"AND a.thisPK != :thisPK " +
			"AND a.thisPK NOT IN ( " +
			"SELECT b.childPK FROM UserChildPKModel b " +
			"WHERE b.motherPK = :thisPK)")
	fun selectAbleChild(thisPK: String): List<UserDataModel>
}