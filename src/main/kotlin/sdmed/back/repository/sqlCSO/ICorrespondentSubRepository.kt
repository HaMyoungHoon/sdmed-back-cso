package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.CorrespondentSubModel

@Repository
interface ICorrespondentSubRepository: JpaRepository<CorrespondentSubModel, Long> {
	@Query("SELECT * from correspondentSubModel" +
			"WHERE thisIndex IN (:thisIndex)", nativeQuery = true)
	fun findCorrespondentListIDs(thisIndex: List<Long>): List<CorrespondentSubModel>
}