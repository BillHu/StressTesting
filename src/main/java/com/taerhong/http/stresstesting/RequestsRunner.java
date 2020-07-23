package com.taerhong.http.stresstesting;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author billhu
 */
@Slf4j(topic = "x.RequestsRunner")
public class RequestsRunner {

    private List<RequestResult> results;

    private String URL;
    private int totalRequests;
    private int concurrentRequests;

    public void runTesting(String URL, int totalRequests, int concurrentRequests) throws ExecutionException, InterruptedException {
        this.URL = URL;
        this.totalRequests = totalRequests;
        this.concurrentRequests = concurrentRequests;

        List<Future<RequestResult>> futureResults = new ArrayList<>();
        results = new ArrayList<>();

        // 初始化线程池
//        ExecutorService pool = Executors.newFixedThreadPool(concurrentRequests);
        ExecutorService pool = new ThreadPoolExecutor(concurrentRequests, concurrentRequests, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());

        for (int i = 0; i < totalRequests; i++) {
            HttpGetTask task = new HttpGetTask(i + 1, URL);
            Future<RequestResult> futureResult = pool.submit(task);
            futureResults.add(futureResult);
        }

        // 获取结果
        for (Future<RequestResult> futureResult : futureResults) {
            RequestResult result = futureResult.get();
            results.add(result);
        }
        pool.shutdown();
    }

    public void printStatistics() {
        if (results == null || results.isEmpty()) {
            return;
        }

        // 排序
        List<RequestResult> sortedResults = results.stream().sorted().collect(Collectors.toList());
        for (RequestResult r : sortedResults) {
            log.debug("{} : {} ms", r.getTaskId(), r.getDuration());
        }
        int index95 = (int) Math.ceil(sortedResults.size() * 0.95f)-1;


        long min = results.stream().mapToLong(RequestResult::getDuration).min().getAsLong();
        long max = results.stream().mapToLong(RequestResult::getDuration).max().getAsLong();
        double average = results.stream().mapToLong(RequestResult::getDuration).average().getAsDouble();

        log.info("访问地址：{}", this.URL);
        log.info("总请求数：{}", this.totalRequests);
        log.info("并发请求数：{}", this.concurrentRequests);
        log.info("最短响应时间：{} ms", min);
        log.info("最长响应时间：{} ms", max);
        log.info("平均响应时间：{} ms", average);
        log.info("95%响应时间：{} ms", sortedResults.get(index95).getDuration());
    }
}

