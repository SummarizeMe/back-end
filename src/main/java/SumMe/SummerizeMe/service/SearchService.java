package SumMe.SummerizeMe.service;

import SumMe.SummerizeMe.domain.BasicInfo.*;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup; 
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Service
public class SearchService {
    public BasicInfo crawlingBasicInfo(List<String> github, List<String> blog) {
        BasicInfo basicInfo = new BasicInfo();
        basicInfo.setGithub_repos(crawlingGithubRepos(github));
        basicInfo.setBlog(crawlingBlog(blog));
        basicInfo.setCalender(createCalender(github,blog));
        return basicInfo;
    }

    private List<GithubRepo> crawlingGithubRepos(List<String> github) {
        List<GithubRepo> githubRepos = new ArrayList<>();
        System.out.println("crawl: " + github);
        for (String githubAddr : github) {
            try {
                org.jsoup.nodes.Document doc = Jsoup.connect(githubAddr+"?tab=repositories").get();
                String[] repoArr = doc.select("a[itemprop=name codeRepository]").text().split(" ");
                for(String repo : repoArr){
                    githubRepos.add(crawlingGithubRepoInfo(githubAddr+"/"+repo));
                }
            } catch (Exception e) {
                System.out.println("Error: Jsoup connect error(" + githubAddr+"?tab=repositories)");
            }
        }
        return githubRepos;
    }

    private GithubRepo crawlingGithubRepoInfo(String repo) {
        GithubRepo githubRepo = new GithubRepo();
        System.out.println("crawl: " + repo);
        githubRepo.setAddr(repo);
        githubRepo.setUsed_tech(new String[]{"spring", "springboot"});
        List<Map<String,String>> used_lang = new ArrayList<>();
        try{
            org.jsoup.nodes.Document doc = Jsoup.connect(repo).get();
            for(var lang : doc.select("li.d-inline")){
                Map<String,String> used_lang_map = new HashMap<>();
                used_lang_map.put("lang", lang.select("span").get(0).text());
                used_lang_map.put("percent", lang.select("span").get(1).text());
                used_lang.add(used_lang_map);
            }
        } catch(Exception e){
            System.out.println("Error: Jsoup connect error(" + repo + ")");
        }
        githubRepo.setUsed_lang(used_lang);
        return githubRepo;
    }

    private List<Blog> crawlingBlog(List<String> blog) {
        return null;
    }

    private List<Calender> createCalender(List<String> github, List<String> blog) {
        return null;
    }


}