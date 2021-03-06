package com.ryuntech.zipkin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import zipkin.server.internal.EnableZipkinServer;

/**
 * 链路追踪 ZipKin
 *
 * @author antu
 * @date 2019-05-21
 */
@EnableZipkinServer
@EnableEurekaClient
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class})
public class RyunCloudZipkinApplication {

    public static void main(String[] args) {
        SpringApplication.run(RyunCloudZipkinApplication.class, args);
    }
}
