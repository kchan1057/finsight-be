package com.bizradar;

import org.springframework.boot.SpringApplication;

public class TestBizradarApplication {

	public static void main(String[] args) {
		SpringApplication.from(BizradarApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
