package red.sukun1899.shishamo.service;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import red.sukun1899.shishamo.model.CreateTableStatement
import red.sukun1899.shishamo.model.Table
import red.sukun1899.shishamo.repository.TableRepository

/**
 * @author su-kun1899
 */
@Service
class TableService(
        val dataSourceProperties: DataSourceProperties,
        val tableRepository: TableRepository
) {
  @Transactional(readOnly = true)
  fun get(): List<Table> = tableRepository.selectAll(dataSourceProperties.schema);


  @Transactional(readOnly = true)
  fun get(tableName: String): Table = tableRepository.select(dataSourceProperties.schema, tableName);

  @Transactional(readOnly = true)
  fun getCreateTableStatement(table: Table): CreateTableStatement = tableRepository.showCreateTableStatement(table);

  /**
   * @return Key: tableName, Value: Parent table's count
   */
  fun getParentTableCountsByTableName(): Map<String, Long> = tableRepository
          .selectParentTableCountsByTableName(dataSourceProperties.schema)
          .filter { it.value.count > 0 }
          .map { it.key to it.value.count }
          .toMap()

  /**
   * @return Key: tableName, Value: Child table's count
   */
  fun getChildTableCountsByTableName(): Map<String, Long> = tableRepository
          .selectChildTableCountsByTableName(dataSourceProperties.schema)
          .filter { it.value.count > 0 }
          .map { it.key to it.value.count }
          .toMap()

  /**
   * @return Key: tableName, Value: Column count
   */
  fun getColumnCountsByTableName(): Map<String, Long> = tableRepository
          .selectColumnCountsByTableName(dataSourceProperties.schema)
          .filter { it.value.count > 0 }
          .map { it.key to it.value.count }
          .toMap()
}
