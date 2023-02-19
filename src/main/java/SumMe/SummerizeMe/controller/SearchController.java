package SumMe.SummerizeMe.controller;

import SumMe.SummerizeMe.domain.BasicInfo.Calender;
import SumMe.SummerizeMe.domain.BasicInfo.Tistory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Map;
import SumMe.SummerizeMe.domain.BasicInfo.BasicInfo;
import SumMe.SummerizeMe.service.SearchService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/search")
@Controller
public class SearchController {
    @Autowired
    private SearchService searchService;
    
    @RequestMapping("/get_basic_infos")
    @ResponseBody
    public BasicInfo github(@RequestBody Map<String, Object> requestData) {
        List<String> github = (List<String>) requestData.get("github");
        List<String> blog = (List<String>) requestData.get("blog");
        List<String> tistory = (List<String>) requestData.get("tistory");
        return searchService.crawlingBasicInfo(github, blog, tistory);
    }
    
    @RequestMapping("/qwer")
    @ResponseBody
    public Object test2(){
        List<String> github = new ArrayList<>();
        github.add("https://github.com/raipen");


        //MonthlyCommit m = new MonthlyCommit();

        //searchService.crawlingdcommitperiod(github);
        return searchService.crawlingMonthlyCommits(github);
    }

    @RequestMapping("/test")
    @ResponseBody
    public List<Calender> test(){
        List<String> githubRepos = new ArrayList<>();
        githubRepos.add("https://github.com/raipen");
        List<Calender> list = searchService.createCalender(githubRepos,5,7);
        return list;
    }

    @RequestMapping("/tistorytest")
    @ResponseBody
    public List<Tistory> tistorytest(){
        List<String> githubRepos = new ArrayList<>();
        githubRepos.add("https://eyls22.tistory.com/");
        return searchService.crawlingTistory(githubRepos);
    }

}