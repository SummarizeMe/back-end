package SumMe.SummerizeMe.domain;

import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GithubRepo {
    private String url;
    private float contribution;
    private List<Map<String,Object>> used_lang;
}