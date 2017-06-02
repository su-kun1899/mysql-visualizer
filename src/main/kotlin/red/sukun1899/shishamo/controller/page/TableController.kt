package red.sukun1899.shishamo.controller.page;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import red.sukun1899.shishamo.model.Table
import red.sukun1899.shishamo.service.IndexService
import red.sukun1899.shishamo.service.TableService

/**
 * @author su-kun1899
 */
@Controller
@RequestMapping("tables")
class TableController(
        val dataSourceProperties: DataSourceProperties,
        val tableService: TableService,
        val indexService: IndexService
) {
    @GetMapping
    fun get(model: Model): String {
        val tables: List<Table> = tableService.get();
        model.addAttribute("tables", tables);

        model.addAttribute("parentTableCounts", tableService.getParentTableCountsByTableName());
        model.addAttribute("childTableCounts", tableService.getChildTableCountsByTableName());
        model.addAttribute("columnCounts", tableService.getColumnCountsByTableName());
        model.addAttribute("schemaName", dataSourceProperties.schema);

        return "tables";
    }

    @GetMapping(path = arrayOf("{tableName}"))
    fun get(@PathVariable tableName: String, model: Model): String {
        val table: Table = tableService.get(tableName)

        model.addAttribute("table", table);
        model.addAttribute("createTableStatement", tableService.getCreateTableStatement(table));
        model.addAttribute("indices", indexService.get(tableName));
        model.addAttribute("schemaName", dataSourceProperties.schema);

        return "table";
    }
}
