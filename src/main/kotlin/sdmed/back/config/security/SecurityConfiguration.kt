package sdmed.back.config.security

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication
import org.springframework.boot.autoconfigure.security.ConditionalOnDefaultWebSecurity
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.AuthorizationFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.header.HeaderWriterFilter
import sdmed.back.service.CommonService
import sdmed.back.service.IPControlService

@Configuration
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableWebSecurity
@ConditionalOnDefaultWebSecurity
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
class SecurityConfiguration {
	@Value(value = "\${str.profile}") lateinit var strProfile: String
	@Autowired lateinit var refererCheckFilter: RefererCheckFilter
	@Autowired lateinit var ipControlService: IPControlService
	@Autowired lateinit var commonService: CommonService
	@Bean
	@Throws(Exception::class)
	fun filterChain(http: HttpSecurity): SecurityFilterChain = http
		.csrf { it.disable() }
		.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
		.exceptionHandling {
			it.authenticationEntryPoint(CustomAuthenticationEntryPoint())
			it.accessDeniedHandler(CustomAccessDeniedHandler())
		}
		.addFilterBefore(refererCheckFilter.setStrProfile(strProfile), UsernamePasswordAuthenticationFilter::class.java)
		.addFilterBefore(IPFilter(ipControlService), HeaderWriterFilter::class.java)
		.addFilterAfter(IPLogFilter(commonService), AuthorizationFilter::class.java)
		.build()

	@Bean
	fun passwordEncoder(): PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
}