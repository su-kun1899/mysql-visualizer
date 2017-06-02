package red.sukun1899.shishamo.controller.api.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import red.sukun1899.shishamo.model.Table;
import red.sukun1899.shishamo.service.TableService;

/**
 * @author su-kun1899
 */
@RestController
@RequestMapping("v1/tables")
class TablesRestController(
        val tableService: TableService
) {
    @GetMapping
    fun get(): List<Table> = tableService.get()

    @GetMapping(path = arrayOf("{tableName}"))
    fun get(@PathVariable tableName: String): Table = tableService.get(tableName)
}
