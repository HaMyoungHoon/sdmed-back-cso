package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.LogModel

@Repository
interface ILogRepository: JpaRepository<LogModel, Long> {
}