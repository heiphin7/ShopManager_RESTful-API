package api.shopmanager.contollers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping("/secured")
    public String securedDate() {
        return "secured Data";
    }

}
