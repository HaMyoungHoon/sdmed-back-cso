package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import sdmed.back.advice.exception.AuthenticationEntryPointException
import sdmed.back.config.FServiceBase
import sdmed.back.config.jpa.CSOJPAConfig
import sdmed.back.model.common.user.UserRole
import sdmed.back.model.sqlCSO.IPBlockModel
import sdmed.back.repository.sqlCSO.IIPBlockRepository

open class IPControlService: FServiceBase() {
	@Autowired lateinit var ipBlockRepository: IIPBlockRepository


	fun getIPBlockModel() = ipBlockRepository.findAll()
	fun getIPBlockModel(ipAddr: String) = ipBlockRepository.findByIpAddr(ipAddr)
	fun getIPBlockModelList(ipAddr: String) = ipBlockRepository.selectLikeIpAddr(ipAddr)
	@Transactional(CSOJPAConfig.TRANSACTION_MANAGER)
	open fun addIPBlockModelForController(token: String, ipBlockModel: IPBlockModel): IPBlockModel {
		isAdmin(token)
		if (getIPBlockModel(ipBlockModel.ipAddr) != null) {
			return ipBlockModel
		}

		return ipBlockRepository.save(ipBlockModel)
	}
	@Transactional(CSOJPAConfig.TRANSACTION_MANAGER)
	open fun addIPBlockModel(ipBlockModel: IPBlockModel): IPBlockModel {
		if (getIPBlockModel(ipBlockModel.ipAddr) != null) {
			return ipBlockModel
		}

		return ipBlockRepository.save(ipBlockModel)
	}
	@Transactional(CSOJPAConfig.TRANSACTION_MANAGER)
	open fun setIPBlockModel(ipAddr: String, block: Boolean): IPBlockModel {
		val model = getIPBlockModel(ipAddr) ?: return IPBlockModel()
		model.isBlock = block
		return ipBlockRepository.save(model)
	}
	@Transactional(CSOJPAConfig.TRANSACTION_MANAGER)
	open fun setIPBlockModelForController(token: String, ipAddr: String, block: Boolean): IPBlockModel {
		isAdmin(token)
		val model = getIPBlockModel(ipAddr) ?: return IPBlockModel()
		model.isBlock = block
		return ipBlockRepository.save(model)
	}

	fun isAdmin(token: String?, notAdminThrow: Boolean = true): Boolean =
		token?.let { x ->
			val user = getUserDataByToken(x)
			if (UserRole.fromFlag(user.role).contains(UserRole.Admin)) true
			else if (notAdminThrow) throw AuthenticationEntryPointException()
			else false
		} ?: if (notAdminThrow) throw AuthenticationEntryPointException() else false
}