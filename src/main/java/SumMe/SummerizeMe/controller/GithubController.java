package SumMe.SummerizeMe.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GithubController {
    
    @RequestMapping("/github")
    @ResponseBody
    public String github() {
        return "github";
    }


}