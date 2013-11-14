/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.http.examples.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

/**
 * A example that demonstrates how HttpClient APIs can be used to perform form-based logon.
 */
public class ClientFormLogin
{

   public static void main(String[] args) throws Exception
   {

      BasicCookieStore cookieStore = new BasicCookieStore();
      CloseableHttpClient httpclient = HttpClients.custom()
               .setDefaultCookieStore(cookieStore).build();
      CloseableHttpResponse response = null;
      try
      {
         HttpGet httpget = new HttpGet("https://www.twilio.com/login");
         httpget.addHeader("User-Agent",
                  "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.114 Safari/537.36");
         httpget.addHeader("Referer", "http://www.google.it");
         response = httpclient.execute(httpget);
         String csfr = "";
         try
         {
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
            System.out.println("Login form get: " + response.getStatusLine());
            EntityUtils.consume(entity);

            // System.out.println(responseString);
            csfr = responseString
                     .substring(
                              responseString.indexOf("CSRF\" value=\"") + 13,
                              responseString.indexOf("<button type=\"submit\"")).trim();

         }
         finally
         {
            response.close();
         }

         // System.out.println("*****************************cookies LOAD 1 PAGE");
         List<Cookie> cookies = cookieStore.getCookies();
         // if (cookies.isEmpty())
         // {
         // System.out.println("None");
         // }
         // else
         // {
         // for (int i = 0; i < cookies.size(); i++)
         // {
         // System.out.println("- " + cookies.get(i).toString());
         // }
         // }
         // System.out.println("*****************************cookies LOAD 1 PAGE");
         /*
          * <form method="post">
          * 
          * <a href="/try-twilio" tabindex="5" class="btn btn-link pull-right">Sign up for free</a>
          * 
          * <h2>Ahoy hoy!</h2>
          * 
          * <input type="text" tabindex="1" placeholder="Email address" name="email" value="" required="">
          * 
          * <input type="password" tabindex="2" placeholder="Password" name="password" required="">
          * 
          * <input type="hidden" name="g" value=""> <input type="hidden" name="t" value=""> <input type="hidden"
          * name="CSRF" value=
          * "1383984979-97c0b0dc2907f2863f1896736e8152a4ed66c4f07572a26c0f57913323ad0c8e+1383984979-273de09f5c692a48c962ea80565f2b1a4bef6c86c6d452c2f5948263bfd08506"
          * >
          * 
          * <button type="submit" tabindex="3" class="btn btn-primary" value="Log in">Log in</button>
          * 
          * <a href="/reset-password" tabindex="4" class="btn btn-link">Forgot Password?</a>
          * 
          * </form>
          */

         HttpPost httpost = new HttpPost("https://www.twilio.com/login");
         httpost.setHeader("User-Agent",
                  "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.114 Safari/537.36");
         httpost.setHeader("Referer", "https://www.twilio.com/login");
         // ADDITIONAL HEADERS
         httpost.setHeader("Accept",
                  "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
         httpost.setHeader("Accept-Charset",
                  "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
         httpost.setHeader("Accept-Encoding", "gzip,deflate,sdch");
         httpost.setHeader("Accept-Language", "en-US");
         httpost.setHeader("Cache-Control", "max-age=0");
         httpost.setHeader("Connection", "keep-alive");
         // httpost.setHeader("Content-Length", "114");
         httpost.setHeader("Content-Type",
                  "application/x-www-form-urlencoded"); //
         List<NameValuePair> nvps = new ArrayList<NameValuePair>();
         nvps.add(new BasicNameValuePair("email", "xxxx@gmail.com"));
         nvps.add(new BasicNameValuePair("password", "xxxxxxxxx"));
         nvps.add(new BasicNameValuePair("g", ""));
         nvps.add(new BasicNameValuePair("t", ""));
         nvps.add(new BasicNameValuePair("CSRF", csfr));

         httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
         System.out.println("*****************************login REQUEST");
         for (org.apache.http.Header header : httpost.getAllHeaders())
         {
            System.out.println(header.getName() + " " + header.getValue());
         }
         System.out.println("*****************************login REQUEST");
         response = httpclient.execute(httpost);
         try
         {

            HttpEntity entity = response.getEntity();

            System.out.println("*****************************cookies LOAD LOGIN PAGE");
            cookies = cookieStore.getCookies();
            if (cookies.isEmpty())
            {
               System.out.println("None");
            }
            else
            {
               for (int i = 0; i < cookies.size(); i++)
               {
                  System.out.println(cookies.get(i).getName() + "=" + cookies.get(i).getValue());
               }
            }
            System.out.println("*****************************cookies LOAD LOGIN PAGE");
            System.out.println("*****************************HEADERS LOAD LOGIN PAGE");
            for (org.apache.http.Header header : response.getAllHeaders())
            {
               System.out.println(header.getName() + ": " + header.getValue());
            }
            System.out.println("*****************************HEADERS LOAD LOGIN PAGE");
            System.out.println("LOCALE:" + response.getLocale());
            System.out.println("PROTOCOL VERSION:" + response.getProtocolVersion());
            System.out.println("STATUS LINE:" + response.getStatusLine());
            System.out.println("PARAMS:" + response.getParams().toString());

            String responseString = EntityUtils.toString(entity);
            System.out.println("Login form get: " + response.getStatusLine());
            // System.out.println(responseString);
            EntityUtils.consume(entity);

            if (response.getStatusLine().getStatusCode() == 302)
            {
               String locationHeader = response.getFirstHeader("location").getValue();
               System.out.println("Location:" + locationHeader);
               String redirectURL = "https://www.twilio.com" + locationHeader;
               System.out.println("redirectURL: " + redirectURL);
               // no auto-redirecting at client side, need manual send the request.
               HttpGet request2 = new HttpGet(redirectURL);
               request2.addHeader("Referer", "https://www.twilio.com/login");
               httpost.setHeader("User-Agent",
                        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.114 Safari/537.36");
               httpost.setHeader("Referer", "https://www.twilio.com/login");
               // ADDITIONAL HEADERS
               httpost.setHeader("Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
               httpost.setHeader("Accept-Charset",
                        "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
               httpost.setHeader("Accept-Encoding", "gzip,deflate,sdch");
               httpost.setHeader("Accept-Language", "en-US");
               httpost.setHeader("Cache-Control", "max-age=0");
               httpost.setHeader("Connection", "keep-alive");
               // httpost.setHeader("Content-Length", "114");
               httpost.setHeader("Content-Type",
                        "application/x-www-form-urlencoded"); //

               System.out.println("*****************************ACCOUNT REQUEST");
               for (org.apache.http.Header header : request2.getAllHeaders())
               {
                  System.out.println("header" + header.getName() + " " + header.getValue());
               }
               System.out.println("*****************************ACCOUNT REQUEST");
               System.out.println("*****************************cookies LOAD ACCOUNT PAGE");
               cookies = cookieStore.getCookies();
               if (cookies.isEmpty())
               {
                  System.out.println("None");
               }
               else
               {
                  for (int i = 0; i < cookies.size(); i++)
                  {
                     System.out.println("- " + cookies.get(i).toString());
                  }
               }
               System.out.println("*****************************cookies LOAD ACCOUNT PAGE");
               response = httpclient.execute(request2);
               System.out.println("account get: " + response.getStatusLine());
               entity = response.getEntity();
               responseString = EntityUtils.toString(entity);
               // System.out.println(responseString);
               cookies = cookieStore.getCookies();
               if (cookies.isEmpty())
               {
                  System.out.println("None");
               }
               else
               {
                  for (int i = 0; i < cookies.size(); i++)
                  {
                     System.out.println("- " + cookies.get(i).toString());
                  }
               }
               System.out.println("*****************************cookies");
               for (org.apache.http.Header header : response.getAllHeaders())
               {
                  System.out.println("header" + header.getName() + " " + header.getValue());
               }
            }

            // httpget = new HttpGet("https://www.twilio.com/user/account");
            // CloseableHttpResponse response3 = httpclient.execute(httpget);
            //
            // entity = response3.getEntity();
            // String responseString = EntityUtils.toString(entity);
            // System.out.println(responseString);

         }
         finally
         {
            response.close();
         }
      }
      finally
      {
         httpclient.close();
      }
   }
}