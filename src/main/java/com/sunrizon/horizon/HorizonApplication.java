package com.sunrizon.horizon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// TODO: 当有人注册一个账号，此时该账号为 PENGDING , 然后通过 rabbitmq 通知所有上线的管理员去审核
@SpringBootApplication
public class HorizonApplication {

  public static void main(String[] args) {
    SpringApplication.run(HorizonApplication.class, args);
  }

}
