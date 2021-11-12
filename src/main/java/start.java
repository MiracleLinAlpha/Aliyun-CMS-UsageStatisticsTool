import api.Ascm_Api;
import api.Ecs_Api;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.ecsInfo;
import entity.requestParams;
import util.ExcelUtils;
import util.FileUtil;
import util.ProgressBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class start {
    public static requestParams rp = new requestParams();
    public static String displayName;
    public static Scanner scan = new Scanner(System.in);
    public static String choose = "0";
    public static String StartTime;
    public static String EndTime;
    public static String Period = "3600";



    public static void main(String[] args) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            //读取配置文件
            String confJson = new FileUtil().readFileInSameFolder();
            if(confJson.equals("false")) {
                System.out.println("配置文件不存在，请确保conf.json与本程序jar包在同一目录后再重新运行 > _ <");
                return;
            }
            JsonNode confJn = mapper.readTree(confJson);
            rp = mapper.readValue(confJn.toString(), requestParams.class);



            //校验配置文件是否与系统AKSK匹配
            if(checkLogin() == true) {
                while(true) {
                    cls();
                    System.out.println(".____    .__                      \r\n"
                            + "|    |   |__| ____ ________ ____  \r\n"
                            + "|    |   |  |/    \\\\___   //    \\ \r\n"
                            + "|    |___|  |   |  \\/    /|   |  \\\r\n"
                            + "|_______ \\__|___|  /_____ \\___|  /\r\n"
                            + "        \\/       \\/      \\/    \\/ \n");
                    System.out.println("---------------------ECS使用率统计工具_异步请求版--------------------");
                    System.out.println("\n+ -- -- =>>用户:	" + displayName + "		<<\n");
                    System.out.println("\n---------------------------------------------------------\n");
                    System.out.println("\n---------------------------------------------------------\n");
//                    scan.nextLine();
                    while(true) {
                        System.out.println("请输入开始时间(2021-07-10 10:30:00)：");
                        StartTime = scan.nextLine();
                        if(StartTime.matches("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}")) {
                            break;
                        }
                        System.out.println("输入有误！");
                    }

                    while(true) {
                        System.out.println("\n请输入结束时间(2021-07-20 10:30:00)：");
                        EndTime = scan.nextLine();
                        if(EndTime.matches("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}")) {
                            break;
                        }
                        System.out.println("输入有误！");
                    }

                    System.out.println("\n开始时间: " + StartTime);
                    System.out.println("\n结束时间: " + EndTime);
                    System.out.println("\n	请选择：\n");
                    System.out.println("+ -- -- =(1、执行		)\n");
                    System.out.println("+ -- -- =(2、退出		)");
                    System.out.println("----------------------------------------------------");


                    choose = scan.next();
                    switch (choose) {
                        case "1":
                            execute();
                            return ;
                        case "2":

                            return ;
                        default:
                            cls();
                            System.out.println("输入有误，请重输！");
                            break;
                    }
                }
            } else {
                System.out.println("AKSK不配置，请检查后重新运行 > _ <");
                return;
            }

        }catch (Exception e) {
            System.out.println("program error");
        }
    }


    public static void execute() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            //读取所有ECS信息，遍历为实体类
            String temp = Ecs_Api.DescribeInstances(rp);
            JsonNode ecsjn = mapper.readTree(temp);
            ecsjn = ecsjn.get("Instances").get("Instance");

            List<ecsInfo> ecsinfolist = new ArrayList<>();
            for(JsonNode item:ecsjn) {
                ecsInfo ei = new ecsInfo();
                ei = (ecsInfo)mapper.readValue(item.toString(), ecsInfo.class);
                ecsinfolist.add(ei);
            }

            System.out.println("\n当前的ECS总数(包含K8S)：" + ecsinfolist.size());


            //创建表格
            ExcelUtils.createExcel();
            ExcelUtils.createSheet("Sheet1");
            ExcelUtils.addHeader(Arrays.asList("组织", "实例名称", "实例ID", "操作系统", "IP", "CPU核心数", "内存", "创建时间", "CPU平均使用率", "CPU最大使用率", "CPU最小使用率", "内存平均使用率", "内存最大使用率", "内存最小使用率", "开始时间", "截止时间")
                    ,false);

            int num = ecsinfolist.size();
//            int num = 100;
            int midNum = (int)(num/2);


            ProgressBar.printProgress_init();
            ProgressBar.printProgress_doing();

            CompletableFuture<List<List<Object>>> cf1 = CompletableFuture.supplyAsync(()->{
                List<Object> row = new ArrayList<>();
                List<List<Object>> rowList = new ArrayList<>();
                for (int j = 0; j < midNum; j++) {
                    row = RequestDescribeMetricList.HandleSingleThread(rp, ecsinfolist.get(j), StartTime, EndTime, Period);
                    rowList.add(row);
                }
                return rowList;
            });



            CompletableFuture<List<List<Object>>> cf2 = CompletableFuture.supplyAsync(()->{
                List<Object> row = new ArrayList<>();
                List<List<Object>> rowList = new ArrayList<>();
                try {
                    int temp1 = 0;
                    float progressNum = 100.0F / (num-midNum);
                    for(int j=0;j<(num-midNum);j++){
                        if ((int)(j * progressNum) % 10 == 0 && temp1 != (int)(j * progressNum)) {
                            ProgressBar.printProgress_doing();
                            temp1 = (int)(j * progressNum);
                        }
                        row = RequestDescribeMetricList.HandleSingleThread(rp,ecsinfolist.get(j+midNum),StartTime,EndTime,Period);
                        rowList.add(row);
                    }
                    return rowList;
                }catch (Exception e){
//                    e.printStackTrace();
                    return rowList;
                }

            });

            ProgressBar.printProgress_doing();
            List<List<Object>> addToAll = new ArrayList<>();
            addToAll.addAll(cf1.get());
            addToAll.addAll(cf2.get());
            int count = 0;
            for(List<Object> item:addToAll) {
                count++;
                ExcelUtils.insertRow(item,count);
            }

            ExcelUtils.exportExcelToSameFolder();


        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public static boolean checkLogin() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        try {
            String userinfojson = Ascm_Api.GetUserInfo(rp);
            JsonNode userinfojn = mapper.readTree(userinfojson);
            userinfojn = userinfojn.get("data").get("displayName");
            displayName = userinfojn.toString();
            rp.setDisplayName(displayName);

            return true;
        } catch (Exception e) {
            System.out.println("配置文件不匹配！");
            e.printStackTrace();
            return false;
        }
    }

    public static void cls() throws IOException, InterruptedException{
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }
}
