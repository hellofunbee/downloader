package org.nutz.ssdb4j.biz;

import java.io.IOException;
import java.io.StringReader;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class JsonUtil {
	
	/**
	 * 解析JSON数据转为字符串
	 * @param json 待解析的JSON数据
	 * @return 返回解析完成的字符串
	 * @throws IOException
	 */
	public static String parseJson(String json) throws IOException {
		JsonReader reader = new JsonReader(new StringReader(json));
		reader.beginObject();
		String tagName = reader.nextName();
		if (tagName.equals("keyword"))
			return reader.nextString();
		reader.close();
		return null;
	}

	public static <T> T parse(String json, Class<T> clasz) {
		T obj=null;
		try{
			obj=JSON.parseObject(json,clasz);
		}catch(Exception e){
		}
		if(obj==null){
			Gson gson=new Gson();
			try{
				obj=gson.fromJson(json,clasz);
			}catch(Exception e){
			}
		}
		return obj;
		
	}
}
