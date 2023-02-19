package SumMe.SummerizeMe.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class Calender {
    private String date;
    private List<Map<String,Object>> works;
}