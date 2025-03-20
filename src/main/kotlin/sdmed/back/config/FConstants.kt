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

	const val HTTP_SD_MED = "http://*.sdmed.co.kr"
	const val HTTPS_SD_MED = "https://*.sdmed.co.kr"

	const val IP_OFFICE_1 = "112.161.25.114"
	const val IP_OFFICE_2 = "112.161.25.115"

	const val HTTP_FRONT_1 = "http://intra-cso.sdmed.co.kr"
	const val HTTPS_FRONT_1 = "https://intra-cso.sdmed.co.kr"
	const val HTTP_FRONT_2 = "http://extra-cso.sdmed.co.kr"
	const val HTTPS_FRONT_2 = "https://extra-cso.sdmed.co.kr"
	const val HTTP_BACK_1 = "http://back-cso.sdmed.co.kr/"
	const val HTTPS_BACK_1 = "https://back-cso.sdmed.co.kr/"

	const val REQUEST_MQTT = "/mqtt"
	const val REQUEST_COMMON = "/common"
	const val REQUEST_INTRA = "/intra"
	const val REQUEST_EXTRA = "/extra"

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

	const val MODEL_USER_ID = "아이디"
	const val MODEL_USER_PW = "비밀번호"
	const val MODEL_USER_NAME = "이름"
	const val MODEL_USER_MAIL = "메일"
	const val MODEL_USER_PHONE = "전화번호"
	const val MODEL_USER_ROLE = "권한"
	const val MODEL_USER_DEPT = "부서"
	const val MODEL_USER_STATUS = "상태"
	const val MODEL_USER_COMPANY_NAME = "사업자명"
	const val MODEL_USER_COMPANY_INNER_NAME = "사업자내부명"
	const val MODEL_USER_COMPANY_NUMBER = "사업자등록번호"
	const val MODEL_USER_COMPANY_OWNER = "대표자"
	const val MODEL_USER_COMPANY_ADDRESS = "사업장소재지"
	const val MODEL_USER_BANK_ACCOUNT = "은행계좌번호"
	const val MODEL_USER_CSO_REPORT_NUMBER = "신고번호"
	const val MODEL_USER_CONTRACT_DATE = "계약일"
	const val MODEL_USER_COUNT = 15
	const val MODEL_USER_INSERT_INTO = "INSERT INTO UserDataModel (thisPK, id, pw, name, mail, phoneNumber, role, dept, status, companyName, companyInnerName, companyNumber, companyOwner, companyAddress, bankAccount, csoReportNumber, contractDate, regDate) VALUES "
	const val MODEL_USER_CHILD_INSERT_INTO = "INSERT INTO UserChildPKModel (thisPK, motherPK, childPK) VALUES "
	const val MODEL_USER_CHILD_DELETE_BY_MOTHER_PK = "DELETE FROM UserChildPKModel WHERE motherPK =  "

	const val MODEL_CODE = "거래처코드"
	const val MODEL_ORG_NAME = "사업자원어명"
	const val MODEL_INNER_NAME = "사업자내부명"
	const val MODEL_OWNER_NAME = "대표자명"
	const val MODEL_TAX_PAYER = "사업자번호"
	const val MODEL_ADDRESS = "주소"
	const val MODEL_PHONE = "전화번호"
	const val MODEL_FAX = "팩스번호"
	const val MODEL_ZIP_CODE = "우편번호"
	const val MODEL_BIZ_TYPE = "업태"
	const val MODEL_BIZ_ITEM = "종목"
	const val MODEL_OPEN_DATE = "거래개시일"
	const val MODEL_RETROACTIVE_RULE = "소급기준"
	const val MODEL_INNER_SETTLEMENT_RULE = "내부정산기준"
	const val MODEL_OUTER_SETTLEMENT_RULE = "외부정산기준"
	const val MODEL_ETC1 = "비고1"
	const val MODEL_ETC2 = "비고2"
	const val MODEL_PHARMA_COUNT = 15
	const val MODEL_PHARMA_INSERT_INTO = "INSERT INTO PharmaModel (thisPK, code, orgName, innerName, ownerName, taxpayerNumber, address, phoneNumber, faxNumber, zipCode, openDate, retroactiveRule, innerSettlementRule, outerSettlementRule, etc1, etc2, imageUrl, inVisible) VALUES "

	const val MODEL_PHARMA_MEDICINE_RELATIONS_DELETE_WHERE_PHARMA_PK = "DELETE FROM PharmaMedicineRelationModel WHERE pharmaPK = "
	const val MODEL_PHARMA_MEDICINE_RELATIONS_INSERT_INTO = "INSERT INTO PharmaMedicineRelationModel (thisPK, pharmaPK, medicinePK) VALUES "

	const val MODEL_NURSING_HOME_NUMBER = "요양기관번호"
	const val MODEL_HOS_COUNT = 14
	const val MODEL_HOS_INSERT_INTO = "INSERT INTO HospitalModel (thisPK, code, orgName, innerName, ownerName, taxpayerNumber, address, phoneNumber, faxNumber, zipCode, businessType, businessItem, nursingHomeNumber, etc1, etc2, imageUrl, inVisible) VALUES "

	const val MODEL_MEDICINE_MAKER_NAME = "제조사"
	const val MODEL_MEDICINE_NAME = "제품명"
	const val MODEL_MEDICINE_INNER_NAME = "내부명"
	const val MODEL_MEDICINE_KD_CODE = "제품코드"
	const val MODEL_MEDICINE_CUSTOM_PRICE = "기준가"
	const val MODEL_MEDICINE_CHARGE = "요율"
	const val MODEL_MEDICINE_STANDARD = "규격"
	const val MODEL_MEDICINE_ETC1 = "비고1"
	const val MODEL_MEDICINE_MAIN_INGREDIENT_CODE = "성분코드"
	const val MODEL_MEDICINE_CODE = "코드"
	const val MODEL_MEDICINE_MAKER_CODE = "제조사코드"
	const val MODEL_MEDICINE_DIV = "제품구분"
	const val MODEL_MEDICINE_COUNT = 12
	const val MODEL_MEDICINE_INSERT_INTO = "INSERT INTO MedicineModel (thisPK, orgName, innerName, kdCode, customPrice, charge, standard, etc1, mainIngredientCode, code, makerCode, medicineDiv, inVisible) VALUES "

	const val MODEL_MEDICINE_PRICE_INDEX = "연번"
	const val MODEL_MEDICINE_PRICE_METHOD = "투여"
	const val MODEL_MEDICINE_PRICE_CLASSIFY = "분류"
	const val MODEL_MEDICINE_PRICE_INGREDIENT_CODE = "주성분코드"
	const val MODEL_MEDICINE_PRICE_KD_CODE = "제품코드"
	const val MODEL_MEDICINE_PRICE_NAME = "제품명"
	const val MODEL_MEDICINE_PRICE_PHARMA_NAME = "업체명"
	const val MODEL_MEDICINE_PRICE_STANDARD = "규격"
	const val MODEL_MEDICINE_PRICE_UNIT = "단위"
	const val MODEL_MEDICINE_PRICE_MAX_PRICE = "상한금액"
	const val MODEL_MEDICINE_PRICE_GENERAL = "전일"
	const val MODEL_MEDICINE_PRICE_ETC = "비고"
	const val MODEL_MEDICINE_PRICE_ANCESTOR_CODE = "목록정비전코드"
	const val MODEL_MEDICINE_PRICE_COUNT = 12
	const val MODEL_MEDICINE_PRICE_INSERT_INTO = "INSERT INTO MedicinePriceModel (thisPK, kdCode, maxPrice, ancestorCode, applyDate) VALUES "
	const val MODEL_MEDICINE_INGREDIENT_INSERT_INTO = "INSERT INTO MedicineIngredientModel (thisPK, mainIngredientCode, mainIngredientName) VALUES "

	const val MODEL_PHARMA_MEDICINE_PARSE_PHARMA_NAME = "발주처"
	const val MODEL_PHARMA_MEDICINE_PARSE_MEDICINE_NAME = "제품명"
	const val MODEL_PHARMA_MEDICINE_PARSE_PHARMA_CODE = "발주처코드"
	const val MODEL_PHARMA_MEDICINE_PARSE_MEDICINE_CODE = "제품코드"
	const val MODEL_PHARMA_MEDICINE_PARSE_COUNT = 4

	const val MODEL_EDI_DUE_DATE_DATE = "날짜"
	const val MODEL_EDI_DUE_DATE_PHARMA_CODE = "제약사코드"
	const val MODEL_EDI_DUE_DATE_COUNT = 2

	const val MODEL_USER_RELATION_COMPANY_INNER_NAME = "업체명"
	const val MODEL_USER_RELATION_HOSPITAL_NAME = "병원명"
	const val MODEL_USER_RELATION_PHARMA_NAME = "제약사명"
	const val MODEL_USER_RELATION_MEDICINE_NAME = "제품명"
	const val MODEL_USER_RELATION_COUNT = 4
	const val MODEL_USER_RELATIONS_DELETE_WHERE_USER_PK = "DELETE FROM UserRelationModel WHERE userPK = "
	const val MODEL_USER_RELATIONS_DELETE_WHERE_USER_PK_IN = "DELETE FROM UserRelationModel WHERE userPK IN "
	const val MODEL_USER_RELATIONS_INSERT_INTO = "INSERT INTO UserRelationModel (thisPK, userPK, hosPK, pharmaPK, medicinePK) VALUES "

	const val MODEL_HOSPITAL_TEMP_CODE = "암호화요양기호"
	const val MODEL_HOSPITAL_TEMP_ORG_NAME = "요양기관명"
	const val MODEL_HOSPITAL_TEMP_TYPE_CODE = "종별코드"
	const val MODEL_HOSPITAL_TEMP_METRO_CODE = "시도코드"
	const val MODEL_HOSPITAL_TEMP_CITY_CODE = "시군구코드"
	const val MODEL_HOSPITAL_TEMP_LOCAL_NAME = "읍면동"
	const val MODEL_HOSPITAL_TEMP_ZIP_CODE = "우편번호"
	const val MODEL_HOSPITAL_TEMP_ADDRESS = "주소"
	const val MODEL_HOSPITAL_TEMP_PHONE_NUMBER = "전화번호"
	const val MODEL_HOSPITAL_TEMP_WEBSITE = "병원홈페이지"
	const val MODEL_HOSPITAL_TEMP_OPEN_DATE = "개설일자"
	const val MODEL_HOSPITAL_TEMP_LONGITUDE = "좌표(X)"
	const val MODEL_HOSPITAL_TEMP_LATITUDE = "좌표(Y)"
	const val MODEL_HOSPITAL_TEMP_COUNT = 16
	const val MODEL_HOSPITAL_TEMP_INSERT_INTO = "INSERT INTO HospitalTempModel (thisPK, code, orgName, hospitalTempTypeCode, hospitalTempMetroCode, hospitalTempCityCode, hospitalTempLocalName, zipCode, address, phoneNumber, websiteUrl, openDate, longitude, latitude) VALUES "

	const val MODEL_PHARMACY_TEMP_CODE = "암호화요양기호"
	const val MODEL_PHARMACY_TEMP_ORG_NAME = "요양기관명"
	const val MODEL_PHARMACY_TEMP_TYPE_CODE = "종별코드"
	const val MODEL_PHARMACY_TEMP_METRO_CODE = "시도코드"
	const val MODEL_PHARMACY_TEMP_CITY_CODE = "시군구코드"
	const val MODEL_PHARMACY_TEMP_LOCAL_NAME = "읍면동"
	const val MODEL_PHARMACY_TEMP_ZIP_CODE = "우편번호"
	const val MODEL_PHARMACY_TEMP_ADDRESS = "주소"
	const val MODEL_PHARMACY_TEMP_PHONE_NUMBER = "전화번호"
	const val MODEL_PHARMACY_TEMP_OPEN_DATE = "개설일자"
	const val MODEL_PHARMACY_TEMP_LONGITUDE = "좌표(X)"
	const val MODEL_PHARMACY_TEMP_LATITUDE = "좌표(Y)"
	const val MODEL_PHARMACY_TEMP_COUNT = 15
	const val MODEL_PHARMACY_TEMP_INSERT_INTO = "INSERT INTO PharmacyTempModel (thisPK, code, orgName, hospitalTempTypeCode, hospitalTempMetroCode, hospitalTempCityCode, hospitalTempLocalName, zipCode, address, phoneNumber, openDate, longitude, latitude) VALUES "

	const val NEW_HOSPITAL_CODE = "-99999"
	const val NEW_HOSPITAL_NAME = "신규처"
	const val TRANSFER_HOSPITAL_CODE = "-88888"
	const val TRANSFER_HOSPITAL_NAME = "이관처"

	const val REGEX_CHECK_ID = "^[a-zA-Z0-9가-힣!@#\$%^&*()/_\\-]{3,20}$"
	// 숫자, 문자(영대소한글)
	const val REGEX_CHECK_PASSWORD_0 = "^(?=.*[A-Za-z가-힣ㄱ-ㅎㅏ-ㅣ!@#\$%^&*()])(?=.*\\d)[A-Za-z가-힣ㄱ-ㅎㅏ-ㅣ\\d@\$!@#\$%^&*()]{8,20}\$"
	// 숫자, 특수문자, 문자(영대소한글)
	const val REGEX_CHECK_PASSWORD_1 = "^(?=.*[A-Za-z가-힣ㄱ-ㅎㅏ-ㅣ!@#\$%^&*()])(?=.*\\d)(?=.*[@\$!@#\$%^&*()])[A-Za-z가-힣ㄱ-ㅎㅏ-ㅣ\\d@\$!@#\$%^&*()]{8,20}\$"
	// 숫자, 대문자, 소문자
	const val REGEX_CHECK_PASSWORD_2 = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,20}\$"
	const val REGEX_ONLY_NUMBER = "[^0-9]"
	const val REGEX_ONLY_ALPHABET = "[^a-zA-Z0-9]"
	const val REGEX_SPECIAL_CHAR_REMOVE = "[^가-힣a-zA-Z0-9\\s]"
	const val REGEX_ESCAPE_SQL = "(['\"])"

	const val REGEX_PHONE_NUMBER = "^\\s*(010|011|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$"
}