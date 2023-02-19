package SumMe.SummerizeMe.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
public class Blog{
    private String date;
    private String title;
    private String url;
    private List<String> keyword = new ArrayList<>();
}