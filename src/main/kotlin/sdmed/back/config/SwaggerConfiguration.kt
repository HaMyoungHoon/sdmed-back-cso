package sdmed.back.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@OpenAPIDefinition(servers = [io.swagger.v3.oas.annotations.servers.Server(url = "/", description = "defaultPath")])
@Configuration
open class SwaggerConfiguration {
	@Value(value = "\${str.version}") lateinit var strVersion: String
	@Value(value = "\${str.profile}") lateinit var strprofile: String
	@Bean
	open fun openApi(): OpenAPI = OpenAPI()
		.info(v1Info())
		.servers(mutableListOf<Server?>().apply {
			add(testHttpsServer())
			add(testHttpServer())
		})
	private fun v1Info() = Info()
		.title("$strprofile test mhha")
		.description("테스트 하려고 만든 거")
		.version(strVersion)
		.license(License())
		.contact(Contact())
	private fun testHttpsServer() = Server().apply {
		url = "https://back.cso.mhha.kr"
		description = "https"
	}
	private fun testHttpServer() = Server().apply {
		url = "http://back.cso.mhha.kr"
		description = "http"
	}
}