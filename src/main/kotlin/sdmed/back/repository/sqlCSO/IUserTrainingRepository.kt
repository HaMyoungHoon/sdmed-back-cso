package sdmed.back.repository.sqlCSO

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import sdmed.back.model.sqlCSO.user.UserTrainingModel
import java.util.Date

@Repository
interface IUserTrainingRepository: JpaRepository<UserTrainingModel, String> {
    fun findAllByUserPKOrderByTrainingDateDesc(userPK: String): List<UserTrainingModel>
    fun findAllByUserPKInOrderByTrainingDateDesc(userPKs: List<String>): List<UserTrainingModel>
    fun findByUserPKAndTrainingDate(userPK: String, trainingDate: Date): UserTrainingModel?

    @Query("WITH RankedUserTrainingModel AS (" +
            "SELECT *, ROW_NUMBER() OVER (PARTITION BY userPK ORDER BY trainingDate DESC) AS RN FROM UserTrainingModel) " +
            "SELECT * FROM RankedUserTrainingModel AS UserTrainingModel WHERE RN = 1 AND userPK IN (:userPKs) " +
            "ORDER BY trainingDate DESC", nativeQuery = true)
    fun selectAllByRecentDataUserPKIn(userPKs: List<String>): List<UserTrainingModel>
}