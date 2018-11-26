package com.example.achauthorizedservice.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by tnguyen on 11/20/18.
 */
@RestController
public class StatusController {

    @RequestMapping("/status")
    public String status() {
        return "OK";
    }
}
