package sdmed.back.repository.sqlCSO

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.LogModel
import sdmed.back.model.sqlCSO.LogViewModel

@Repository
interface ILogRepository: JpaRepository<LogModel, Long> {

	@Query("SELECT new sdmed.back.model.sqlCSO.LogViewModel(b.id, a.content, a.className, a.regDate) " +
			"FROM LogModel a " +
			"LEFT JOIN UserDataModel b on a.userPK = b.thisPK " +
			"ORDER BY a.regDate Desc")
	fun selectLogViewModel(pageable: Pageable): Page<LogViewModel>
}