package sdmed.back.config

import dev.akkinoc.util.YamlResourceBundle
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ResourceBundleMessageSource
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor
import org.springframework.web.servlet.i18n.SessionLocaleResolver
import java.util.*

@Configuration
open class MessageConfiguration: WebMvcConfigurer {
	@Bean
	open fun localeResolver() = SessionLocaleResolver().apply { this.setDefaultLocale(Locale.KOREAN) }
	@Bean
	open fun localeChangeInterceptor() = LocaleChangeInterceptor().apply { this.paramName = "lang" }
	@Bean
	open fun messageSource(@Value(value = "\${spring.messages.basename}") basename: String,
	                  @Value(value = "\${spring.messages.encoding}") encoding: String) =
		YamlMessageSource().apply {
			this.setBasename(basename)
			this.setDefaultEncoding(encoding)
			this.setAlwaysUseMessageFormat(true)
			this.setUseCodeAsDefaultMessage(true)
			this.setFallbackToSystemLocale(true)
		}

	override fun addInterceptors(registry: InterceptorRegistry) {
		registry.addInterceptor(localeChangeInterceptor())
	}

	override fun addCorsMappings(registry: CorsRegistry) {
		registry.addMapping("/**").allowedOrigins("*")
	}

	override fun configureAsyncSupport(configurer: AsyncSupportConfigurer) {
		configurer.setDefaultTimeout(-1)
	}

	class YamlMessageSource: ResourceBundleMessageSource() {
		@Throws(MissingResourceException::class)
		override fun doGetBundle(basename: String, locale: Locale): ResourceBundle = ResourceBundle.getBundle(basename, locale, YamlResourceBundle.Control)
	}
}