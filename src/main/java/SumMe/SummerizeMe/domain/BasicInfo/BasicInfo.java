package SumMe.SummerizeMe.domain.BasicInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
public class BasicInfo {
    private List<GithubRepo> github_repos = new ArrayList<>();
    private List<Blog> blog = new ArrayList<>();
    private List<Calender> calender = new ArrayList<>();
    private List<Tistory> tistory = new ArrayList<>();

}