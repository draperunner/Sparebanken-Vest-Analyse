package main;

import javafx.util.StringConverter;

import java.time.YearMonth;
import java.util.List;

/**
 * Created by mats on 26.07.2015.
 */
public class PieChartSliderStringConverter extends StringConverter<Double> {
    
    private List<YearMonth> months;
    
    public PieChartSliderStringConverter(List<YearMonth> months) {
        this.months = months;
    }
    
    @Override
    public String toString(Double n) {
        if (n.intValue() == months.size()) {
            return "Totalt";
        }
        if (n.intValue() == months.size() + 1) {
            return "Gjennomsnitt";
        }
        if (n.intValue() == months.size() + 2) {
            return "Median";
        }
        return months.get((int) Math.floor(n)).toString();
    }

    @Override
    public Double fromString(String s) {
        if (s.equals("Totalt")) {
            return (double) months.size();
        }
        if (s.equals("Gjennomsnitt")) {
            return (double) months.size() + 1;
        }
        if (s.equals("Median")) {
            return (double) months.size() + 2;
        }
        return (double) months.indexOf(months.stream().filter(m -> m.toString().equals(s)).findFirst().get());
    }
}
