import util.ExcelUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class testExcel {
    public static void main(String[] args) throws Exception {

        ExcelUtils.createExcel();
        ExcelUtils.createSheet("test");
        List<String> title = new ArrayList<>();
        title.add("first");
        title.add("second");
        title.add("third");

        ExcelUtils.addHeader(title,false);
        List<Object> first = new ArrayList<>();
        first.add("1");
        first.add("11");
        first.add("111");
        ExcelUtils.insertRow(first,1);
        List<Object> second = new ArrayList<>();
        second.add("2");
        second.add("22");
        second.add("222");
        ExcelUtils.insertRow(second,2);

        ExcelUtils.insertRow(Arrays.asList("3", "33", "333"),3);

        ExcelUtils.exportExcel("D://test.xlsx");

    }
}
