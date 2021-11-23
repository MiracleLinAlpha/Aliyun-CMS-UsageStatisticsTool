import api.Ascm_Api;
import api.Cms_Api;
import api.Ecs_Api;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.DatapointsEty;
import entity.diskInfo;
import entity.ecsInfo;
import entity.requestParams;
import util.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class go {

    public requestParams goCheckIn() {
        requestParams rp = new requestParams();
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            //读取配置文件
            String confJson = new FileUtil().readFileInSameFolder("conf.json");
            if(confJson.equals("false")) {
                System.out.println("配置文件不存在，请确保conf.json与本程序jar包在同一目录后再重新运行 > _ <");
                rp.setDisplayName("null");
                return rp;
            }
            JsonNode confJn = mapper.readTree(confJson);
            rp = mapper.readValue(confJn.toString(), requestParams.class);


            String userinfojson = Ascm_Api.GetUserInfo(rp);
            JsonNode userinfojn = mapper.readTree(userinfojson);
            userinfojn = userinfojn.get("data").get("displayName");
            rp.setDisplayName(userinfojn.toString());


            return rp;


        }catch (Exception e) {
//            e.printStackTrace();
            System.out.println("AKSK不正确，请检查后重新运行 > _ <");
            rp.setDisplayName("null");
            return rp;
        }
    }


    public boolean goNavi(requestParams rp) {
        try {
            Scanner scan = new Scanner(System.in);
            String StartTime,EndTime,choose = "0",Period = "3600";
            while(true) {
                cls();
                System.out.println(".____    .__                      \r\n"
                        + "|    |   |__| ____ ________ ____  \r\n"
                        + "|    |   |  |/    \\\\___   //    \\ \r\n"
                        + "|    |___|  |   |  \\/    /|   |  \\\r\n"
                        + "|_______ \\__|___|  /_____ \\___|  /\r\n"
                        + "        \\/       \\/      \\/    \\/ \n");
                System.out.println("---------------------ECS使用率统计工具_异步请求版------------------------");
                System.out.println("\n+ -- -- =>>用户:	" + rp.getDisplayName() + "		<<\n");
                System.out.println("\n-------------------------------------------------------------------------\n");
                System.out.println("\n-------------------------------------------------------------------------\n");
//                    scan.nextLine();
                while(true) {
                    System.out.println("请输入开始时间(2021-07-10 10:30:00)：");
                    StartTime = scan.nextLine();
                    if(StartTime.matches("^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")) {
                        break;
                    }
                    System.out.println("输入有误！");
                }

                while(true) {
                    System.out.println("\n请输入结束时间(2021-07-20 10:30:00)：");
                    EndTime = scan.nextLine();
                    if(EndTime.matches("^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)\\s+([01][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$")) {
                        break;
                    }
                    System.out.println("输入有误！");
                }

                System.out.println("\n开始时间: " + StartTime);
                System.out.println("\n结束时间: " + EndTime);
                System.out.println("\n	请选择：\n");
                System.out.println("+ -- -- =(1、基础信息	                                             	)\n");
                System.out.println("+ -- -- =(2、基础信息 + CPU	                                    	)\n");
                System.out.println("+ -- -- =(3、基础信息 + CPU + 内存         	                     	)\n");
                System.out.println("+ -- -- =(4、基础信息 + CPU + 内存 + 整体磁盘	                     	)\n");
                System.out.println("+ -- -- =(5、基础信息 + CPU + 内存 + 整体磁盘 + 内网带宽		)\n");
                System.out.println("+ -- -- =(6、基础信息 + 整体磁盘 + 内网带宽	                                   \n");
                System.out.println("+ -- -- =(9、退出		)");
                System.out.println("-------------------------------------------------------------------------");


                choose = scan.next();
                switch (choose) {
                    case "1":
                        goUnion(rp, StartTime, EndTime, Period,
                                Arrays.asList("base"));
                        return true;
                    case "2":
                        goUnion(rp, StartTime, EndTime, Period,
                                Arrays.asList("base","cpu"));
                        return true;
                    case "3":
                        goUnion(rp, StartTime, EndTime, Period,
                                Arrays.asList("base","cpu","mem"));
                        return true;
                    case "4":
                        goUnion(rp, StartTime, EndTime, Period,
                                Arrays.asList("base","cpu","mem","disk"));
                        return true;
                    case "5":
                        goUnion(rp, StartTime, EndTime, Period,
                                Arrays.asList("base","cpu","mem","disk","bandwidth"));
                        return true;
                    case "6":
                        goUnion(rp, StartTime, EndTime, Period,
                                Arrays.asList("base","disk","bandwidth"));
                        return true;
                    case "9":

                        return false;
                    default:
                        cls();
                        System.out.println("输入有误，请重输！");
                        break;
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public void goUnion(requestParams rp, String StartTime, String EndTime, String Period, List<String> args) {
        try {
            List<String> excelHeader = new ArrayList<>();
            List<List<Object>> excelList = new ArrayList<>();
            //创建表格
            ExcelUtils.createExcel();
            ExcelUtils.createSheet("Sheet1");
            //表头初始化
            excelHeader.addAll(Arrays.asList("组织", "实例名称", "实例ID", "操作系统", "IP", "CPU核心数", "内存", "磁盘总容量", "创建时间"));

            //进度条初始化
            ProgressBar.printProgress_init();

            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);


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


            //读取所有磁盘信息，遍历为实体类
            temp = Ecs_Api.DescribeDisks(rp);
            JsonNode diskjn = mapper.readTree(temp);
            diskjn = diskjn.get("Disks").get("Disk");

            List<diskInfo> diskInfoList = new ArrayList<>();
            for(JsonNode item:diskjn) {
                diskInfo di = new diskInfo();
                di = (diskInfo)mapper.readValue(item.toString(), diskInfo.class);
                diskInfoList.add(di);
            }


            ProgressBar.printProgress_doing();

            for(String arg:args) {
                switch (arg) {
                    case "cpu":
                        excelHeader.addAll(Arrays.asList("CPU平均使用率", "CPU最大使用率", "CPU最小使用率"));
                        break;
                    case "mem":
                        excelHeader.addAll(Arrays.asList("内存平均使用率", "内存最大使用率", "内存最小使用率"));
                        break;
                    case "disk":
                        excelHeader.addAll(Arrays.asList("磁盘总使用率"));
                        break;
                    case "bandwidth":
                        excelHeader.addAll(Arrays.asList("带宽平均值", "带宽最大值", "带宽最小值"));
                        break;
                }
                excelHeader.addAll(Arrays.asList("实际统计开始时间", "实际统计结束时间"));
            }




            int num = ecsinfolist.size();
            int midNum = (int)(num/2);

            //线程一
            CompletableFuture<List<List<Object>>> cf1 = CompletableFuture.supplyAsync(()->{
                List<List<Object>> cf1List = new ArrayList<>();
                try {
                    for(int i=0;i<midNum;i++) {
                        List<Object> tmpList = new ArrayList<>();
                        tmpList.addAll(goBaseInfo(rp,ecsinfolist.get(i),diskInfoList));
                        tmpList.addAll(goHandleStatistics(rp,StartTime,EndTime,Period,args,ecsinfolist.get(i),diskInfoList));
                        cf1List.add(tmpList);
                    }

                    return cf1List;
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return cf1List;
            });


            //线程二
            CompletableFuture<List<List<Object>>> cf2 = CompletableFuture.supplyAsync(()->{
                List<List<Object>> cf2List = new ArrayList<>();
                try {
                    int temp1 = 0;
                    float progressNum = 100.0F / (num-midNum);
                    for(int j=0;j<(num-midNum);j++) {
                        //进度条
                        if ((int)(j * progressNum) % 10 == 0 && temp1 != (int)(j * progressNum)) {
                            ProgressBar.printProgress_doing();
                            temp1 = (int)(j * progressNum);
                        }

                        List<Object> tmpList = new ArrayList<>();
                        tmpList.addAll(goBaseInfo(rp,ecsinfolist.get(j+midNum),diskInfoList));
                        tmpList.addAll(goHandleStatistics(rp,StartTime,EndTime,Period,args,ecsinfolist.get(j+midNum),diskInfoList));
                        cf2List.add(tmpList);
                    }

                    return cf2List;
                }catch (Exception e) {
                    e.printStackTrace();
                }
                return cf2List;
            });

            List<List<Object>> addToAll = new ArrayList<>();
            addToAll.addAll(cf1.get());
            addToAll.addAll(cf2.get());
            int count = 0;
            for(List<Object> item:addToAll) {
                count++;
                ExcelUtils.insertRow(item,count);
            }

            //结束进度条
            ProgressBar.printProgress_doing();
            //导出使用率到excel文件
            ExcelUtils.exportExcelToSameFolder("Usages-" + StartTime.replace(" ","-")  + "-" + EndTime.replace(" ","-") + ".xlsx");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    public List<Object> goBaseInfo(requestParams rp, ecsInfo ecsInfo, List<diskInfo> diskInfoList) {
        List<Object> oneRow = new ArrayList<>();
        try {
            oneRow.add(ecsInfo.getDepartmentName());
            oneRow.add(ecsInfo.getInstanceName());
            oneRow.add(ecsInfo.getInstanceId());
            oneRow.add(ecsInfo.getOSName());
            oneRow.add(ecsInfo.getNetworkInterfaces().getNetworkInterface().get(0).getPrimaryIpAddress());
            oneRow.add(ecsInfo.getCpu());
            oneRow.add(ecsInfo.getMemory()/1024);

            int totalDisk = 0;
            for(diskInfo item:diskInfoList) {
                if(item.getInstanceId().equals(ecsInfo.getInstanceId())) {
                    totalDisk+=item.getSize();
                }
            }
            oneRow.add(totalDisk);
            oneRow.add(utc2Local.utc2Local(ecsInfo.getCreationTime(), "yyyy-MM-dd'T'HH:mm'Z'","yyyy-MM-dd HH:mm:ss"));

            return oneRow;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return oneRow;
    }


    public List<Object> goHandleStatistics(requestParams rp, String StartTime, String EndTime, String Period, List<String> args, ecsInfo ecsInfo, List<diskInfo> diskInfoList) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Object> oneRow = new ArrayList<>();
        Object Avg = 0, Max = 0, Min = 0, StatisticStartTime = "null", StatisticEndTime = "null";
        Map<String,Object> mapTemp = new HashMap<>();

        try {
            //筛选创建时间小于开始时间的ECS，将开始时间修改为创建时间
            String creationtimeUtc;
            creationtimeUtc = utc2Local.utc2Local(ecsInfo.getCreationTime(), "yyyy-MM-dd'T'HH:mm'Z'","yyyy-MM-dd HH:mm:ss");

            String UsefulStartTime;
            if(df.parse(StartTime).getTime() - df.parse(creationtimeUtc).getTime() <= 0) {
                //System.out.println(creationtimeUtc);
                UsefulStartTime = creationtimeUtc;

            }else{
                UsefulStartTime = StartTime;
            }

            //状态不为运行中，则返回状态
            if(!ecsInfo.getStatus().equals("Running")) {
                oneRow.add("未运行");

                return oneRow;
            }


            //结束时间比创建时间早则返回大于结束时间
            if(df.parse(EndTime).getTime() - df.parse(UsefulStartTime).getTime() <= 0) {
                oneRow.add("创建时间早于结束时间");

                return oneRow;
            }



            for(String arg:args) {
                switch (arg) {
                    case "cpu":
                        mapTemp = goHandleArg(rp,UsefulStartTime,EndTime,Period,"cpu",ecsInfo,null);
                        oneRow.addAll(Arrays.asList(mapTemp.get("Avg"),mapTemp.get("Max"),mapTemp.get("Min")));
                        if(!StatisticStartTime.toString().equals("null"))
                            StatisticStartTime = mapTemp.get("StatisticStartTime");
                        if(!StatisticEndTime.toString().equals("null"))
                            StatisticEndTime = mapTemp.get("StatisticEndTime");
                        break;
                    case "mem":
                        mapTemp = goHandleArg(rp,UsefulStartTime,EndTime,Period,"mem",ecsInfo,null);
                        oneRow.addAll(Arrays.asList(mapTemp.get("Avg")));
                        if(!StatisticStartTime.toString().equals("null"))
                            StatisticStartTime = mapTemp.get("StatisticStartTime");
                        if(!StatisticEndTime.toString().equals("null"))
                            StatisticEndTime = mapTemp.get("StatisticEndTime");
                        break;
                    case "disk":
                        mapTemp = goHandleArg(rp,UsefulStartTime,EndTime,Period,"disk",ecsInfo,diskInfoList);
                        oneRow.addAll(Arrays.asList(mapTemp.get("Avg"),mapTemp.get("Max"),mapTemp.get("Min")));
                        if(!StatisticStartTime.toString().equals("null"))
                            StatisticStartTime = mapTemp.get("StatisticStartTime");
                        if(!StatisticEndTime.toString().equals("null"))
                            StatisticEndTime = mapTemp.get("StatisticEndTime");
                        break;
                    case "bandwidth":
                        mapTemp = goHandleArg(rp,UsefulStartTime,EndTime,Period,"bandwidth",ecsInfo,null);
                        oneRow.addAll(Arrays.asList(mapTemp.get("Avg"),mapTemp.get("Max"),mapTemp.get("Min")));
                        if(!StatisticStartTime.toString().equals("null"))
                            StatisticStartTime = mapTemp.get("StatisticStartTime");
                        if(!StatisticEndTime.toString().equals("null"))
                            StatisticEndTime = mapTemp.get("StatisticEndTime");
                        break;
                }

                oneRow.addAll(Arrays.asList(StatisticStartTime,StatisticEndTime));
            }

            return oneRow;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oneRow;
    }



    public Map<String,Object> goHandleArg(requestParams rp, String UsefulStartTime, String EndTime, String Period, String arg, ecsInfo ecsInfo, List<diskInfo> diskInfoList) {
        Map<String,Object> oneRow = new HashMap<>();
        Object Avg = 0, Max = 0, Min = 0;
        Map<String,Object> mapTemp = new HashMap<>();
        try {
            //正常处理
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
            mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);


            switch (arg) {
                case "cpu":
                    mapTemp = analysisResponse(rp,"CPUUtilization","cpu","",ecsInfo.getInstanceId(),String.valueOf(ecsInfo.getDepartment()),UsefulStartTime,EndTime,Period);
                    Avg = mapTemp.get("Avg");
                    Max = mapTemp.get("Max");
                    Min = mapTemp.get("Min");
                    break;
                case "mem":
                    mapTemp = analysisResponse(rp,"memory_usedutilization","mem","",ecsInfo.getInstanceId(),String.valueOf(ecsInfo.getDepartment()),UsefulStartTime,EndTime,Period);
                    Avg = mapTemp.get("Avg");
                    Max = mapTemp.get("Max");
                    Min = mapTemp.get("Min");
                    break;
                case "disk":
                    float diskUse = 0,diskTotal = 0,totalUse = 0;
                    List<String> diskPath = new ArrayList<>();
                    int totalDisk = 0;
                    for(diskInfo item:diskInfoList) {
                        if(item.getInstanceId().equals(ecsInfo.getInstanceId())) {
                            totalDisk+=item.getSize();
                            diskPath.add(item.getDevice()+ "-" + String.valueOf(item.getSize()));
                        }

                    }
                    if(diskPath.size() == 1) {
                        String strTmp[];
                        strTmp = diskPath.get(0).split("-");
                        mapTemp = analysisResponse(rp,"vm.DiskUtilization","disk",strTmp[0],ecsInfo.getInstanceId(),String.valueOf(ecsInfo.getDepartment()),UsefulStartTime,EndTime,Period);
                        if(mapTemp.get("Avg").equals("服务器返回数据出错")) {
                            oneRow.put("Avg","NULL");
                            return oneRow;
                        }
                        oneRow.put("Avg",totalUse);
                        oneRow.put("StatisticStartTime",TimeUtil.StampToTime((long)mapTemp.get("start")));
                        oneRow.put("StatisticEndTime",TimeUtil.StampToTime((long)mapTemp.get("end")));
                        return oneRow;
                    }else {
                        for(String item:diskPath) {
                            String strTmp[];
                            strTmp = item.split("-");
                            mapTemp = analysisResponse(rp,"vm.DiskUtilization","disk",strTmp[0],ecsInfo.getInstanceId(),String.valueOf(ecsInfo.getDepartment()),UsefulStartTime,EndTime,Period);
                            if(mapTemp.get("Avg").equals("服务器返回数据出错")) {
                                oneRow.put("Avg","NULL");
                                return oneRow;
                            }
                            if(!mapTemp.get("Avg").equals("NULL")) {
                                diskUse += Float.parseFloat(strTmp[1]) * (float)mapTemp.get("Avg");
                                diskTotal += Float.parseFloat(strTmp[1]);
                            }

                        }

                        totalUse = diskUse/diskTotal;

                        oneRow.put("Avg",totalUse);
                        oneRow.put("StatisticStartTime",TimeUtil.StampToTime((long)mapTemp.get("start")));
                        oneRow.put("StatisticEndTime",TimeUtil.StampToTime((long)mapTemp.get("end")));
                        return oneRow;
                    }
                case "bandwidth":
                    mapTemp = analysisResponse(rp,"IntranetInRate","bandwidth","",ecsInfo.getInstanceId(),String.valueOf(ecsInfo.getDepartment()),UsefulStartTime,EndTime,Period);
                    Avg = mapTemp.get("Avg");
                    Max = mapTemp.get("Max");
                    Min = mapTemp.get("Min");
                    mapTemp = analysisResponse(rp,"IntranetOutRate","bandwidth","",ecsInfo.getInstanceId(),String.valueOf(ecsInfo.getDepartment()),UsefulStartTime,EndTime,Period);

                    if(Avg.equals("结果为空")) {
                        oneRow.put("Avg","NULL");
                        return oneRow;
                    }

                    Avg = ((float)Avg + (float)mapTemp.get("Avg"))/2;
                    if((float)Max < (float)mapTemp.get("Max"))
                        Max = mapTemp.get("Max");
                    if((float)Min > (float)mapTemp.get("Min"))
                        Min = mapTemp.get("Min");

                    Avg = (float)Avg/1000000;
                    Max = (float)Max/1000000;
                    Min = (float)Min/1000000;
                    break;
            }

            if(Avg.equals("结果为空")) {
                oneRow.put("Avg","NULL");
                return oneRow;
            }

            oneRow.put("Avg",Avg);
            oneRow.put("Max",Max);
            oneRow.put("Min",Min);
            oneRow.put("StatisticStartTime",TimeUtil.StampToTime((long)mapTemp.get("start")));
            oneRow.put("StatisticEndTime",TimeUtil.StampToTime((long)mapTemp.get("end")));

            return oneRow;
        }catch (Exception e) {
            e.printStackTrace();
        }

        return oneRow;
    }





    public Map<String, Object> analysisResponse(requestParams rp, String MetricName, String key, String arg, String InstanceId, String organizationid, String StartTime, String EndTime, String Period) throws Exception {
        List<DatapointsEty> Datapointslist = new ArrayList<DatapointsEty>();
        float Avg,Max,Min,avgtemp,maxtemp,mintemp,availableNum;

        Datapointslist = handlesegmentationTime(rp,MetricName,key,arg,InstanceId,organizationid,StartTime,EndTime,Period);

        //处理数据
        Map<String,Object> map = new HashMap<>();
        if(Datapointslist.size() != 0) {
            avgtemp = 0;
            maxtemp = 0;
            mintemp = 100000000;
            availableNum = 0;


            for(DatapointsEty item:Datapointslist) {
                if(item.getAverage() != 0) {
                    avgtemp += item.getAverage();
                    availableNum++;
                }
                if(maxtemp < item.getMaximum()) {
                    maxtemp = item.getMaximum();
                }
                if(mintemp > item.getMinimum()) {
                    mintemp = item.getMinimum();
                }
            }



            if(availableNum == 0) {
                Avg = 0;
                Max = 0;
                Min = 0;
            }else {
                Avg = avgtemp/availableNum;
                BigDecimal bd = new BigDecimal(Avg);
                Avg = bd.setScale(2,  RoundingMode.HALF_UP).floatValue();

                Max = maxtemp;

                Min = mintemp;
            }


            map.put("Avg", Avg);
            map.put("Max", Max);
            map.put("Min", Min);
            map.put("start", Datapointslist.get(0).getTimestamp());
            map.put("end", Datapointslist.get(Datapointslist.size()-1).getTimestamp());

        } else {
            map.put("Avg", "结果为空");
            map.put("Max", "结果为空");
            map.put("Min", "结果为空");
            map.put("start", "结果为空");
            map.put("end", "结果为空");
        }



        return map;

    }





    public List<DatapointsEty> handlesegmentationTime(requestParams rp, String MetricName, String key, String arg, String InstanceId, String organizationid, String StartTime, String EndTime, String Period){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<DatapointsEty> Datapointslist = new ArrayList<DatapointsEty>();
        String Time1,Time2;
        int k;

        try {
            //开始时间到结束时间超过一个月，则分月请求处理
            //30天时间间隔为2592000000L  segmentation
            if(df.parse(EndTime).getTime() - df.parse(StartTime).getTime() > 2592000000L) {
                long segmentationTime = df.parse(EndTime).getTime() - df.parse(StartTime).getTime();
                Time1 = StartTime;
                int frequency = (int) (segmentationTime/2592000000L);
                for (k=frequency;k>=0;k--) {

                    Time2 = Time1;
                    Time1 = TimeUtil.StampToTime(TimeUtil.TimeToStamp(Time1) + 2592000000L);

                    //最后一段   日期
                    if (k == 0){
                        //System.out.println(Time2 + " -- " + EndTime);
                        if(Time2.equals(EndTime))
                            break;
                        Datapointslist.addAll(handleResponse(rp,MetricName,key,arg,InstanceId,organizationid,Time2,EndTime,Period));

                    } else {
                        //日期未到最后一段
                        //System.out.println(Time2 + " -- " + Time1);

                        Datapointslist.addAll(handleResponse(rp,MetricName,key,arg,InstanceId,organizationid,Time2,Time1,Period));
                    }

                }

            } else {
                //开始时间到结束时间小于或等于一个月，单次请求处理
                Datapointslist = handleResponse(rp,MetricName,key,arg,InstanceId,organizationid,StartTime,EndTime,Period);
            }




            return Datapointslist;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }




    public List<DatapointsEty> handleResponse(requestParams rp, String MetricName, String key, String arg, String InstanceId, String organizationid, String StartTime, String EndTime, String Period){
        DatapointsEty de = new DatapointsEty();
        List<DatapointsEty> Datapointslist = new ArrayList<DatapointsEty>();
        String a,b,c;
        int k;


        try {
            //校验API
            String responseJson = Cms_Api.DescribeMetricListByEcs(rp, MetricName, "acs_ecs_dashboard", key, arg, InstanceId, organizationid, StartTime, EndTime, Period);
            while (responseJson.contains("false") || !responseJson.contains("Datapoints") || responseJson.contains("ServiceUnavailable")) {
                Thread.sleep( 1000 );
                responseJson = Cms_Api.DescribeMetricListByEcs(rp, MetricName, "acs_ecs_dashboard", key, arg, InstanceId, organizationid, StartTime, EndTime, Period);
            }



            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_DEFAULT);
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);


            JsonNode datajn = mapper.readTree(responseJson);
            String temp = datajn.get("Datapoints").toString();
            temp = temp.substring(1, temp.length() - 1);
            temp = temp.replace("\\", "");
            datajn = mapper.readTree(temp);

            //遍历返回json中的Datapoints字段，过滤无效结果,并反序列化为实体类
            for(JsonNode item:datajn) {
                temp = item.toString();
                if (datajn.size() == 0 || temp.contains("?") || temp.contains("Average\":\"")) {
                    //System.out.println("返回错误 替换相关值为0");

                    k = temp.indexOf("\"Average");

                    a = temp.substring(0, k);

                    b = temp.substring(k + 13);

                    c = a.concat("\"Average\":0");

                    c = c.concat(b);

                    temp = c;
                    //System.out.println(temp);
                }

                de = mapper.readValue(temp, DatapointsEty.class);
                Datapointslist.add(de);
            }


            return Datapointslist;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public void cls() throws Exception{
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
    }
}
