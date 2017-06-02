package red.sukun1899.shishamo.controller.page;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author su-kun1899
 */
@Controller
@RequestMapping("data-source")
class DataSourceController(
        val dataSourceProperties: DataSourceProperties
) {
    @GetMapping
    fun get(model: Model): String {
        model.addAttribute("dataSourceProperties", dataSourceProperties)
        return "data-source"
    }
}
