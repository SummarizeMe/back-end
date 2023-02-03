package SumMe.SummerizeMe.service;

import SumMe.SummerizeMe.domain.BasicInfo.*;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

    public List<String> crawlingdcommitperiod(List<String> github) {
        List<String> years = new ArrayList<>();
        for(String addr : github){
            try{
                org.jsoup.nodes.Document doc = Jsoup.connect(addr).get();
                Elements yearLinks = doc.select(".js-year-link");
                for(Element y : yearLinks)
                    years.add(y.attr("id").split("-")[2]);
                //years.add(y.text());

                //이렇게 넘어오면 어떤 형태로 작성이 되지?
                //이거 하나하나 꺼내서 문자열로 변형해서 주소를 만들어야함
                //자바 문자열 어케 다룸
                /*for (dates : years){
                    //git hub 주소에다가 tab=overview&from=년-월-일 &to=년-월-일


                    years.add();
                }*/
            } catch(Exception e){

            }
        }
        return years;
    }

    public int[][] crawlingMonthlyCommits(List<String> github) {
        //List<MonthlyCommit> monthlyCommits = new ArrayList<>();

        List<String> year = crawlingdcommitperiod(github);

        int[][] Monthlycommit = new int[year.size()][13];
        int num = 0;
        for(String gitlink: github) {
            System.out.println(gitlink);
            for(String y : year){
                System.out.println(y);
                Monthlycommit[num][0] = Integer.parseInt(y);
                try{
                    //System.out.println(gitlink + "?tab=overview&from="+y+"-01-01&to="+y+"-01-31");
                    org.jsoup.nodes.Document doc = Jsoup.connect(gitlink + "?tab=overview&from="+y+"-01-01&to="+y+"-01-31").get();
                    Elements a = doc.select(".ContributionCalendar-day");
                    System.out.println(num);
                    for(var e : a) {
                        //System.out.println(e.attr("data-date"));
                        int month = Integer.parseInt(e.attr("data-date").split("-")[1]);
                        //System.out.println(month);
                        try{
                            int commit = Integer.parseInt(e.text().split(" ")[0]);
                            Monthlycommit[num][month] += commit;
                        }
                        catch(NumberFormatException ex){
                            //ex.printStackTrace();
                        }
                    }


                } catch(Exception e){
                    //System.out.println(e);
                }
                num += 1;
            }
        }

        for(int[] a : Monthlycommit){
            System.out.println(Monthlycommit);
        }

        return Monthlycommit;
    }
}