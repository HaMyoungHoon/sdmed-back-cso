package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.CorrespondentModel

@Repository
interface ICorrespondentRepository: JpaRepository<CorrespondentModel, Long> {

	@Query("", nativeQuery = true)
	fun findCorrespondentListSearch(taxpayerNumber: String, innerName: String)
}