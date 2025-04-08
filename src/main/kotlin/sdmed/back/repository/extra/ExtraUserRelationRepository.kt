package sdmed.back.repository.extra

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import sdmed.back.model.sqlCSO.extra.ExtraEDIHosBuffModel
import sdmed.back.model.sqlCSO.extra.ExtraEDIMedicineBuffModel
import sdmed.back.model.sqlCSO.extra.ExtraEDIPharmaBuffModel
import sdmed.back.model.sqlCSO.user.UserRelationModel

interface ExtraUserRelationRepository: JpaRepository<UserRelationModel, String> {
    fun findAllByUserPK(userPK: String): List<UserRelationModel>
    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraEDIHosBuffModel(a.thisPK, a.orgName) FROM HospitalModel a " +
            "LEFT JOIN UserRelationModel b ON a.thisPK = b.hosPK " +
            "WHERE a.inVisible = false AND b.userPK = :userPK " +
            "ORDER BY a.orgName ASC ")
    fun selectAllMyHospital(userPK: String): List<ExtraEDIHosBuffModel>
    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraEDIPharmaBuffModel(a.thisPK, '', a.code, a.orgName) " +
            "FROM PharmaModel a " +
            "WHERE a.inVisible = :inVisible " +
            "ORDER BY a.orgName ASC")
    fun selectAllByInvisible(inVisible: Boolean = false): List<ExtraEDIPharmaBuffModel>
    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraEDIPharmaBuffModel(a.thisPK, b.hosPK, a.code, a.orgName) " +
            "FROM PharmaModel a " +
            "LEFT JOIN UserRelationModel b ON a.thisPK = b.pharmaPK " +
            "WHERE a.inVisible = false AND b.userPK = :userPK AND b.hosPK = :hosPK " +
            "AND (b.hosPK, a.thisPK) NOT IN (" +
            "SELECT d.hospitalPK, c.pharmaPK FROM EDIUploadPharmaModel c " +
            "LEFT JOIN EDIUploadModel d ON c.ediPK = d.thisPK " +
            "WHERE d.userPK = :userPK AND d.year = :year AND d.month = :month AND c.ediState != 2) " +
            "ORDER BY a.orgName ASC ")
    fun selectAllMyPharmaAble(userPK: String, hosPK: String, year: String, month: String): List<ExtraEDIPharmaBuffModel>
    @Query("SELECT new sdmed.back.model.sqlCSO.extra.ExtraEDIPharmaBuffModel(a.thisPK, b.hosPK, a.code, a.orgName) " +
            "FROM PharmaModel a " +
            "LEFT JOIN UserRelationModel b ON a.thisPK = b.pharmaPK " +
            "WHERE a.inVisible = false AND b.userPK = :userPK AND b.hosPK IN (:hosPK) " +
            "AND NOT EXISTS (" +
            "SELECT 1 FROM EDIUploadPharmaModel c " +
            "LEFT JOIN EDIUploadModel d ON c.ediPK = d.thisPK " +
            "WHERE d.userPK = :userPK AND d.year = :year AND d.month = :month AND c.ediState != 2 AND d.hospitalPK = b.hosPK AND c.pharmaPK = a.thisPK) " +
            "ORDER BY a.orgName ASC ")
    fun selectAllMyPharmaAbleIn(userPK: String, hosPK: List<String>, year: String, month: String): List<ExtraEDIPharmaBuffModel>
    @Query(
        "SELECT new sdmed.back.model.sqlCSO.extra.ExtraEDIMedicineBuffModel(a.thisPK, a.code, c.orgName, a.orgName, b.pharmaPK, b.hosPK) FROM MedicineModel a " +
                "LEFT JOIN UserRelationModel b ON a.thisPK = b.medicinePK " +
                "LEFT JOIN PharmaModel c ON b.pharmaPK = c.thisPK " +
                "WHERE a.inVisible = false AND b.userPK = :userPK AND b.hosPK = :hosPK " +
                "ORDER BY a.orgName ASC ")
    fun selectAllMyMedicine(userPK: String, hosPK: String): List<ExtraEDIMedicineBuffModel>

    @Query(
        "SELECT new sdmed.back.model.sqlCSO.extra.ExtraEDIMedicineBuffModel(a.thisPK, a.code, c.orgName, a.orgName, b.pharmaPK, b.hosPK) FROM MedicineModel a " +
                "LEFT JOIN UserRelationModel b ON a.thisPK = b.medicinePK " +
                "LEFT JOIN PharmaModel c ON b.pharmaPK = c.thisPK " +
                "WHERE a.inVisible = false AND b.userPK = :userPK AND b.hosPK IN (:hosPK) " +
                "ORDER BY a.orgName ASC ")
    fun selectAllMyMedicineIn(userPK: String, hosPK: List<String>): List<ExtraEDIMedicineBuffModel>
}