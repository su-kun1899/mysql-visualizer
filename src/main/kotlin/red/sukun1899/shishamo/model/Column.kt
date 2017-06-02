package red.sukun1899.shishamo.model;

/**
 * @author su-kun1899
 */
data class Column(
        val name: String,
        val defaultValue: String?,
        val nullable: Boolean,
        val type: String,
        val comment: String,
        val parentColumn: ReferencedColumn,
        val childColumns: List<ReferencedColumn>
) {
    fun getIndexCategory(indices: Collection<Index>): Index.Category? {
        return indices.filter {
            it.columns.any {
                it.name == this.name
            }
        }.map { it.category }.firstOrNull()
    }
}
