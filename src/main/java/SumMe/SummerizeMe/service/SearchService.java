package SumMe.SummerizeMe.service;

import SumMe.SummerizeMe.domain.BasicInfo.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class SearchService {

    public List<GithubRepo> crawlingGithubRepos(List<String> github) {
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

    public List<Calender> createCalender(List<String> github, Map<String,Object> start, Map<String,Object> end) {
        List<Calender> githubDate = new ArrayList<>();
        System.out.println("crawl: " + github);
        for (String githubAddr : github) {
            try {
                Document doc = Jsoup.connect(githubAddr+"?tab=overview&from").get();
                Elements rectArr = doc.select("rect.ContributionCalendar-day");
                int rectArrsize = rectArr.size();
                rectArr.subList(rectArrsize-5,rectArrsize).clear();
                for(Element rect : rectArr){
                    if(!rect.attr("data-level").equals("0")) {
                        int month = Integer.parseInt(rect.attr("data-date").split("-")[1]);
                        if(month >= (int) start.get("month") && month <= (int) end.get("month")){
                            String date = rect.attr("data-date");
                            githubDate.add(crawlingGithubCalen(githubAddr , date));
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: Jsoup connect error(" + githubAddr+"?tab=overview)");
            }
        }
        return githubDate;
    }

    private Calender crawlingGithubCalen(String githubAddr, String date) {
        Calender calender = new Calender();
        System.out.println("crawl: " + date);
        calender.setDate(date);
        List<Map<String, String>> works = new ArrayList<>();
        try {
            //https://github.com/raipen?from=2022-12-01&to=2022-12-01&tab=overview
            //org.jsoup.nodes.Document doc = Jsoup.connect("https://github.com/raipen?from=2022-12-01&to=2022-12-01&tab=overview").get();
            org.jsoup.nodes.Document doc = Jsoup.connect(githubAddr + "?from=" + date + "&to=" + date + "&tab=overview").get();
            System.out.println(githubAddr + "?from=" + date + "&to=" + date + "&tab=overview");
            List<Map<String, Object>> data = new ArrayList<>();
            Elements div = doc.select("div.col-8.css-truncate.css-truncate-target.lh-condensed.width-fit.flex-auto.min-width-0");
            for (Element a : div) {
                Map<String, Object> work = new HashMap<>();
                work.put("type", "github");
                work.put("repo", a.select("a").get(0).text());
                work.put("commit", a.select("a").get(1).text());
                data.add(work);
            }
            calender.setWorks(data);
        } catch (Exception e) {
            System.out.println("Error: Jsoup connect error(" + date + ")");
        }
        return calender;
    }

    public List<Tistory> crawlingTistory(List<String> tistory) {
        List<Tistory> tistories = new ArrayList<>();
        System.out.println("crawl: "+ tistory);
        for(String tistoryAddr : tistory){
            try{
                //tistoryAddr=https://eyls22.tistory.com/
                //org.jsoup.nodes.Document doc = Jsoup.connect("https://eyls22.tistory.com/").get();
                org.jsoup.nodes.Document doc = Jsoup.connect(tistoryAddr).get();
                Elements articleArr = doc.select("a.link-article");//attr->[]으로
                int num=0;
                //Elements articleArr = doc.select("article.article-type-common article-type-thumbnail checked-item");
                for(Element article: articleArr){
                    num++;
                    if(article.hasClass("link-article")){
                        if(num%2==0){
                            continue;
                        }
                    }
                    tistories.add(crawlingTistoryInfo(tistoryAddr+article.attr("href")));//+숫자 32
                }
            }catch(Exception e){
                System.out.println("Error: Jsoup connect error(" +tistoryAddr );
            }
        }
        return tistories;
    }

    private Tistory crawlingTistoryInfo(String articleArr){
        Tistory tistory = new Tistory();
        System.out.println("crawl: "+ tistory);
        try{
            org.jsoup.nodes.Document doc = Jsoup.connect(articleArr).get();
            System.out.println(articleArr);
            tistory.setAddr(articleArr);
            tistory.setTitle(doc.select("h2.title-article").text());
            //.replace('.','-')//split(" ")[1]
            String[] contents = doc.select("span.date").text().split(" ");
            //System.out.println(contents[0]);
            String s;
            s = contents[0]+contents[1]+contents[2];
            //System.out.println(s);
            s = s.substring(0,(s.length()-1)).replace('.','-');
            //System.out.println(s);
            tistory.setDate(s);
            /*for(int i=0;i<s.length()-1;i++){
                System.out.println(s[i]);
            }*/
            //tistory.setDate(doc.select("span.date").text().split(" ")[0]);

            //tistory.setDate(doc.select("span.date").text().split(" ")[0]);
            //tistory.setKeyword("key");
        }
        catch(Exception e){
            System.out.println("Error: Jsoup connect error(" + tistory + ")");
        }
        return tistory;
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

    public List<Map<String,Object>> crawlingMonthlyCommits(List<String> github) {
        //List<MonthlyCommit> monthlyCommits = new ArrayList<>();

        List<String> year = crawlingdcommitperiod(github);

        List<Map<String,Object>> Monthlycommit = new ArrayList<>();
        for(String gitlink: github) {
            System.out.println(gitlink);
            for(String y : year){
                System.out.println(y);
                Map<String,Object> monthlyCommit = new HashMap<>();
                monthlyCommit.put("year", Integer.parseInt(y));
                int commitCount[] = new int[12];
                try{
                    //System.out.println(gitlink + "?tab=overview&from="+y+"-01-01&to="+y+"-01-31");
                    org.jsoup.nodes.Document doc = Jsoup.connect(gitlink + "?tab=overview&from="+y+"-01-01&to="+y+"-01-31").get();
                    Elements a = doc.select(".ContributionCalendar-day");
                    for(var e : a) {
                        System.out.println(e.attr("data-date"));

                        int month = Integer.parseInt(e.attr("data-date").split("-")[1]);
                        //System.out.println(month);
                        try{
                            int commit = Integer.parseInt(e.text().split(" ")[0]);
                            commitCount[month-1] += commit;
                        }
                        catch(NumberFormatException ex){
                            //ex.printStackTrace();
                        }
                    }
                } catch(Exception e){
                    //System.out.println(e);
                }
                System.out.println(Arrays.toString(commitCount));
                monthlyCommit.put("commit", commitCount);
                Monthlycommit.add(monthlyCommit);
            }
        }

        return Monthlycommit;
    }

    public List<String> getlinksforVelog(List<String> velog){
        List<String> info = new ArrayList<>();
         for(String veloglink : velog){
            try{
                org.jsoup.nodes.Document doc = Jsoup.connect(veloglink).get();
                Elements posts = doc.select(".sc-gslxeA");
                System.out.println(posts);
                for(Element p : posts) {
                    info.add(p.select("a").first().attr("abs:href"));

                }
            } catch(Exception e){
                System.out.println("조졌네 이거");
            }
        }
        System.out.println(info);
        return info;
    }

    public List<Velog> getinfofromVelog(List<String> links){
        List<Velog> info = new ArrayList<>();
        Velog temp = new Velog();
        //이 함수에서는 date post제목 가져오고
        // 다른 함수에서 주요 키워드 분석해주기
        // 나중엔 getlinksfor velog이거 수정해서 저 함수안에서 모두 호출하는걸
        for (String link : links){
            try{
                //포스트 제목 구하기는 간단함
                temp.setUrl(link);
                System.out.println(link);
                temp.setTitle(link.split("/")[4]);
                System.out.println(link.split("/")[4]);
                //System.out.println(postname);
                org.jsoup.nodes.Document doc = Jsoup.connect(link).get();
                System.out.println("들어옴");
                Elements a = doc.select(".information span");
                //System.out.println(String.valueOf(a.text()).split("·")[1]);
                temp.setDate(String.valueOf(a.text()).split("·")[1]);
                //date, url, title 짠

                info.add(temp);
            }
            catch(Exception e){
                System.out.println("빠빠빠");
                System.out.println(e);
            }

        }
        return info;

    }



}