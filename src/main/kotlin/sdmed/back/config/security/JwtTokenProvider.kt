package sdmed.back.config.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import jakarta.annotation.PostConstruct
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import sdmed.back.config.FConstants
import sdmed.back.model.sqlCSO.user.UserDataModel
import sdmed.back.repository.sqlCSO.IUserDataRepository
import java.lang.Exception
import java.security.Key
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

@Component
class JwtTokenProvider {
	@Value(value = "\${spring.jwt.secret}") var secretKey: String = ""
	var tokenValidMS = 1000L * 60 * 60 * 24 * 14 // 14 days
	@Autowired lateinit var userDataRepository: IUserDataRepository
	companion object {
		const val authToken: String = "auth_token"
	}
	@PostConstruct
	protected fun init() {
//        secretKey = Base64.getEncoder().encodeToString(secretKey.toByteArray())
	}

	fun getSignKey(): Key = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256")
	fun getSecretKey(): SecretKey = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256")
	fun createToken(user: UserDataModel, validTime: Long = tokenValidMS): String {
		val now = Date()
		return Jwts.builder().claims(Jwts.claims().subject(user.id).apply {
			this.add(FConstants.CLAIM_INDEX, user.thisPK)
			this.add(FConstants.CLAIM_NAME, user.name)
			this.add(FConstants.CLAIM_STATUS, user.status)
		}.build()).expiration(Date(now.time + validTime)).signWith(getSignKey()).compact()
	}
	fun getAllClaimsFromToken(token : String): Claims = Jwts.parser().verifyWith(getSecretKey()).build().parseSignedClaims(token).payload
	fun resolveToken(req : HttpServletRequest): String? =
		req.getHeader(authToken)
	fun validateToken(token : String) = try {
		!getAllClaimsFromToken(token).expiration.before(Date())
	}
	catch (e: Exception) {
		false
	}
}