package com.ecofresh.bottice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * <p>
 * 主方法入口 
 * </p>
 * @author sunhongwei
 * @email sunhongwei@gmail.com
 * @date 2017年11月4日 上午11:07:35
 */
@SpringBootApplication
@MapperScan(basePackages = {"com.ecofresh.**.dao"})
@EnableMongoRepositories(basePackages = {"com.ecofresh.**.repository"})
@ComponentScan(basePackages = {"com.ecofresh"})
public class Application
{
	public static void main(String[] args)
	{
		SpringApplication.run(Application.class, args);
	}
}
