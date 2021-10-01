package api;

import java.util.HashMap;
import java.util.Map;

import com.aliyun.asapi.ASClient;

import entity.requestParams;

public class Ecs_Api {

	//获取所有磁盘信息
	public static String DescribeDisks(requestParams rp) {
    	try {
    		Map<String, Object> requestParams = new HashMap<String, Object>();
     		requestParams.put("action", "DescribeDisks");
     		requestParams.put("product", "Ecs");
		    requestParams.put("Version", "2014-05-26");
     		requestParams.put("RegionId", rp.getRegionId());
     	    requestParams.put("AccessKeyId", rp.getAccessKeyId());
     	    requestParams.put("AccessKeySecret", rp.getAccessKeySecret());
     	    requestParams.put("regionId", rp.getRegionId());


     		ASClient client = new ASClient();
     		client.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
     		String result = client.doRequest(rp.getApiGateWay(), requestParams);

//     		System.out.println("DescribeDisks API Success!");
     		return result;

     	} catch (Exception e) {
     		System.out.println("DescribeDisks API Error!");
     		e.printStackTrace();
     		return null;
     	}
    }

	//获取所有ECS实例
	public static String DescribeInstances(requestParams rp) {
    	try {
		    Map<String, Object> rpmap = new HashMap<String, Object>();
		    rpmap.put("action", "DescribeInstances");
		    rpmap.put("product", "Ecs");
		    rpmap.put("Version", "2014-05-26");
		    rpmap.put("RegionId", rp.getRegionId());
		    rpmap.put("AccessKeyId", rp.getAccessKeyId());
		    rpmap.put("AccessKeySecret", rp.getAccessKeySecret());

     		ASClient client = new ASClient();
     		client.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
     		String result = client.doRequest(rp.getApiGateWay(), rpmap);

     		//System.out.println("DescribeInstances API Success!");
     		return result;
     	} catch (Exception e) {
     		System.out.println("DescribeInstances API Error!");
     		e.printStackTrace();
     		return null;
     	}
    }
}
