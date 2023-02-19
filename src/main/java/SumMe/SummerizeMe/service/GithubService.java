package SumMe.SummerizeMe.service;

import SumMe.SummerizeMe.domain.GithubRepo;
import SumMe.SummerizeMe.domain.Calender;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@Service
public class GithubService{
    @Value("${github.token}")
    private String token;

    private JSONArray crawlingUserCommits(String name){
        JSONArray items = new JSONArray();
        JSONParser jsonParser = new JSONParser();
        int total_page = 0;
        try{
            Document doc = Jsoup
            .connect("https://api.github.com/search/commits?q=author:"+name)
            .ignoreContentType(true)
            .get();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(doc.text());
            total_page = (Integer.parseInt(jsonObject.get("total_count").toString())-1)/100+1;
        }catch(Exception e){
            e.printStackTrace();
        }
        for(int i=1; i<=total_page; i++){
            try{
                Document doc = Jsoup
                .connect("https://api.github.com/search/commits?q=author:"+name+"&sort=author-date&per_page=100&page="+i)
                .ignoreContentType(true)
                .get();
                JSONObject jsonObject = (JSONObject) jsonParser.parse(doc.text());
                JSONArray temp = (JSONArray) jsonObject.get("items");
                items.addAll(temp);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return items;
    }

    private String getNameFromGithubUrl(String url){
        if(url.matches("https://github.com/[a-zA-Z0-9-_]+"))
            return url.substring(19);
        return null;
    }

    public List<GithubRepo> crawlingGithubRepos(List<String> githubs){
        List<GithubRepo> repos = new ArrayList<GithubRepo>();
        Set<String> repoUrls = new HashSet<String>();
        JSONParser jsonParser = new JSONParser();
        for(String github : githubs){
            String name = getNameFromGithubUrl(github);
            if(name == null) continue;
            System.out.println(name);
            JSONArray items = crawlingUserCommits(name);
            for(var item : items){
                JSONObject jsonRepo = (JSONObject) ((JSONObject)item).get("repository");
                String languagesUrl = (String) jsonRepo.get("languages_url");
                String contributorsUrl = (String) jsonRepo.get("contributors_url");
                String url = (String) jsonRepo.get("html_url");
                if(!repoUrls.contains(url)){
                    repoUrls.add(url);
                    GithubRepo repo = new GithubRepo();
                    repo.setUrl(url);
                    repo.setContribution(crawlingContibutor(contributorsUrl,name));
                    repo.setUsed_lang(crawlingUsedLang(languagesUrl));
                    repos.add(repo);
                }
            }
        }
        return repos;
    };

    private float crawlingContibutor(String url,String name){
        float contribution = 0;
        float total = 0;
        JSONParser jsonParser = new JSONParser();
        try{
            Document doc = Jsoup
            .connect(url)
            .header("Authorization", "Token "+token)
            .ignoreContentType(true)
            .get();
            JSONArray jsonArray = (JSONArray) jsonParser.parse(doc.text());
            for(var item : jsonArray){
                JSONObject jsonObject = (JSONObject) item;
                if(jsonObject.get("login").toString().equals(name)){
                    contribution = Float.parseFloat(jsonObject.get("contributions").toString());
                }
                total += Float.parseFloat(jsonObject.get("contributions").toString());
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return contribution*100/total;
    }

    private List<Map<String,Object>> crawlingUsedLang(String url){
        List<Map<String,Object>> used_lang = new ArrayList<Map<String,Object>>();
        JSONParser jsonParser = new JSONParser();
        try{
            Document doc = Jsoup
            .connect(url)
            .header("Authorization", "Token "+token)
            .ignoreContentType(true)
            .get();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(doc.text());
            Set<String> keys = jsonObject.keySet();
            int sum = 0;
            for(String key : keys){
                sum += Integer.parseInt(jsonObject.get(key).toString());
            }
            for(String key : keys){
                Map<String,Object> lang = new HashMap<String,Object>();
                lang.put("lang",key);
                lang.put("percent",Float.parseFloat(jsonObject.get(key).toString())*100/sum);
                used_lang.add(lang);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return used_lang;
    }

    public List<Map<String,Object>> crawlingMonthlyCommits(List<String> github){
        List<Map<String,Object>> monthly_commits = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        for(String url : github){
            String name = getNameFromGithubUrl(url);
            if(name == null) continue;
            JSONArray items = crawlingUserCommits(name);
            for(var item : items){
                JSONObject jsonObject = (JSONObject) item;
                String date = ((JSONObject)((JSONObject)jsonObject.get("commit")).get("author")).get("date").toString().substring(0,10);
                String year = date.substring(0,4);
                String month = date.substring(5,7);
                boolean isExist = false;
                for(var commit : monthly_commits){
                    if(commit.get("year").toString().equals(year) && commit.get("month").toString().equals(month)){
                        commit.put("count",Integer.parseInt(commit.get("count").toString())+1);
                        isExist = true;
                        break;
                    }
                }
                if(!isExist){
                    Map<String,Object> commit = new HashMap<>();
                    commit.put("year",year);
                    commit.put("month",month);
                    commit.put("count",1);
                    monthly_commits.add(commit);
                }
            }
        }

        return monthly_commits;
    };

    public List<Calender> createCalender(List<String> github){
        List<Calender> calenders = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        for(String url : github){
            String name = getNameFromGithubUrl(url);
            if(name == null) continue;
            JSONArray items = crawlingUserCommits(name);
            for(var item : items){
                JSONObject jsonObject = (JSONObject) item;
                String date = ((JSONObject)((JSONObject)jsonObject.get("commit")).get("author")).get("date").toString().substring(0,10);
                String year = date.substring(0,4);
                String month = date.substring(5,7);
                String day = date.substring(8,10);
                boolean isExist = false;
                for(var calender : calenders){
                    if(calender.getDate().equals(date)){
                        boolean isExist2 = false;
                        for(var commit : calender.getWorks()){
                            if(commit.get("repo").toString().equals(((JSONObject)jsonObject.get("repository")).get("html_url").toString())){
                                commit.put("count",Integer.parseInt(commit.get("count").toString())+1);
                                isExist2 = true;
                                break;
                            }
                        }
                        if(!isExist2){
                            Map<String,Object> commit = new HashMap<>();
                            commit.put("repo",((JSONObject)jsonObject.get("repository")).get("html_url").toString());
                            commit.put("count",1);
                            calender.getWorks().add(commit);
                        }
                        isExist = true;
                        break;
                    }
                }
                if(!isExist){
                    Calender calender = new Calender();
                    calender.setDate(date);
                    calender.setWorks(new ArrayList<>());
                    Map<String,Object> commit = new HashMap<>();
                    commit.put("repo",((JSONObject)jsonObject.get("repository")).get("html_url").toString());
                    commit.put("count",1);
                    calender.getWorks().add(commit);
                    calenders.add(calender);
                }
            }
        }
        return calenders;
    };
}