package com.webtoon.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/pause")
    public String pause() throws InterruptedException {
        int s = 10;
        System.out.println(s + " 초 sleep 시작");
        for (int idx = 1; idx <= s; idx++) {
            Thread.sleep(1000);
            System.out.println(idx + " 초");
        }
        System.out.println("sleep 끝");
        return "pause~";
    }
}
