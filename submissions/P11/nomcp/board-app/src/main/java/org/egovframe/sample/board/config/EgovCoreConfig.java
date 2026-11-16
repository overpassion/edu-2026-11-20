package org.egovframe.sample.board.config;

import org.egovframe.rte.fdl.cmmn.trace.LeaveaTrace;
import org.egovframe.rte.fdl.cmmn.trace.handler.DefaultTraceHandler;
import org.egovframe.rte.fdl.cmmn.trace.handler.TraceHandler;
import org.egovframe.rte.fdl.cmmn.trace.manager.DefaultTraceHandleManager;
import org.egovframe.rte.fdl.cmmn.trace.manager.TraceHandlerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.util.AntPathMatcher;

/**
 * eGovFrame 공통(FDL) 기반 Bean 구성.
 *
 * <p>{@code EgovAbstractServiceImpl} 은 {@code @Resource(name="messageSource")} 와
 * {@code @Resource(name="leaveaTrace")} 로 두 빈을 주입받는다. 부트 환경에는 자동 등록되지
 * 않으므로 여기서 명시적으로 정의한다.</p>
 */
@Configuration
public class EgovCoreConfig {

    /**
     * 메시지 소스. eGovFrame 예외/로그 메시지 해석과 Bean Validation 의 메시지
     * 인터포폴레이션에 사용된다. classpath 의 messages.properties 를 읽는다.
     */
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasenames("messages");
        source.setDefaultEncoding("UTF-8");
        source.setUseCodeAsDefaultMessage(true);
        return source;
    }

    /**
     * eGovFrame 트레이스 처리기. 본 앱에서는 실제 trace 호출을 쓰지 않으므로
     * 기본 핸들러만 연결한 최소 구성으로 등록한다.
     */
    @Bean(name = "leaveaTrace")
    public LeaveaTrace leaveaTrace() {
        TraceHandler traceHandler = new DefaultTraceHandler();

        DefaultTraceHandleManager handleManager = new DefaultTraceHandleManager();
        handleManager.setReqExpMatcher(new AntPathMatcher());
        handleManager.setPatterns(new String[]{"*"});
        handleManager.setHandlers(new TraceHandler[]{traceHandler});

        LeaveaTrace leaveaTrace = new LeaveaTrace();
        leaveaTrace.setTraceHandlerServices(new TraceHandlerService[]{handleManager});
        return leaveaTrace;
    }
}
