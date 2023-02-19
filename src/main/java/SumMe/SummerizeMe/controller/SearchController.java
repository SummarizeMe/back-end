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
import SumMe.SummerizeMe.domain.BasicInfo.*;
import SumMe.SummerizeMe.service.SearchService;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/search")
@Controller
public class SearchController {
    @Autowired
    private SearchService searchService;
    
    @RequestMapping("/get_github_repos")
    @ResponseBody
    public List<GithubRepo> github(@RequestBody List<String> github) {
        return searchService.crawlingGithubRepos(github);
    }
    
    @RequestMapping("/get_monthly_commits")
    @ResponseBody
    public List<Map<String,Object>> monthly(@RequestBody List<String> github){
        return searchService.crawlingMonthlyCommits(github);
    }

    @RequestMapping("/get_calender")
    @ResponseBody
    public List<Calender> calender(@RequestBody Map<String,Object> map){
        List<String> github = (List<String>) map.get("github");
        Map<String,Object> start = (Map<String,Object>) map.get("start");
        Map<String,Object> end = (Map<String,Object>) map.get("end");
        List<Calender> list = searchService.createCalender(github,start,end);
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