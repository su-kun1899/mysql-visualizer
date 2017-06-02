package red.sukun1899.shishamo.repository.jdbc

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import red.sukun1899.shishamo.model.*
import red.sukun1899.shishamo.repository.TableRepository

/**
 *
 * @author yo1000
 */
@Repository
class JdbcTableRepository(
        val jdbcTemplate: NamedParameterJdbcTemplate
) : TableRepository {
    override fun selectAll(schemaName: String): List<Table> {
        return jdbcTemplate.query("""
                SELECT
                    tab.table_name    AS `name`,
                    tab.table_comment AS `comment`,
                    tab.table_rows    AS `rowCount`
                FROM
                    information_schema.tables tab
                WHERE
                    tab.table_schema = :schemaName
                    AND
                    tab.table_type = 'BASE TABLE'
                """,
                mapOf(
                        "schemaName" to schemaName
                ),
                { resultSet, _ ->

                    Table(
                            resultSet.getString("name"),
                            resultSet.getString("comment"),
                            resultSet.getLong("rowCount"),
                            emptyList()
                    )
                }
        ) ?: emptyList()
    }

    override fun select(schemaName: String, name: String): Table {
        return jdbcTemplate.query("""
                SELECT
                    tab.table_name               AS `name`,
                    tab.table_comment            AS `comment`,
                    tab.table_rows               AS `rowCount`,
                    col.column_name              AS `column_name`,
                    col.column_type              AS `column_type`,
                    col.column_default           AS `column_defaultValue`,
                    col.is_nullable              AS `column_nullable`,
                    col.column_comment           AS `column_comment`,
                    parent.referenced_table_name AS `column_parent_tableName`,
                    child.column_name            AS `column_child_name`,
                    child.table_name             AS `column_child_tableName`
                FROM
                    information_schema.tables tab
                    INNER JOIN
                    information_schema.columns col
                        ON tab.table_schema = col.table_schema AND tab.table_name = col.table_name
                    LEFT OUTER JOIN
                    information_schema.key_column_usage parent
                        ON col.table_schema = parent.table_schema
                           AND col.table_name = parent.table_name
                           AND col.column_name = parent.column_name
                    LEFT OUTER JOIN
                    information_schema.key_column_usage child
                        ON col.table_schema = child.table_schema
                           AND col.table_name = child.referenced_table_name
                           AND col.column_name = child.referenced_column_name
                WHERE
                    tab.table_schema = :schemaName
                    AND
                    tab.table_name = :name
                    AND
                    tab.table_type = 'BASE TABLE'
                ORDER BY
                    col.ordinal_position
                """,
                mapOf(
                        "schemaName" to schemaName,
                        "name" to name
                ),
                { resultSet, _ ->
                    mapOf(
                            "name" to resultSet.getString("name"),
                            "comment" to resultSet.getString("comment"),
                            "rowCount" to resultSet.getLong("rowCount"),
                            "columns_name" to resultSet.getString("column_name"),
                            "columns_defaultValue" to resultSet.getString("column_defaultValue"),
                            "columns_nullable" to resultSet.getBoolean("column_nullable"),
                            "columns_type" to resultSet.getString("column_type"),
                            "columns_comment" to resultSet.getString("column_comment"),
                            "columns_parentColumn" to ReferencedColumn(
                                    "",
                                    resultSet.getString("column_parent_tableName") ?: ""
                            ),
                            "columns_childColumns" to ReferencedColumn(
                                    resultSet.getString("column_child_name") ?: "",
                                    resultSet.getString("column_child_tableName") ?: ""
                            )
                    )
                })
                //
                // TODO: 検索結果が存在しない場合のテスト
                //
                .groupBy { mapOf(
                        "name" to it["name"],
                        "comment" to it["comment"],
                        "rowCount" to it["rowCount"]
                ) }
                .map { (key, value) ->
                    Table(
                            name = key["name"] as String,
                            comment = key["comment"] as String,
                            rowCount = key["rowCount"] as Long,
                            columns = value.groupBy { mapOf(
                                    "columns_name" to it["columns_name"],
                                    "columns_defaultValue" to it["columns_defaultValue"],
                                    "columns_nullable" to it["columns_nullable"],
                                    "columns_comment" to it["columns_comment"],
                                    "columns_type" to it["columns_type"],
                                    "columns_parentColumn" to it["columns_parentColumn"]
                            ) }.map { (key, value) -> Column(
                                    name = key["columns_name"] as String,
                                    defaultValue = key["columns_defaultValue"] as String?,
                                    nullable = key["columns_nullable"] as Boolean,
                                    comment = key["columns_comment"] as String,
                                    type = key["columns_type"] as String,
                                    parentColumn = key["columns_parentColumn"] as ReferencedColumn,
                                    childColumns = value.map { it["columns_childColumns"] as ReferencedColumn }
                            ) }
                    )
                }.first()
    }

    override fun selectParentTableCountsByTableName(schemaName: String): Map<String, ReferencedTableCount> {
        return jdbcTemplate.query("""
                SELECT
                    table_name     AS baseTableName,
                    sum(col_count) AS count
                FROM
                    (
                        SELECT
                            table_name,
                            count(referenced_column_name) AS col_count
                        FROM
                            information_schema.key_column_usage
                        WHERE
                            table_schema = :schemaName
                        GROUP BY
                            table_name, referenced_table_name
                    ) parent_col
                GROUP BY
                    table_name
                """,
                mapOf(
                        "schemaName" to schemaName
                ),
                { resultSet, _ ->
                    ReferencedTableCount(
                            baseTableName = resultSet.getString("baseTableName"),
                            count = resultSet.getLong("count"))
                })
                .associateBy { it.baseTableName }
    }

    override fun selectChildTableCountsByTableName(schemaName: String): Map<String, ReferencedTableCount> {
        return jdbcTemplate.query("""
                SELECT
                    table_name AS baseTableName,
                    sum(count) AS count
                FROM
                    (
                        SELECT
                            referenced_table_name AS table_name,
                            count(column_name)    AS count
                        FROM
                            information_schema.key_column_usage
                        WHERE
                            table_schema = :schemaName
                            AND
                            referenced_table_name IS NOT NULL
                        GROUP BY
                            referenced_table_name, table_name
                    ) child_col
                GROUP BY
                    table_name
                """,
                mapOf(
                        "schemaName" to schemaName
                ),
                { resultSet, _ ->
                    ReferencedTableCount(
                            baseTableName = resultSet.getString("baseTableName"),
                            count = resultSet.getLong("count"))
                })
                .associateBy { it.baseTableName }
    }

    override fun selectColumnCountsByTableName(schemaName: String): Map<String, ReferencedTableCount> {
        return jdbcTemplate.query("""
                SELECT
                    tab.table_name         AS baseTableName,
                    count(col.column_name) AS count
                FROM
                    information_schema.tables tab
                    INNER JOIN
                    information_schema.columns col
                        ON tab.table_schema = col.table_schema AND tab.table_name = col.table_name
                WHERE
                    tab.table_schema = :schemaName
                    AND
                    tab.table_type = 'BASE TABLE'
                GROUP BY
                    tab.table_name
                """,
                mapOf(
                        "schemaName" to schemaName
                ),
                { resultSet, _ ->
                    ReferencedTableCount(
                            baseTableName = resultSet.getString("baseTableName"),
                            count = resultSet.getLong("count"))
                })
                .associateBy { it.baseTableName }
    }

    override fun showCreateTableStatement(table: Table): CreateTableStatement {
        return jdbcTemplate.query("""
                SHOW CREATE TABLE ${table.name}
                """,
                { resultSet, _ ->
                    CreateTableStatement(
                            tableName = resultSet.getString("table"),
                            ddl = resultSet.getString("create table")
                    )
                }
        ).first() ?: CreateTableStatement("", "")
    }
}