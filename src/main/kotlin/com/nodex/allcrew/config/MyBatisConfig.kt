package com.nodex.allcrew.config

import org.mybatis.spring.annotation.MapperScan
import org.springframework.context.annotation.Configuration

/**
 * MyBatis Mapper 스캔 설정.
 *
 * `com.nodex.allcrew.mapper` 패키지 하위의 `@Mapper` 인터페이스를
 * 스프링 빈으로 등록한다. 메인 애플리케이션 클래스와 분리하여
 * IDE 실행 환경에서도 Mapper 스캔이 안정적으로 동작하도록 한다.
 */
@Configuration
@MapperScan("com.nodex.allcrew.mapper")
class MyBatisConfig
