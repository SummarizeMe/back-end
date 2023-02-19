package SumMe.SummerizeMe.domain.BasicInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@ToString
public class Velog{
    private String date;
    private String title;
    private String url;
    private List<String> keyword = new ArrayList<>();

}
