package sdmed.back.service

import org.springframework.beans.factory.annotation.Autowired
import sdmed.back.config.FServiceBase
import sdmed.back.repository.sqlCSO.*

open class EDIService: FServiceBase() {
	@Autowired lateinit var ediUploadRepository: IEDIUploadRepository
	@Autowired lateinit var ediApplyDateRepository: IEDIApplyDateRepository
	@Autowired lateinit var ediPharmaDueDateRepository: IEDIPharmaDueDateRepository

	@Autowired lateinit var hospitalRepository: IHospitalRepository
	@Autowired lateinit var pharmaRepository: IPharmaRepository
}