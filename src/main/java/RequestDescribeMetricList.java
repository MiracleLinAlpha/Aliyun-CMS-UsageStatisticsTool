import api.Ascm_Api;
import api.Cms_Api;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import entity.DatapointsEty;
import entity.ecsInfo;
import entity.requestParams;
import util.TimeUtil;
import util.utc2Local;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestDescribeMetricList {
    public static requestParams rp = new requestParams();
    public static Ascm_Api aa = new Ascm_Api();
    public static Cms_Api ca = new Cms_Api();
    public static float Avg;
    public static float Max;
    public static float Min;
    public static float avgtemp;
    public static float maxtemp;
    public static float mintemp;
    public static int availableNum;
    public static int i,k;
    public static String tempstring,a,b,c;
    public static DatapointsEty de = new DatapointsEty();
    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static long segmentationTime;
    public static String Time1,Time2;
    public static TimeUtil timeutil = new TimeUtil();
    public static utc2Local u2l = new utc2Local();



    public List<Object> HandleSingleThread(requestParams rp, ecsInfo ecsinfo, String StartTime, String EndTime, String Period){
        List<Object> row = new ArrayList<>();
        float cpuAvg;
        float cpuMax;
        float cpuMin;
        float memAvg;
        float memMax;
        float memMin;
        Map<String,Float> mapTemp = new HashMap<>();

        try {
            //筛选创建时间小于开始时间的ECS，将开始时间修改为创建时间
            String creationtimeUtc;
            creationtimeUtc = u2l.utc2Local(ecsinfo.getCreationTime(), "yyyy-MM-dd'T'HH:mm'Z'","yyyy-MM-dd HH:mm:ss");

            String UsefulStartTime;
            if(df.parse(StartTime).getTime() - df.parse(creationtimeUtc).getTime() <= 0) {
                //System.out.println(creationtimeUtc);
                UsefulStartTime = creationtimeUtc;

            }else{
                UsefulStartTime = StartTime;
            }

            //状态不为运行中，则返回状态
            if(ecsinfo.getStatus().equals("Running") == false) {
                row.add(ecsinfo.getDepartmentName());
                row.add(ecsinfo.getInstanceName());
                row.add(ecsinfo.getInstanceId());
                row.add(ecsinfo.getOSName());
                row.add(ecsinfo.getNetworkInterfaces().getNetworkInterface().get(0).getPrimaryIpAddress());
                row.add(ecsinfo.getCpu());
                row.add(ecsinfo.getMemory()/1024);
                row.add(u2l.utc2Local(ecsinfo.getCreationTime(), "yyyy-MM-dd'T'HH:mm'Z'","yyyy-MM-dd HH:mm:ss"));
                row.add("未运行");
                row.add("未运行");
                row.add("未运行");
                row.add("未运行");
                row.add("未运行");
                row.add("未运行");

                return row;
            }


            //结束时间比创建时间早则返回大于结束时间
            if(df.parse(EndTime).getTime() - df.parse(UsefulStartTime).getTime() <= 0) {
                row.add(ecsinfo.getDepartmentName());
                row.add(ecsinfo.getInstanceName());
                row.add(ecsinfo.getInstanceId());
                row.add(ecsinfo.getOSName());
                row.add(ecsinfo.getNetworkInterfaces().getNetworkInterface().get(0).getPrimaryIpAddress());
                row.add(ecsinfo.getCpu());
                row.add(ecsinfo.getMemory()/1024);
                row.add(u2l.utc2Local(ecsinfo.getCreationTime(), "yyyy-MM-dd'T'HH:mm'Z'","yyyy-MM-dd HH:mm:ss"));
                row.add("创建时间早于结束时间");
                row.add("创建时间早于结束时间");
                row.add("创建时间早于结束时间");
                row.add("创建时间早于结束时间");
                row.add("创建时间早于结束时间");
                row.add("创建时间早于结束时间");

                return row;
            }

            //正常处理
            mapTemp = analysisResponse(rp,"CPUUtilization",ecsinfo.getInstanceId(),String.valueOf(ecsinfo.getDepartment()),UsefulStartTime,EndTime,Period);
            cpuAvg = mapTemp.get("Avg");
            cpuMax = mapTemp.get("Max");
            cpuMin = mapTemp.get("Min");

            mapTemp = analysisResponse(rp,"memory_usedutilization",ecsinfo.getInstanceId(),String.valueOf(ecsinfo.getDepartment()),UsefulStartTime,EndTime,Period);
            memAvg = mapTemp.get("Avg");
            memMax = mapTemp.get("Max");
            memMin = mapTemp.get("Min");

            row.add(ecsinfo.getDepartmentName());
            row.add(ecsinfo.getInstanceName());
            row.add(ecsinfo.getInstanceId());
            row.add(ecsinfo.getOSName());
            row.add(ecsinfo.getNetworkInterfaces().getNetworkInterface().get(0).getPrimaryIpAddress());
            row.add(ecsinfo.getCpu());
            row.add(ecsinfo.getMemory()/1024);
            row.add(u2l.utc2Local(ecsinfo.getCreationTime(), "yyyy-MM-dd'T'HH:mm'Z'","yyyy-MM-dd HH:mm:ss"));
            row.add(cpuAvg);
            row.add(cpuMax);
            row.add(cpuMin);
            row.add(memAvg);
            row.add(memMax);
            row.add(memMin);

            return row;


        }catch (Exception e){
//            e.printStackTrace();
        }

        row.add("NULL");
        return row;
    }



    public Map<String, Float> analysisResponse(requestParams rp, String MetricName, String InstanceId, String organizationid, String StartTime, String EndTime, String Period) throws Exception {
        List<DatapointsEty> Datapointslist = new ArrayList<DatapointsEty>();

        Datapointslist = handlesegmentationTime(rp,MetricName,InstanceId,organizationid,StartTime,EndTime,Period);




        //处理数据

        avgtemp = 0;
        maxtemp = 0;
        mintemp = 100;
        availableNum = 0;

        for(i=0;i<Datapointslist.size();i++) {
            if(Datapointslist.get(i).getAverage() != 0) {
                avgtemp += Datapointslist.get(i).getAverage();
                availableNum++;
            }
            if(maxtemp < Datapointslist.get(i).getMaximum()) {
                maxtemp = Datapointslist.get(i).getMaximum();
            }
            if(mintemp > Datapointslist.get(i).getMinimum()) {
                mintemp = Datapointslist.get(i).getMinimum();
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

        Map<String,Float> map = new HashMap<>();
        map.put("Avg", Avg);
        map.put("Max", Max);
        map.put("Min", Min);

        return map;

    }


    public List<DatapointsEty> handlesegmentationTime(requestParams rp, String MetricName, String InstanceId, String organizationid, String StartTime, String EndTime, String Period){
        List<DatapointsEty> Datapointslist = new ArrayList<DatapointsEty>();


        try {
            //开始时间到结束时间超过一个月，则分月请求处理
            //30天时间间隔为2592000000L  segmentation
            if(df.parse(EndTime).getTime() - df.parse(StartTime).getTime() > 2592000000L) {
                segmentationTime = df.parse(EndTime).getTime() - df.parse(StartTime).getTime();
                Time1 = StartTime;
                Time2 = Time1;
                int frequency = (int) (segmentationTime/2592000000L);
                for (k=frequency;k>=0;k--) {

                    Time2 = Time1;
                    Time1 = timeutil.StampToTime(timeutil.TimeToStamp(Time1) + 2592000000L);

                    //最后一段   日期
                    if (k == 0){
//                        System.out.println(Time2 + " -- " + EndTime);

                        Datapointslist.addAll(handleResponse(rp,MetricName,InstanceId,organizationid,Time2,EndTime,Period));

                    } else {
                        //日期未到最后一段
//                        System.out.println(Time2 + " -- " + Time1);

                        Datapointslist.addAll(handleResponse(rp,MetricName,InstanceId,organizationid,Time2,Time1,Period));
                    }

                }

            } else {
                //开始时间到结束时间小于或等于一个月，单次请求处理
                Datapointslist = handleResponse(rp,MetricName,InstanceId,organizationid,StartTime,EndTime,Period);
            }

            return Datapointslist;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    public List<DatapointsEty> handleResponse(requestParams rp, String MetricName, String InstanceId, String organizationid, String StartTime, String EndTime, String Period){
        String responseJson = "";
        List<DatapointsEty> Datapointslist = new ArrayList<DatapointsEty>();


        try {
            //校验API
//            responseJson = ca.DescribeMetricListByEcs(rp, MetricName, "acs_ecs_dashboard", InstanceId, organizationid, StartTime, EndTime, Period);
//            if (responseJson.indexOf("false") != -1 || responseJson.indexOf("Datapoints") == -1 || responseJson.indexOf("ServiceUnavailable") != -1) {
//                //System.out.println("API调用错误,重试一次!");
//                responseJson = ca.DescribeMetricListByEcs(rp, MetricName, "acs_ecs_dashboard", InstanceId, organizationid, StartTime, EndTime, Period);
//                if (responseJson.indexOf("false") != -1 || responseJson.indexOf("Datapoints") == -1 || responseJson.indexOf("ServiceUnavailable") != -1) {
//                    //System.out.println("API调用错误,重试二次!");
//                    responseJson = ca.DescribeMetricListByEcs(rp, MetricName, "acs_ecs_dashboard", InstanceId, organizationid, StartTime, EndTime, Period);
//                    if (responseJson.indexOf("false") != -1 || responseJson.indexOf("Datapoints") == -1 || responseJson.indexOf("ServiceUnavailable") != -1) {
//                        System.out.println("API调用错误,跳过!统计参数将设置为0!");
//                        return null;
//                    }
//
//                }
//            }
            int count = 5;
            while(true){
                count--;
                if(count == 0)
                    return null;
                responseJson = ca.DescribeMetricListByEcs(rp, MetricName, "acs_ecs_dashboard", InstanceId, organizationid, StartTime, EndTime, Period);
                if (responseJson.indexOf("false") != -1 || responseJson.indexOf("Datapoints") == -1 || responseJson.indexOf("ServiceUnavailable") != -1) {
                    continue;
                }
                break;
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
            for (i = 0; i < datajn.size(); i++) {
                tempstring = datajn.get(i).toString();
                if (datajn.size() == 0 || tempstring.contains("?") || tempstring.contains("Average\":\"")) {
//                    System.out.println("返回错误 替换相关值为0");

                    k = tempstring.indexOf("\"Average");

                    a = tempstring.substring(0, k);

                    b = tempstring.substring(k + 13, tempstring.length());

                    c = a.concat("\"Average\":0");

                    c = c.concat(b);

                    tempstring = c;
//                    System.out.println(tempstring);
                }

                de = mapper.readValue(tempstring, DatapointsEty.class);
                Datapointslist.add(de);
            }
            return Datapointslist;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
