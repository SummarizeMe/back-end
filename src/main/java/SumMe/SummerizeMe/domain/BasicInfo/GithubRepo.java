package SumMe.SummerizeMe.domain.BasicInfo;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GithubRepo {
    private String addr;
    private String[] used_tech;
    private List<Map<String,String>> used_lang;
}