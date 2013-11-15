package org.apache.http.examples.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
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
               .setDefaultCookieStore(cookieStore).disableRedirectHandling()
               .addInterceptorLast(new HttpRequestInterceptor()
               {

                  public void process(HttpRequest request, HttpContext context) throws HttpException, IOException
                  {
                     System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv "
                              + request.getRequestLine().getMethod() + " " + request.getRequestLine().getUri() + " "
                              + request.getRequestLine().getProtocolVersion());
                     for (Header h : request.getAllHeaders())
                     {
                        System.out.println("REQUEST HEADER = " + h.getName() + ": " + h.getValue());
                     }
                  }
               }).addInterceptorFirst(new HttpResponseInterceptor()
               {

                  public void process(HttpResponse response, HttpContext context) throws HttpException, IOException
                  {
                     System.out.println("---------------- " + response.getStatusLine() + " -----------------------");
                     for (Header h : response.getAllHeaders())
                     {
                        System.out.println("RESPONSE HEADER = " + h.getName() + ": " + h.getValue());
                     }
                     System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
                     System.out.println();
                     System.out.println();
                     System.out.println();
                  }
               }).build();

      String csfr = "";

      // LOGIN PAGE
      CloseableHttpResponse loginPageResponse = null;
      try
      {
         HttpGet getLoginPageRequest = new HttpGet("https://www.twilio.com/login");
         getLoginPageRequest
                  .addHeader("User-Agent",
                           "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.114 Safari/537.36");
         getLoginPageRequest.addHeader("Referer", "http://www.google.it");
         loginPageResponse = httpclient.execute(getLoginPageRequest);

         HttpEntity entity = loginPageResponse.getEntity();
         String responseString = EntityUtils.toString(entity);
         EntityUtils.consume(entity);

         // System.out.println(responseString);
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
         csfr = responseString
                  .substring(
                           responseString.indexOf("CSRF\" value=\"") + 13,
                           responseString.indexOf("<button type=\"submit\"")).trim();

      }
      catch (Throwable t)
      {
         t.printStackTrace();
      }
      finally
      {
         if (loginPageResponse != null)
         {
            try
            {
               loginPageResponse.close();
            }
            catch (Throwable tt)
            {
               tt.printStackTrace();
            }
         }
      }

      // LOGIN DATA
      CloseableHttpResponse loginDataResponse = null;
      try
      {
         HttpPost postLoginDataRequest = new HttpPost("https://www.twilio.com/login");
         postLoginDataRequest
                  .setHeader("User-Agent",
                           "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.114 Safari/537.36");
         postLoginDataRequest.setHeader("Referer", "https://www.twilio.com/login");
         // ADDITIONAL HEADERS
         postLoginDataRequest.setHeader("Accept",
                  "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
         postLoginDataRequest.setHeader("Accept-Charset",
                  "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
         postLoginDataRequest.setHeader("Accept-Encoding", "gzip,deflate,sdch");
         postLoginDataRequest.setHeader("Accept-Language", "en-US");
         postLoginDataRequest.setHeader("Cache-Control", "max-age=0");
         postLoginDataRequest.setHeader("Connection", "keep-alive");
         // httpost.setHeader("Content-Length", "114");
         postLoginDataRequest.setHeader("Content-Type",
                  "application/x-www-form-urlencoded"); //
         List<NameValuePair> nvps = new ArrayList<NameValuePair>();
         nvps.add(new BasicNameValuePair("email", "fiorenzino@gmail.com"));
         nvps.add(new BasicNameValuePair("password", "d10b01a"));
         nvps.add(new BasicNameValuePair("g", ""));
         nvps.add(new BasicNameValuePair("t", ""));
         nvps.add(new BasicNameValuePair("CSRF", csfr));

         postLoginDataRequest.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
         loginDataResponse = httpclient.execute(postLoginDataRequest);
         HttpEntity entity = loginDataResponse.getEntity();
         String responseString = EntityUtils.toString(entity);
         // System.out.println(responseString);
         EntityUtils.consume(entity);

         if (loginDataResponse.getStatusLine().getStatusCode() != 302)
         {
            System.out.println("FAILED");
         }
         else
         {
            CloseableHttpResponse accountPageResponse = null;
            try
            {
               String locationHeader = loginDataResponse.getFirstHeader("location").getValue();
               String redirectURL = "https://www.twilio.com" + locationHeader;
               // no auto-redirecting at client side, need manual send the request.
               HttpGet getAccountPageRequest = new HttpGet(redirectURL);
               getAccountPageRequest
                        .setHeader("User-Agent",
                                 "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/30.0.1599.114 Safari/537.36");
               getAccountPageRequest.setHeader("Referer", "https://www.twilio.com/login");
               // ADDITIONAL HEADERS
               getAccountPageRequest.setHeader("Accept",
                        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
               getAccountPageRequest.setHeader("Accept-Charset",
                        "ISO-8859-1,utf-8;q=0.7,*;q=0.3");
               getAccountPageRequest.setHeader("Accept-Encoding", "gzip,deflate,sdch");
               getAccountPageRequest.setHeader("Accept-Language", "en-US");
               getAccountPageRequest.setHeader("Cache-Control", "max-age=0");
               getAccountPageRequest.setHeader("Connection", "keep-alive");
               // httpost.setHeader("Content-Length", "114");
//               getAccountPageRequest.setHeader("Content-Type",
//                        "application/x-www-form-urlencoded"); //
               accountPageResponse = httpclient.execute(getAccountPageRequest);
               if (accountPageResponse.getStatusLine().getStatusCode() == 200)
               {
                  entity = accountPageResponse.getEntity();
                  responseString = EntityUtils.toString(entity);
                  System.out.println(responseString);
               }
               else
               {
                  System.out.println("FAILED");
               }
            }
            catch (Throwable w)
            {
               w.printStackTrace();
            }
            finally
            {
               if (accountPageResponse != null)
               {
                  try
                  {
                     accountPageResponse.close();
                  }
                  catch (Throwable ww)
                  {
                     ww.printStackTrace();
                  }
               }
            }

         }
      }
      catch (Throwable t)
      {
         t.printStackTrace();
      }
      finally
      {
         if (loginDataResponse != null)
         {
            try
            {
               loginDataResponse.close();
            }
            catch (Throwable tt)
            {
               tt.printStackTrace();
            }
         }
      }

      httpclient.close();

   }
}