package red.sukun1899.shishamo.model;

/**
 * @author su-kun1899
 */
data class Table(
        val name: String,
        val comment: String,
        val rowCount: Long,
        val columns: List<Column>
)
