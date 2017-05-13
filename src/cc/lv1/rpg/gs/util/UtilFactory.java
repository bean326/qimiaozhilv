package cc.lv1.rpg.gs.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class UtilFactory 
{
	public static String getIPArea(String ip)
    {
		if(ip.indexOf(":") != -1)
		{
			ip = ip.replace("/", "");
			ip = ip.substring(0, ip.indexOf(":"));
		}
	    String REQUEST_URL = "http://www.ip138.com/ips.asp" ;
	    ///////有时查询不出来，就用这个URL    http://www.ip138.com/ips8.asp
	    String REQUEST_MOTHOD = "POST" ;
	    HttpURLConnection httpConn = null ;
	    String requestParameter = "ip=" + ip + "&action=2" ;
	    String IPArea = "" ;
	    BufferedReader br = null ;
        try 
         {
          httpConn = (HttpURLConnection) new  URL(REQUEST_URL).openConnection();
          httpConn.setRequestMethod(REQUEST_MOTHOD);
          httpConn.setDoOutput( true );
          httpConn.getOutputStream().write(requestParameter.getBytes());
          httpConn.getOutputStream().flush();
          httpConn.getOutputStream().close();
          
          br = new  BufferedReader( new  InputStreamReader(httpConn.getInputStream(), "gb2312" ));
          String lineStr = null ;
           while ((lineStr = br.readLine()) != null )
            {
        	   if (lineStr.contains("<td align=\"center\"><ul class=\"ul1\"><li>"))
                {
                  IPArea = lineStr.substring(lineStr.indexOf( "：" ) + 1 ,lineStr.indexOf( "</" ));
                   break ;
              } 
          } 
       } 
        catch (IOException e)   {
          return "";
       } 
       finally 
         {
           if (br != null )
               try    {
                  br.close();
              }   catch  (IOException e)   {
                  return "";
              } 
      } 
      return  IPArea;
  } 


}
