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
			"SELECT motherPK, ROW_NUMBER() OVER (PARTITION BY a.motherPK ODER BY (SELECT NULL)) AS RowNum " +
			"FROM UserChildPKModel " +
			"WHERE motherPK IN :motherPKInString) " +
			"SELECT childPK FROM OnlyOneUserChildPKModel " +
			"WHERE RowNum = 1", nativeQuery = true)
	fun selectAllByMotherPKInOnlyOne(motherPKInString: String): List<String>
}