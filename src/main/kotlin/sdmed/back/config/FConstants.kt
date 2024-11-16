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

	const val NOT_FOUND_VALUE_OR_FORMAT = "값이 없거나 형식에 맞지 않습니다."

	const val USER_MODEL_ID = "아이디"
	const val USER_MODEL_PW = "비밀번호"
	const val USER_MODEL_NAME = "이름"
	const val USER_MODEL_MAIL = "이름"
	const val USER_MODEL_PHONE = "이름"
	const val USER_MODEL_ROLE = "이름"
	const val USER_MODEL_DEPT = "이름"
	const val USER_MODEL_STATUS = "이름"

	const val CO_MODEL_CODE = "거래처코드"
	const val CO_MODEL_TAX_PAYER = "사업자번호"
	const val CO_MODEL_ORG_NAME = "사업자원어명"
	const val CO_MODEL_INNER_NAME = "사업자내부명"
	const val CO_MODEL_OPEN_DATE = "거래개시일"
	const val CO_MODEL_CLOSE_DATE = "거래종료일"
	const val CO_MODEL_OWNER_NAME = "대표자명"
	const val CO_MODEL_ZIP_CODE = "우편번호"
	const val CO_MODEL_ADDRESS = "주소"
	const val CO_MODEL_ADDRESS_DETAIL = "상세주소"
	const val CO_MODEL_BIZ_TYPE = "업태"
	const val CO_MODEL_BIZ_ITEM = "종목"
	const val CO_MODEL_PHONE = "전화번호"
	const val CO_MODEL_FAX = "팩스번호"
	const val CO_MODEL_ETC1 = "비고1"
	const val CO_MODEL_ETC2 = "비고2"
}