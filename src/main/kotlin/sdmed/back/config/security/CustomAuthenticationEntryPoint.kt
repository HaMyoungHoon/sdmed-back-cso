package sdmed.back.config.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {
	override fun commence(request : HttpServletRequest, response : HttpServletResponse, authException : AuthenticationException) {
		response.sendRedirect("/exception/entryPoint")
	}
}