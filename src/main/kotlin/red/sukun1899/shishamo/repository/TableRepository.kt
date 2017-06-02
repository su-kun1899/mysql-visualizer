package red.sukun1899.shishamo.repository;

import red.sukun1899.shishamo.model.CreateTableStatement
import red.sukun1899.shishamo.model.ReferencedTableCount
import red.sukun1899.shishamo.model.Table

/**
 * @author su-kun1899
 */
interface TableRepository {
    fun selectAll(schemaName: String): List<Table>
    fun select(schemaName: String, name: String): Table
    fun selectParentTableCountsByTableName(schemaName: String): Map<String, ReferencedTableCount>
    fun selectChildTableCountsByTableName(schemaName: String): Map<String, ReferencedTableCount>
    fun selectColumnCountsByTableName(schemaName: String): Map<String, ReferencedTableCount>
    fun showCreateTableStatement(table: Table): CreateTableStatement
}
