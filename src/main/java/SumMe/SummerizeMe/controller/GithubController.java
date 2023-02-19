package SumMe.SummerizeMe.controller;

import SumMe.SummerizeMe.domain.Calender;
import SumMe.SummerizeMe.domain.GithubRepo;
import SumMe.SummerizeMe.service.GithubService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/github")
@Controller
public class GithubController {
    @Autowired
    private GithubService githubService;

    @RequestMapping("/get_repos")
    @ResponseBody
    public List<GithubRepo> github(@RequestBody List<String> github) {
        return githubService.crawlingGithubRepos(github);
    }

    @RequestMapping("/get_monthly_commits")
    @ResponseBody
    public List<Map<String,Object>> monthly(@RequestBody List<String> github){
        return githubService.crawlingMonthlyCommits(github);
    }

    @RequestMapping("/get_calender")
    @ResponseBody
    public List<Calender> calender(@RequestBody List<String> github){
        return githubService.createCalender(github);
    }
}