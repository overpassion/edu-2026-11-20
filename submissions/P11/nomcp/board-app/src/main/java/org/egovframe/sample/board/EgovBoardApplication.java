package org.egovframe.sample.board;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * eGovFrame 5.0 Boot 게시판 애플리케이션 진입점.
 *
 * <p>내장 HSQLDB(in-memory)로 기동되며, MyBatis Mapper 인터페이스는
 * {@code org.egovframe.sample.board.mapper} 패키지를 스캔한다.</p>
 */
@SpringBootApplication
@MapperScan("org.egovframe.sample.board.mapper")
public class EgovBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(EgovBoardApplication.class, args);
    }
}
