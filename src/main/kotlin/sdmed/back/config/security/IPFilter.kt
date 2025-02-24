package sdmed.back.config.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import sdmed.back.service.IPControlService

class IPFilter(private var ipControlService: IPControlService): OncePerRequestFilter() {
	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
		filterChain.doFilter(request, response)
	}
}