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

	const val MODEL_USER_ID = "아이디"
	const val MODEL_USER_PW = "비밀번호"
	const val MODEL_USER_NAME = "이름"
	const val MODEL_USER_MAIL = "메일"
	const val MODEL_USER_PHONE = "전화번호"
	const val MODEL_USER_ROLE = "권한"
	const val MODEL_USER_DEPT = "부서"
	const val MODEL_USER_STATUS = "상태"
	const val MODEL_USER_COMPANY_NAME = "회사명"
	const val MODEL_USER_COMPANY_NUMBER = "사업자등록번호"
	const val MODEL_USER_COMPANY_ADDRESS = "회사주소"
	const val MODEL_USER_BANK_ACCOUNT = "은행계좌번호"
	const val MODEL_USER_COUNT = 12
	const val MODEL_USER_INSERT_INTO = "INSERT INTO UserDataModel (thisPK, id, pw, name, mail, phoneNumber, role, dept, status, companyName, companyNumber, companyAddress, bankAccount, regDate, taxpayerImageUrl, bankAccountImageUrl) VALUES "
	const val MODEL_USER_CHILD_INSERT_INTO = "INSERT INTO UserChildPKModel (thisPK, motherPK, childPK) VALUES "
	const val MODEL_USER_CHILD_DELETE_BY_MOTHER_PK = "DELETE FROM UserChildPKModel WHERE motherPK =  "

	const val MODEL_CODE = "거래처코드"
	const val MODEL_ORG_NAME = "사업자원어명"
	const val MODEL_INNER_NAME = "사업자내부명"
	const val MODEL_OWNER_NAME = "대표자명"
	const val MODEL_TAX_PAYER = "사업자번호"
	const val MODEL_PHONE = "전화번호"
	const val MODEL_FAX = "팩스번호"
	const val MODEL_ZIP_CODE = "우편번호"
	const val MODEL_ADDRESS = "주소"
	const val MODEL_ADDRESS_DETAIL = "상세주소"
	const val MODEL_BIZ_TYPE = "업태"
	const val MODEL_BIZ_ITEM = "종목"
	const val MODEL_BILL_TYPE = "계산서발행"
	const val MODEL_CO_TYPE = "거래처종류"
	const val MODEL_CO_GROUP = "거래처그룹"
	const val MODEL_CONTRACT_TYPE = "계약구분"
	const val MODEL_DELIVERY = "배송구분"
	const val MODEL_MAIL = "메일"
	const val MODEL_MOBILE_PHONE = "담당자번호"
	const val MODEL_OPEN_DATE = "거래개시일"
	const val MODEL_CLOSE_DATE = "거래종료일"
	const val MODEL_ETC1 = "비고1"
	const val MODEL_ETC2 = "비고2"
	const val MODEL_PHARMA_COUNT = 23
	const val MODEL_PHARMA_INSERT_INTO = "INSERT INTO PharmaModel (thisPK, code, orgName, innerName, ownerName, taxpayerNumber, phoneNumber, faxNumber, zipCode, address, addressDetail, businessType, businessItem, billType, pharmaType, pharmaGroup, contractType, deliveryDiv, mail, mobilePhone, openDate, closeDate, etc1, etc2, imageUrl, inVisible) VALUES "

	const val MODEL_PHARMA_MEDICINE_RELATIONS_DELETE_WHERE_PHARMA_PK = "DELETE FROM PharmaMedicineRelationModel WHERE pharmaPK = "
	const val MODEL_PHARMA_MEDICINE_RELATIONS_INSERT_INTO = "INSERT INTO PharmaMedicineRelationModel (thisPK, pharmaPK, medicinePK) VALUES "

	const val MODEL_LICENSE_NUMBER = "면허번호"
	const val MODEL_NURSING_HOME_NUMBER = "요양기관번호"
	const val MODEL_HOS_COUNT = 23
	const val MODEL_HOS_INSERT_INTO = "INSERT INTO HospitalModel (thisPK, code, orgName, innerName, ownerName, taxpayerNumber, phoneNumber, faxNumber, zipCode, address, addressDetail, businessType, businessItem, billType, licenseNumber, nursingHomeNumber, contractType, deliveryDiv, mail, mobilePhone, openDate, closeDate, etc1, etc2, imageUrl, inVisible) VALUES "

	const val MODEL_MEDICINE_CODE = "코드"
	const val MODEL_MEDICINE_MAIN_INGREDIENT_CODE = "주성분코드"
	const val MODEL_MEDICINE_KD_CODE = "제품코드"
	const val MODEL_MEDICINE_STANDARD_CODE = "표준코드"
	const val MODEL_MEDICINE_PHARMA = "제조사"
	const val MODEL_MEDICINE_NAME = "제품명"
	const val MODEL_MEDICINE_STANDARD = "규격"
	const val MODEL_MEDICINE_ACCOUNT_UNIT = "계산단위"
	const val MODEL_MEDICINE_CUSTOM_PRICE = "지정가"
	const val MODEL_MEDICINE_TYPE = "제품종류"
	const val MODEL_MEDICINE_METHOD = "제제구분"
	const val MODEL_MEDICINE_CATEGORY = "특수구분"
	const val MODEL_MEDICINE_GROUP = "제품그룹"
	const val MODEL_MEDICINE_DIV = "제품구분"
	const val MODEL_MEDICINE_RANK = "제품등급"
	const val MODEL_MEDICINE_STORAGE_TEMP = "보관온도"
	const val MODEL_MEDICINE_STORAGE_BOX = "보관용기"
	const val MODEL_MEDICINE_PACKAGE_UNIT = "포장단위"
	const val MODEL_MEDICINE_UNIT = "단위"
	const val MODEL_MEDICINE_ETC1 = "비고1"
	const val MODEL_MEDICINE_ETC2 = "비고2"
	const val MODEL_MEDICINE_COUNT = 21
	const val MODEL_MEDICINE_INSERT_INTO = "INSERT INTO MedicineModel (thisPK, code, mainIngredientCode, kdCode, standardCode, pharma, name, customPrice, inVisible) VALUES "
	const val MODEL_MEDICINE_SUB_INSERT_INTO = "INSERT INTO MedicineSubModel (thisPK, code, standard, accountUnit, " +
			"medicineType, medicineMethod, medicineCategory, medicineGroup, medicineDiv, medicineRank, medicineStorageTemp, medicineStorageBox, " +
			"packageUnit, unit, etc1, etc2) VALUES "

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
	const val MODEL_MEDICINE_PRICE_COUNT = 13
	const val MODEL_MEDICINE_PRICE_INSERT_INTO = "INSERT INTO MedicinePriceModel (thisPK, kdCode, maxPrice, ancestorCode, applyDate) VALUES "
	const val MODEL_MEDICINE_INGREDIENT_INSERT_INTO = "INSERT INTO MedicineIngredientModel (thisPK, mainIngredientCode, mainIngredientName) VALUES "

	const val MODEL_USER_RELATIONS_DELETE_WHERE_USER_PK = "DELETE FROM UserRelationModel WHERE userPK = "
	const val MODEL_USER_RELATIONS_INSERT_INTO = "INSERT INTO UserRelationModel (thisPK, userPK, hosPK, pharmaPK, medicinePK) VALUES "

	const val MODEL_PHARMA_MEDICINE_PARSE_PHARMA_CODE = "제약사코드"
	const val MODEL_PHARMA_MEDICINE_PARSE_MEDICINE_CODE = "약품코드"
	const val MODEL_PHARMA_MEDICINE_PARSE_COUNT = 2

	const val REGEX_ONLY_ALPHABET = "[^a-zA-Z0-9]"
	const val REGEX_SPECIAL_CHAR_REMOVE = "[^가-힣a-zA-Z0-9\\s]"
	const val REGEX_ESCAPE_SQL = "(['\"])"
}