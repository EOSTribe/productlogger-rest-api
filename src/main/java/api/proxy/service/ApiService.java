package api.proxy.service;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;


import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ApiService {

    private static final String AGENT_NAME = "ProductLogger API Service";

    public static ApiResponse call(String apiUrl, String requestJSON) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        StringEntity se = new StringEntity(requestJSON);
        se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
        HttpPost post = new HttpPost(apiUrl);
        post.setEntity(se);
        post.addHeader("User-Agent", AGENT_NAME);
        CloseableHttpResponse httpResponse = httpclient.execute(post);
        int code = httpResponse.getStatusLine().getStatusCode();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                httpResponse.getEntity().getContent()));

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();
        httpclient.close();

        String content = response.toString();

        return new ApiResponse(code, content);
    }



}
