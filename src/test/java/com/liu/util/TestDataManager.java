package com.liu.util;

import com.csvreader.CsvReader;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.liu.pojo.TestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试用例管理，负责从data.xlsx读取用例
 */
public class TestDataManager {
    public static final String FALSE = "FALSE";
    private static final Logger logger = LoggerFactory.getLogger(TestDataManager.class);
    //存贮所有测试用例，菜单、操作、测试用例
    public static Map<String, HashMap<String, ArrayList<TestData>>> testDataList = new HashMap<>();

    /**
     * 由于excel文件提交到gitlab上，不能diff，固换为csv
     *
     * @param module 模块名
     */
    public static void loadDataCsv(String module) {
        String csvFile = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "csv" + File.separator + module + ".csv";
        CsvReader csvReader = null;
        int i = 0;
        int column = 0;
        try {
//          为什么用gb2312,不是utf-8，是因为本地打开csv文件，保存时，默认保存编码为gb2312(即使原编码为utf8)
            csvReader = new CsvReader(new FileReader(csvFile, Charset.forName("GB2312")));
            csvReader.readHeaders();
            Gson gson = new Gson();
            while (csvReader.readRecord()) {
                i++;
                String[] strs = csvReader.getRawRecord().split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                //第5列,是否使用
                if (FALSE.equalsIgnoreCase(strs[4])) {
                    continue;
                }
                column = 0;
                String menu = strs[column++];
                String operate = strs[column++];
                String title = strs[column++];
                String data = strs[column++];
                if (data.contains(",")) {
                    data = data.substring(1, data.length() - 1);
                }
                Map<String, String> dataMap = gson.fromJson(data.trim(), new TypeToken<Map<String, String>>() {
                }.getType());
                TestData testData = new TestData(menu, operate, title, dataMap);
                HashMap<String, ArrayList<TestData>> map = testDataList.computeIfAbsent(menu, k -> new HashMap<>());
                ArrayList<TestData> list = map.computeIfAbsent(operate, k -> new ArrayList<>());
                list.add(testData);
            }
        } catch (Exception e) {
            logger.error("读取测试用例数据,第" + (++i) + "行，第" + column + "列数据有问题.", e);
        } finally {
            if (null != csvReader) {
                csvReader.close();
            }
        }
    }


    public static void main(String[] args) {
        ArrayList<TestData> arrayList;
        loadDataCsv("core");
        arrayList = testDataList.get("Dashboard").get("view");
        System.out.println(arrayList.get(0));
        loadDataCsv("cp");
        arrayList = testDataList.get("项目配置").get("view");
        System.out.println(arrayList.get(0));
        loadDataCsv("e2e");
        arrayList = testDataList.get("demo").get("demo");
        System.out.println(arrayList.get(0));
    }


}
