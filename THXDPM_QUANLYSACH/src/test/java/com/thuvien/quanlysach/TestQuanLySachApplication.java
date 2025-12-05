package com.thuvien.quanlysach;

import org.springframework.boot.SpringApplication;

public class TestQuanLySachApplication {

    public static void main(String[] args) {
        SpringApplication.from(QuanLySachApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}

