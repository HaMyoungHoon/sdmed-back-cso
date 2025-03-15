package sdmed.back.config

import org.apache.catalina.connector.Connector
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class ConnectorConfig {
	@Value(value = "\${spring.http-port}") var httpPort: Int = 25801
	@Bean
	open fun servletContainer() = TomcatServletWebServerFactory().apply {
		this.addAdditionalTomcatConnectors(createSslConnector())
	}

	private fun createSslConnector() = Connector("org.apache.coyote.http11.Http11NioProtocol").apply {
		this.scheme = "http"
		this.secure = false
		this.port = httpPort
	}
}