package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.UserDataSubModel

@Repository
interface IUserSubDataRepository: JpaRepository<UserDataSubModel, Long> {
}