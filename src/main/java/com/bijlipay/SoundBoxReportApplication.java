package com.bijlipay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Report Generation Application.
 * Responsible only for generating and delivering reports:
 *  - Nodal / Callback / Payout exception reports
 *  - Any-time report generation
 *  - VPA reports
 *  - Axis file upload / comparison
 *  - Emailing and SFTP delivery of reports
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan
public class SoundBoxReportApplication {
	public static void main(String[] args) {
		SpringApplication.run(SoundBoxReportApplication.class, args);
	}
}
