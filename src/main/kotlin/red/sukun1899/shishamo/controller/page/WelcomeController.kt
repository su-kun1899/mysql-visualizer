package red.sukun1899.shishamo.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author yo1000
 */
@Controller
@RequestMapping("/")
class WelcomeController {
    @GetMapping
    fun get(): String = "redirect:/tables";
}
