package red.sukun1899.shishamo.embedded.mysql;

import com.wix.mysql.config.Charset
import com.wix.mysql.config.Charset.*
import com.wix.mysql.distribution.Version

/**
 * @author su-kun1899
 */
class EmbeddedMySqlConfig(
        var enable: Boolean = false,
        var port: Int = 3306,
        var version: Version = Version.v5_7_latest,
        var username: String = "root",
        var password: String = "test",
        var schemaName: String = "",
        var charset: String = UTF8.charset
) {
    fun getWixCharset(): Charset {
        when (charset.toLowerCase()) {
            UTF8.charset.toLowerCase() ->
                return UTF8;
            UTF8MB4.charset.toLowerCase() ->
                return UTF8MB4;
            LATIN1.charset.toLowerCase() ->
                return LATIN1;
            else ->
                return UTF8;
        }
    }
}
