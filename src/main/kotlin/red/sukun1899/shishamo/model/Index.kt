package red.sukun1899.shishamo.model;

/**
 * @author su-kun1899
 */
data class Index(
        val name: String,
        val columns: List<Column>,
        val category: Category
) {
    enum class Category(
            val order: Int
    ) {
        PRIMARY(10),
        UNIQUE(20),
        PERFORMANCE(30);
    }
}
