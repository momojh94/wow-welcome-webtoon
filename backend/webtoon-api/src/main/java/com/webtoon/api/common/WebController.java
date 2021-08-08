package com.webtoon.api.common;



// test용

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebController {

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/hello")
    public String helloWorld() {
        return "Hello World!!";
    }
}
