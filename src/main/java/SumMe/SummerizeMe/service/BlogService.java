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
import java.util.Collections;
import java.util.Arrays;

import kr.bydelta.koala.kkma.Tagger;
import kr.bydelta.koala.data.Sentence;
import kr.bydelta.koala.data.Word;
import kr.bydelta.koala.data.Morpheme;


@Service
public class BlogService{

    private String Preposition[] = {"a","about","above","across","after","against","along",
    "amid","among","anti","around","as","at",
    "before","behind","below","beneath","beside","besides","between","beyond","but","by",
    "concerning","considering","despite","down","during","except","excepting",
    "excluding","following","for","from","in","inside","into","like","minus","near",
    "of","off","on","onto","opposite","outside","over","past","per","plus","regarding",
    "round","save","since","than","through","to","toward","towards","under",
    "underneath","unlike","until","up","upon","versus","via","with","within","without"};

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
                org.jsoup.nodes.Document doc = Jsoup.connect(link).get();
                Elements a = doc.select(".information span");
                temp.setDate(String.valueOf(a.text()).split("·")[1].replace("년 ","-").replace("월 ","-").replace("일",""));
                temp.setKeyword(getKeywords(doc.select("#root>div:nth-child(2)>div:nth-child(4)").text()));
                info.add(temp);
            }
            catch(Exception e){
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
            tistory.setKeyword(getKeywords(doc.select("div.article-view").text()));
        }
        catch(Exception e){
            System.out.println("Error: Jsoup connect error(" + tistory + ")");
        }
        return tistory;
    }

    private List<String> getKeywords(String text) {
        Map<String, Integer> wordCount = new HashMap<>();
        Tagger tagger = new Tagger();
        for(Sentence sentence : tagger.tag(text)) {
            for(Word word : sentence.getNouns()) {
                for(Morpheme morpheme : word) {
                    if(morpheme.isNoun()){
                        String wordString = morpheme.getSurface();
                        //wordString이 숫자인 경우 제외
                        if(wordString.length() <= 1&&wordString.matches("^[0-9.]*$"))
                            continue;
                        if(wordCount.containsKey(wordString)) {
                            wordCount.put(wordString, wordCount.get(wordString) + 1);
                        } else {
                            wordCount.put(wordString, 1);
                        }
                    }
                }
            }
        }

        String[] words2 = text.split(" ");
        for(String word : words2){
            if(word.length() > 1&&word.matches("^[a-zA-Z]*$")){
                if(Arrays.asList(Preposition).contains(word)){
                    continue;
                }
                if(wordCount.containsKey(word)) {
                    wordCount.put(word, wordCount.get(word) + 1);
                } else {
                    wordCount.put(word, 1);
                }
            }
        }

        List<String> keywords = new ArrayList<>();
        List<Map.Entry<String, Integer>> entries = new ArrayList<>(wordCount.entrySet());
        Collections.sort(entries, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        for(int i=0; i<5; i++){
            if(i<entries.size()){
                keywords.add(entries.get(i).getKey());
            }
        }
        return keywords;
    }
}