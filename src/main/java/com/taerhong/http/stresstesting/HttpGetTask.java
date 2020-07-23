package com.taerhong.http.stresstesting;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * 一个Get请求任务
 *
 * @author billhu
 */
@Slf4j(topic = "x.HttpGetTask")
public class HttpGetTask implements Callable<RequestResult> {

    private int taskId;
    private String URL;

    public HttpGetTask(int taskId, String URL) {
        this.taskId = taskId;
        this.URL = URL;
    }

    @Override
    public RequestResult call() throws Exception {
        RequestResult requestResult = null;
        boolean success = false;
        long startTime = 0;
        long endTime = 0;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(URL);

            ResponseHandler<Boolean> responseHandler = new ResponseHandler<Boolean>() {

                @Override
                public Boolean handleResponse(
                        final HttpResponse response) throws ClientProtocolException, IOException {
                    int status = response.getStatusLine().getStatusCode();
                    if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
                        return true;
                    } else {
                        log.info("请求失败，status code {}", status);
                        return false;
                    }
                }

            };
            startTime = System.currentTimeMillis();
            success = httpclient.execute(httpget, responseHandler);
            endTime = System.currentTimeMillis();


        } catch (Exception e) {
            log.error("请求异常 {}", e);
            success = false;
        } finally {
            httpclient.close();
        }

        requestResult = new RequestResult(this.taskId, success, endTime - startTime);
        return requestResult;
    }
}
