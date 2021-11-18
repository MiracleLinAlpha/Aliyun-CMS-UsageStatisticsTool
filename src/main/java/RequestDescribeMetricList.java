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
    public static int retryNum = 6;



    public List<Object> HandleSingleThread(requestParams rp, ecsInfo ecsinfo, String StartTime, String EndTime, String Period){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Object> row = new ArrayList<>();
        Object cpuAvg,cpuMax,cpuMin,memAvg,memMax,memMin,timeTmpStart,timeTmpEnd;
        Map<String,Object> mapTemp = new HashMap<>();

        try {
            //筛选创建时间小于开始时间的ECS，将开始时间修改为创建时间
            String creationtimeUtc;
            creationtimeUtc = utc2Local.utc2Local(ecsinfo.getCreationTime(), "yyyy-MM-dd'T'HH:mm'Z'","yyyy-MM-dd HH:mm:ss");

            String UsefulStartTime;
            if(df.parse(StartTime).getTime() - df.parse(creationtimeUtc).getTime() <= 0) {
                //System.out.println(creationtimeUtc);
                UsefulStartTime = creationtimeUtc;

            }else{
                UsefulStartTime = StartTime;
            }

            //状态不为运行中，则返回状态
            if(!ecsinfo.getStatus().equals("Running")) {
                row.add("未运行");
                row.add("未运行");
                row.add("未运行");
                row.add("未运行");
                row.add("未运行");
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
                row.add("创建时间早于结束时间");
                row.add("创建时间早于结束时间");
                row.add("创建时间早于结束时间");
                row.add("创建时间早于结束时间");
                row.add("创建时间早于结束时间");
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
            timeTmpStart = mapTemp.get("start");
            timeTmpEnd = mapTemp.get("end");


            mapTemp = analysisResponse(rp,"memory_usedutilization",ecsinfo.getInstanceId(),String.valueOf(ecsinfo.getDepartment()),UsefulStartTime,EndTime,Period);
            memAvg = mapTemp.get("Avg");
            memMax = mapTemp.get("Max");
            memMin = mapTemp.get("Min");

            mapTemp = analysisResponse(rp,"IntranetInRate",ecsinfo.getInstanceId(),String.valueOf(ecsinfo.getDepartment()),UsefulStartTime,EndTime,Period);

            mapTemp = analysisResponse(rp,"IntranetOutRate",ecsinfo.getInstanceId(),String.valueOf(ecsinfo.getDepartment()),UsefulStartTime,EndTime,Period);


            row.add(cpuAvg);
            row.add(cpuMax);
            row.add(cpuMin);
            row.add(memAvg);
            row.add(memMax);
            row.add(memMin);

            row.add(TimeUtil.StampToTime((long)timeTmpStart));
            row.add(TimeUtil.StampToTime((long)timeTmpEnd));

            return row;


        }catch (Exception e){
            e.printStackTrace();
        }

        row.add("NULL");
        return row;
    }



    public Map<String, Object> analysisResponse(requestParams rp, String MetricName, String InstanceId, String organizationid, String StartTime, String EndTime, String Period) throws Exception {
        List<DatapointsEty> Datapointslist = new ArrayList<DatapointsEty>();
        float Avg,Max,Min,avgtemp,maxtemp,mintemp,availableNum;

        Datapointslist = handlesegmentationTime(rp,MetricName,InstanceId,organizationid,StartTime,EndTime,Period);

        //处理数据
        Map<String,Object> map = new HashMap<>();
        if(Datapointslist.size() != 0) {
            avgtemp = 0;
            maxtemp = 0;
            mintemp = 100;
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
            map.put("Avg", "服务器返回数据出错");
            map.put("Max", "服务器返回数据出错");
            map.put("Min", "服务器返回数据出错");
            map.put("start", "服务器返回数据出错");
            map.put("end", "服务器返回数据出错");
        }



        return map;

    }



    public List<DatapointsEty> handlesegmentationTime(requestParams rp, String MetricName, String InstanceId, String organizationid, String StartTime, String EndTime, String Period){
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
                        Datapointslist.addAll(handleResponse(rp,MetricName,InstanceId,organizationid,Time2,EndTime,Period));

                    } else {
                        //日期未到最后一段
                        //System.out.println(Time2 + " -- " + Time1);

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
        DatapointsEty de = new DatapointsEty();
        List<DatapointsEty> Datapointslist = new ArrayList<DatapointsEty>();
        String a,b,c;
        int k;


        try {
            //校验API
            while(true){
                retryNum--;
                if(retryNum == 0)
                    return null;
                responseJson = Cms_Api.DescribeMetricListByEcs(rp, MetricName, "acs_ecs_dashboard", InstanceId, organizationid, StartTime, EndTime, Period);
                if (responseJson.contains("false") || !responseJson.contains("Datapoints") || responseJson.contains("ServiceUnavailable")) {
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
}
