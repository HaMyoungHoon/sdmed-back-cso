package sdmed.back.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import sdmed.back.service.*

@Configuration
class FServiceConfig {
	@Bean fun dashboardService() = DashboardService()

	@Bean fun userService() = UserService()
	@Bean fun myInfoService() = MyInfoService()
	@Bean fun userInfoService() = UserInfoService()
	@Bean fun userMappingService() = UserMappingService()

	@Bean fun medicineService() = MedicineService()
	@Bean fun medicinePriceListService() = MedicinePriceListService()
	@Bean fun medicineListService() = MedicineListService()

	@Bean fun hospitalService() = HospitalService()
	@Bean fun hospitalListService() = HospitalListService()

	@Bean fun pharmaService() = PharmaService()
	@Bean fun pharmaListService() = PharmaListService()
}