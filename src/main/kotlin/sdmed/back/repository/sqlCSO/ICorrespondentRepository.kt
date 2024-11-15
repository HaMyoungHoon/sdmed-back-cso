package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.CorrespondentModel

@Repository
interface ICorrespondentRepository: JpaRepository<CorrespondentModel, Long> {

	@Query("SELECT * from correspondentModel" +
			"WHERE thisIndex IN :thisIndex", nativeQuery = true)
	fun findCorrespondentListIDs(thisIndex: List<Long>): List<CorrespondentModel>
	@Query("SELECT * from correspondentModel" +
			"WHERE taxpayerNumber LIKE %:taxpayerNumber% OR" +
			"innerName LIKE %:innerName%", nativeQuery = true)
	fun findCorrespondentListSearch(taxpayerNumber: String, innerName: String): List<CorrespondentModel>
}