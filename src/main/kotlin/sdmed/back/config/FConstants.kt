package sdmed.back.config

object FConstants {
	const val CLAIM_INDEX = "index"
	const val CLAIM_ID = "id"
	const val CLAIM_NAME = "name"
	const val CLAIM_JTI = "jti"
	const val CLAIM_ROLE = "role"
	const val CLAIM_DEPT = "dept"
	const val CLAIM_STATUS = "status"

	const val ASYNC_TASK_EXECUTOR = "asyncTaskExecutor"

	const val HTTP_MHHA = "http://*.mhha.kr"
	const val HTTPS_MHHA = "https://*.mhha.kr"

	const val HTTP_FRONT_1 = "http://angular.mhha.kr"
	const val HTTPS_FRONT_1 = "https://angular.mhha.kr"
	const val HTTP_FRONT_2 = "http://www.mhha.kr"
	const val HTTPS_FRONT_2 = "https://www.mhha.kr"
	const val HTTP_FRONT_3 = "http://mhha.kr"
	const val HTTPS_FRONT_3 = "https://mhha.kr"

	const val CONTENT_TYPE = "Content-Type"
	const val CONTENT_LENGTH = "Content-Length"
	const val CONTENT_RANGE = "Content-Range"
	const val ACCEPT_RANGES = "Accept-Ranges"
	const val VIDEO_CONTENT = "video/"
	const val BYTES = "bytes"
	const val CHUNK_SIZE = 314700L
	const val BYTE_RANGE = 1024

	const val HEADER_FORWARDED_FOR = "X-Forwarded-For"
	const val HEADER_PROXY_CLIENT_IP = "Proxy-Client-IP"
	const val HEADER_WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP"
	const val HEADER_HTTP_CLIENT_IP = "HTTP_CLIENT_IP"
	const val HEADER_HTTP_FORWARDED_FOR = "HTTP_X_FORWARDED_FOR"
}