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
    public List<String> getlinksforVelog(List<String> blog){
        return null;
    };

    public List<Blog> getinfofromVelog(List<String> blog){
        return null;
    };

    public List<Blog> crawlingTistory(List<String> blog){
        return null;
    };
}