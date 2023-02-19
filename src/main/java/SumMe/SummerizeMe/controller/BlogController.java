package SumMe.SummerizeMe.controller;

import SumMe.SummerizeMe.domain.Calender;
import SumMe.SummerizeMe.domain.Blog;
import SumMe.SummerizeMe.service.BlogService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import kr.bydelta.koala.data.Sentence;

@RequestMapping("/api/v1/blog")
@Controller
public class BlogController {
    @Autowired
    private BlogService blogService;

    @RequestMapping("/velog")
    @ResponseBody
    public List<Blog> velog(@RequestBody List<String> velog) {
        return blogService.getinfofromVelog(blogService.getlinksforVelog(velog));
    }

    @RequestMapping("/tistory")
    @ResponseBody
    public List<Blog> tistory(@RequestBody List<String> tistory) {
        return blogService.crawlingTistory(tistory);
    }
}