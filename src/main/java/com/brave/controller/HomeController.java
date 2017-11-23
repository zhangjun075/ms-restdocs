package com.brave.controller;

import com.brave.model.Demo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * Created by junzhang on 22/11/2017.
 */
@RestController
public class HomeController {
    @GetMapping("/")
    public String greeting() {
        return "hello";
    }

    @GetMapping("/demo")
    public ResponseEntity<Demo> demo(){
        return ResponseEntity.ok(Demo.builder().name("zhangjun").build());
    }
}
