package sdmed.back.model.sqlCSO

import jakarta.persistence.*
import org.springframework.web.util.ContentCachingRequestWrapper
import sdmed.back.config.FConstants
import java.sql.Timestamp
import java.util.*

@Entity
data class IPLogModel(
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	var thisIndex: Long = 0,
	@Column
	var dateTime: Timestamp = Timestamp(Date().time),
	@Column(columnDefinition = "nvarchar(300)")
	var method: String = "",
	@Column
	var requestUri: String = "",
	@Column(columnDefinition = "nvarchar(100)")
	var forwardedFor: String? = null,
	@Column(columnDefinition = "nvarchar(100)")
	var proxyClientIp: String? = null,
	@Column(columnDefinition = "nvarchar(100)")
	var wlProxyClientIp: String? = null,
	@Column(columnDefinition = "nvarchar(100)")
	var httpClientIp: String? = null,
	@Column(columnDefinition = "nvarchar(100)")
	var httpForwardedFor: String? = null,
	@Column(columnDefinition = "nvarchar(100)")
	var remoteAddr: String = "255.255.255.255",
	@Column(columnDefinition = "nvarchar(100)")
	var localAddr: String = "255.255.255.255",
	@Column(columnDefinition = "nvarchar(200)")
	var serverName: String? = null,
	@Column
	var serverPort: Int = 0,
	@Column(columnDefinition = "nvarchar(200)")
	var localName: String? = null,
	@Column
	var localPort: Int = 0,
) {
	fun setRequestWrapper(wrapper: ContentCachingRequestWrapper): IPLogModel {
		this.method = wrapper.method
		this.requestUri = wrapper.requestURI
		this.forwardedFor = wrapper.getHeader(FConstants.HEADER_FORWARDED_FOR)
		this.proxyClientIp = wrapper.getHeader(FConstants.HEADER_PROXY_CLIENT_IP)
		this.wlProxyClientIp = wrapper.getHeader(FConstants.HEADER_WL_PROXY_CLIENT_IP)
		this.httpClientIp = wrapper.getHeader(FConstants.HEADER_HTTP_CLIENT_IP)
		this.httpForwardedFor = wrapper.getHeader(FConstants.HEADER_HTTP_FORWARDED_FOR)
		this.remoteAddr = wrapper.remoteAddr
		this.localAddr = wrapper.localAddr
		this.serverName = wrapper.serverName
		this.serverPort = wrapper.serverPort
		this.localName = wrapper.localName
		this.localPort = wrapper.localPort

		return this
	}
	fun ipContains(ip: String): Boolean {
		if (this.forwardedFor?.contains(ip) == true) return true
		if (this.proxyClientIp?.contains(ip) == true) return true
		if (this.remoteAddr.contains(ip) == true) return true


		return false
	}
}