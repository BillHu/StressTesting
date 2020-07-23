package com.taerhong.test;

import com.taerhong.http.stresstesting.RequestsRunner;

import java.util.concurrent.ExecutionException;

/**
 * @author billhu
 */
public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        String URL = "http://www.baidu.com";
        RequestsRunner runner = new RequestsRunner();
        runner.runTesting(URL, 100,10);

        runner.printStatistics();
    }
}
