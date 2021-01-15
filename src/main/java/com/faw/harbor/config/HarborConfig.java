package com.faw.harbor.config;

import org.springframework.context.annotation.Configuration;

/**
 * @Author Ron
 * @create 2021-1-8 13:34
 */
@Configuration
public class HarborConfig {
    private String api = "http://10.43.4.130";
    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }
}
