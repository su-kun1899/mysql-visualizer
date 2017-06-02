package red.sukun1899.shishamo.embedded.mysql;

import com.wix.mysql.EmbeddedMysql
import com.wix.mysql.EmbeddedMysql.anEmbeddedMysql
import com.wix.mysql.config.MysqldConfig.aMysqldConfig
import org.yaml.snakeyaml.Yaml

/**
 * @author su-kun1899
 */
class EmbeddedMySqlUtil private constructor () {
    companion object {
        @JvmStatic
        val config: EmbeddedMySqlConfig = loadConfig()

        @JvmStatic
        private val SINGLETON_INSTANCE: EmbeddedMysql? = startEmbeddedMysql()

        @JvmStatic
        fun enable(): Boolean {
            System.getProperty("shishamo.embedded.mysql")?.let {
                return it.toBoolean()
            }

            return config.enable;
        }

        @JvmStatic
        fun ready(): EmbeddedMysql? {
            if (!enable()) {
                throw error("Embedded MySQL is disabled. " +
                        "Check your configuration file in embedded-mysql.yml");
            }
            return SINGLETON_INSTANCE;
        }

        private fun loadConfig(): EmbeddedMySqlConfig {
            EmbeddedMySqlUtil::class.java.classLoader
                    .getResourceAsStream("embedded-mysql.yml")?.let {
                return Yaml().loadAs(it, EmbeddedMySqlConfig::class.java)
            }

            // Config file is not found. Use default.
            // FIXME Untested
            return EmbeddedMySqlConfig()
        }

        private fun startEmbeddedMysql(): EmbeddedMysql? {
            if (enable()) {
                return anEmbeddedMysql(aMysqldConfig(config.version)
                        .withCharset(config.getWixCharset())
                        .withPort(config.port)
                        .withUser(config.username, config.password)
                        .build()
                ).addSchema(config.schemaName)
                        .start()
            }

            return null
        }
    }
}
