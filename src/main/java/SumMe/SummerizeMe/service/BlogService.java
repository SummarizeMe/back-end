package SumMe.SummerizeMe.service;

import SumMe.SummerizeMe.domain.Blog;
import org.springframework.stereotype.Service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BlogService{

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

    public List<Blog> getinfofromVelog(List<String> links){
        List<Blog> info = new ArrayList<>();
        //이 함수에서는 date post제목 가져오고
        // 다른 함수에서 주요 키워드 분석해주기
        // 나중엔 getlinksfor velog이거 수정해서 저 함수안에서 모두 호출하는걸
        for (String link : links){
            try{
                Blog temp = new Blog();
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
                temp.setDate(String.valueOf(a.text()).split("·")[1].replace("년","-").replace("월","-").replace("일",""));
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

    public List<Blog> crawlingTistory(List<String> tistory) {
        List<Blog> tistories = new ArrayList<>();
        System.out.println("crawl: "+ tistory);
        for(String tistoryAddr : tistory){
            try{
                org.jsoup.nodes.Document doc = Jsoup.connect(tistoryAddr).get();
                Elements articleArr = doc.select("a.link-article");
                int num=0;
                for(Element article: articleArr){
                    num++;
                    if(article.hasClass("link-article")){
                        if(num%2==0){
                            continue;
                        }
                    }
                    tistories.add(crawlingTistoryInfo(tistoryAddr+article.attr("href")));
                }
            }catch(Exception e){
                System.out.println("Error: Jsoup connect error(" +tistoryAddr );
            }
        }
        return tistories;
    }

    private Blog crawlingTistoryInfo(String articleArr){
        Blog tistory = new Blog();
        System.out.println("crawl: "+ tistory);
        try{
            org.jsoup.nodes.Document doc = Jsoup.connect(articleArr).get();
            System.out.println(articleArr);
            tistory.setUrl(articleArr);
            tistory.setTitle(doc.select("h2.title-article").text());
            String[] contents = doc.select("span.date").text().split(" ");
            String s;
            s = contents[0]+contents[1]+contents[2];
            s = s.substring(0,(s.length()-1)).replace('.','-');
            tistory.setDate(s);
            
        }
        catch(Exception e){
            System.out.println("Error: Jsoup connect error(" + tistory + ")");
        }
        return tistory;
    }
}