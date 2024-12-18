package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.user.UserChildPKModel

@Repository
interface IUserChildPKRepository: JpaRepository<UserChildPKModel, String> {

	@Query("SELECT a.childPK FROM UserChildPKModel a " +
			"WHERE a.motherPK = :motherPK")
	fun selectAllByMotherPK(motherPK: String): List<String>

	@Query("WITH OnlyOneUserChildPKModel AS ( " +
			"SELECT motherPK, childPK, ROW_NUMBER() OVER (PARTITION BY a.motherPK ORDER BY (SELECT NULL)) AS RowNum " +
			"FROM UserChildPKModel a " +
			"WHERE motherPK IN (:motherPKInString)) " +
			"SELECT b.childPK FROM OnlyOneUserChildPKModel b " +
			"WHERE RowNum = 1", nativeQuery = true)
	fun selectAllByMotherPKInOnlyOne(motherPKInString: String): List<String>
}