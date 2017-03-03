package red.sukun1899.controller.api.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import red.sukun1899.model.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * @author su-kun1899
 */
@RestController
@RequestMapping("v1/tables")
public class TablesRestController {
    @GetMapping
    public List<Table> get() {
        final List<Table> tables = new ArrayList<>();
        tables.add(new Table());
        return tables;
    }
}
