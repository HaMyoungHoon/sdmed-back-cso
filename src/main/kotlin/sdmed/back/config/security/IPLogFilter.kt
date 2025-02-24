package sdmed.back.config.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import sdmed.back.model.sqlCSO.IPLogModel
import sdmed.back.service.CommonService

class IPLogFilter(private var commonService: CommonService): OncePerRequestFilter() {
	override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
		request.let { x ->
			commonService.addIPLog(IPLogModel().apply {
				setRequestWrapper(ContentCachingRequestWrapper(x))
			})
			filterChain.doFilter(request, response)
		}
	}
}