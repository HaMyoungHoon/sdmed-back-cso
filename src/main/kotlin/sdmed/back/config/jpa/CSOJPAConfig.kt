package sdmed.back.config.jpa

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
	basePackages = ["sdmed.back.repository.sqlCSO"],
	entityManagerFactoryRef = CSOJPAConfig.ENTITY_MANAGER,
	transactionManagerRef = CSOJPAConfig.TRANSACTION_MANAGER,
	enableDefaultTransactions = true)
open class CSOJPAConfig {
	companion object {
		const val DATA_SOURCE = "csoDataSource"
		const val ENTITY_MANAGER = "csoEntityManager"
		const val TRANSACTION_MANAGER = "csoTransactionManager"
	}

	@Bean
	open fun exceptionTranslation() = PersistenceExceptionTranslationPostProcessor()
	@Bean(name = [DATA_SOURCE])
	@ConfigurationProperties(prefix = "spring.datasource.sql-cso")
	open fun dataSource(): HikariDataSource = DataSourceBuilder.create().type(HikariDataSource::class.java).build()
	@Bean(name = [ENTITY_MANAGER])
	open fun csoEntityManagerFactory(): LocalContainerEntityManagerFactoryBean = LocalContainerEntityManagerFactoryBean().apply {
		this.dataSource = dataSource()
		this.setPackagesToScan("sdmed.back.model.sqlCSO")
		this.jpaVendorAdapter = HibernateJpaVendorAdapter()
		this.setJpaProperties(additionalProperties())
	}
	@Bean(name = [TRANSACTION_MANAGER])
	open fun csoTransactionManager(): PlatformTransactionManager = JpaTransactionManager().apply {
		this.entityManagerFactory = csoEntityManagerFactory().`object`
	}
	fun additionalProperties(): Properties = Properties().apply {
//		this.setProperty("hibernate.hbm2ddl.auto", "update")
		this.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect")
//		this.setProperty("hibernate.dialect", "org.hibernate.dialect.SQLServerDialect")
//		this.setProperty("hibernate.show_sql", "true")
	}
}