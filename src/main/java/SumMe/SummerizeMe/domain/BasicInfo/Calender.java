package SumMe.SummerizeMe.domain.BasicInfo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class Calender {
    private String date;
    private List<Map<String,Object>> works;
}