package bit.com.a.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.springframework.stereotype.Component;

@Component	// 생성해 놓는다	
public class YoutubeParser {

	String urls = "https://www.youtube.com/results?search_query=";
	
	public String search(String s) {		
		System.out.println("검색:" + s);
		
		BufferedReader br = null;
		String str = "";
		
		try {
			String ss = URLEncoder.encode(s, "utf-8");
						
			URL url = new URL(urls + ss);
			
			br = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
			
			String msg = "";
			String text = "";
			
			while((msg = br.readLine())!= null) {
				text += msg.trim();
			}
			
		//	System.out.println(text);	// html
			
			// 시작 위치
			int pos = text.trim().indexOf("\"responseContext\"");
			
			// 끝 위치
			int endpos = text.indexOf("};", pos);
			
			str = text.substring(pos - 1, endpos + 1);	// json -> string
			
		//	System.out.println(str);
			
			str = str.replace("\"", "\\\"");	// " -> \"
			str = str.replace("\'", "\\\'");	// ' -> \'
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		return str;
	}
}








