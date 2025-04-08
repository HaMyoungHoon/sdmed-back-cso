package sdmed.back.service.extra

import org.springframework.beans.factory.annotation.Autowired
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.advice.exception.NotValidOperationException
import sdmed.back.config.FExtensions
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.common.user.UserRoles
import sdmed.back.model.sqlCSO.edi.EDIPharmaDueDateModel
import sdmed.back.repository.extra.ExtraUserRelationRepository
import java.util.Date

class ExtraEDIDueDateService: ExtraEDIService() {
    @Autowired lateinit var extraUserRelationRepository: ExtraUserRelationRepository
    fun getEDIDueDateMyList(token: String, date: Date, isYear: Boolean = false): List<EDIPharmaDueDateModel> {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
            throw AuthenticationEntryPointException()
        }
        isLive(tokenUser)
        val year = FExtensions.parseDateTimeString(date, "yyyy") ?: throw NotValidOperationException()
        val month = FExtensions.parseDateTimeString(date, "MM") ?: throw NotValidOperationException()
        val myPharmaPK = extraUserRelationRepository.findAllByUserPK(tokenUser.thisPK).map { it.pharmaPK }

        val ret = if (isYear) ediPharmaDueDateRepository.selectAllByThisYearDueDate(year)
        else ediPharmaDueDateRepository.selectAllByThisYearMonthDueDate(year, month)
        return ret.filter { it.pharmaPK in myPharmaPK }
    }
    fun getEDIDueDateRangeMyList(token: String, startDate: Date, endDate: Date): List<EDIPharmaDueDateModel> {
        isValid(token)
        val tokenUser = getUserDataByToken(token)
        if (!haveRole(tokenUser, UserRoles.of(UserRole.Admin, UserRole.CsoAdmin, UserRole.Employee, UserRole.BusinessMan))) {
            throw AuthenticationEntryPointException()
        }
        isLive(tokenUser)
        val myPharmaPK = extraUserRelationRepository.findAllByUserPK(tokenUser.thisPK).map { it.pharmaPK }
        val queryDate = FExtensions.getStartEndQueryDate(startDate, endDate)
        return ediPharmaDueDateRepository.selectAllByThisYearMonthRangeDueDate(queryDate.first, queryDate.second).filter { it.pharmaPK in myPharmaPK }
    }
}