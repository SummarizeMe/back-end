package SumMe.SummerizeMe.service;

import SumMe.SummerizeMe.domain.BasicInfo.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.jsoup.Jsoup;

import java.util.*;

@Service
public class SearchService {
    public BasicInfo crawlingBasicInfo(List<String> github, List<String> blog, List<String> tistory) {
        BasicInfo basicInfo = new BasicInfo();
        basicInfo.setGithub_repos(crawlingGithubRepos(github));
        basicInfo.setBlog(crawlingBlog(blog));
        basicInfo.setTistory(crawlingTistory(tistory));
        basicInfo.setCalender(createCalender(github,3,5));
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


    public List<Calender> createCalender(List<String> github, int StartMonth, int EndMonth) {
        List<Calender> githubDate = new ArrayList<>();
        System.out.println("crawl: " + github);
        for (String githubAddr : github) {
            try {
                Document doc = Jsoup.connect(githubAddr+"?tab=overview&from").get();
                Elements rectArr = doc.select("rect.ContributionCalendar-day");
                int rectArrsize = rectArr.size();
                //rectArr.sublist(0,rectArrsize-5);
                rectArr.subList(rectArrsize-5,rectArrsize).clear();
                for(Element rect : rectArr){
                    if(!rect.attr("data-level").equals("0")) {
                        int month = Integer.parseInt(rect.attr("data-date").split("-")[1]);
                        if(month >= StartMonth && month <= EndMonth){
                            //System.out.println(rect.attr("data-date"));
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
        System.out.println("crawl: "+ date);
        calender.setDate(date);
        List<Map<String,String>> works = new ArrayList<>();
        try{
            //https://github.com/raipen?from=2022-12-01&to=2022-12-01&tab=overview
            //org.jsoup.nodes.Document doc = Jsoup.connect("https://github.com/raipen?from=2022-12-01&to=2022-12-01&tab=overview").get();
            org.jsoup.nodes.Document doc = Jsoup.connect(githubAddr+"?from="+date+"&to="+date+"&tab=overview").get();
            System.out.println(githubAddr+"?from="+date+"&to="+date+"&tab=overview");
            List<Map<String,Object>> data = new ArrayList<>();
            Elements div = doc.select("div.col-8.css-truncate.css-truncate-target.lh-condensed.width-fit.flex-auto.min-width-0");
            for(Element a : div) {
                Map<String,Object> work = new HashMap<>();
                work.put("type","github");
                work.put("repo",a.select("a").get(0).text());
                work.put("commit",a.select("a").get(1).text());
                data.add(work);
            }
            calender.setWorks(data);
        }
        catch(Exception e){
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

}