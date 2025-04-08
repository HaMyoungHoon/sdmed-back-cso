package sdmed.back.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sdmed.back.service.*
import sdmed.back.service.extra.ExtraEDIDueDateService
import sdmed.back.service.extra.ExtraEDIListService
import sdmed.back.service.extra.ExtraEDIRequestService
import sdmed.back.service.extra.ExtraEDIService
import sdmed.back.service.extra.ExtraMedicinePriceListService
import sdmed.back.service.extra.ExtraMedicineService
import sdmed.back.service.extra.ExtraMyInfoService
import sdmed.back.service.intra.IntraEDIListService
import sdmed.back.service.intra.IntraEDIService

@Configuration
open class FServiceConfig {
	@Bean open fun ipControlService() = IPControlService()
	@Bean open fun mqttService() = MqttService()
	@Bean open fun dashboardService() = DashboardService()
	@Bean open fun extraDashboardService() = ExtraDashboardService()
	@Bean open fun commonService() = CommonService()

	@Bean open fun userService() = UserService()
	@Bean open fun myInfoService() = MyInfoService()
	@Bean open fun userInfoService() = UserInfoService()
	@Bean open fun userMappingService() = UserMappingService()

	@Bean open fun medicineService() = MedicineService()
	@Bean open fun medicinePriceListService() = MedicinePriceListService()
	@Bean open fun medicineListService() = MedicineListService()

	@Bean open fun hospitalService() = HospitalService()
	@Bean open fun hospitalListService() = HospitalListService()
	@Bean open fun hospitalTempService() = HospitalTempService()

	@Bean open fun pharmaService() = PharmaService()
	@Bean open fun pharmaListService() = PharmaListService()

	@Bean open fun ediService() = EDIService()
	@Bean open fun ediDueDateService() = EDIDueDateService()
	@Bean open fun ediApplyDateService() = EDIApplyDateService()
	@Bean open fun ediUploadCheckService() = EDIUploadCheckService()

	@Bean open fun qnaService() = QnAService()
	@Bean open fun qnaListService() = QnAListService()

	@Bean open fun intraEDIService() = IntraEDIService()
	@Bean open fun intraEDIListService() = IntraEDIListService()

	@Bean open fun extraEDIService() = ExtraEDIService()
	@Bean open fun extraEDIListService() = ExtraEDIListService()
	@Bean open fun extraEDIRequestService() = ExtraEDIRequestService()
	@Bean open fun extraEDIDueDateService() = ExtraEDIDueDateService()

	@Bean open fun extraMedicineService() = ExtraMedicineService()
	@Bean open fun extraMedicinePriceListService() = ExtraMedicinePriceListService()

	@Bean open fun extraMyInfoService() = ExtraMyInfoService()
}