/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import me.ixk.app.beans.SessionTest;
import me.ixk.app.beans.User;
import me.ixk.app.beans.User2;
import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.annotation.core.Bean;
import me.ixk.framework.annotation.core.DataBind;
import me.ixk.framework.annotation.core.PreDestroy;
import me.ixk.framework.annotation.web.BodyValue;
import me.ixk.framework.annotation.web.Controller;
import me.ixk.framework.annotation.web.CrossOrigin;
import me.ixk.framework.annotation.web.GetMapping;
import me.ixk.framework.annotation.web.HeaderValue;
import me.ixk.framework.annotation.web.InitBinder;
import me.ixk.framework.annotation.web.PostMapping;
import me.ixk.framework.annotation.web.QueryValue;
import me.ixk.framework.annotation.web.RequestMapping;
import me.ixk.framework.annotation.web.ResponseStatus;
import me.ixk.framework.annotation.web.WebAsync;
import me.ixk.framework.annotation.web.WebBind;
import me.ixk.framework.annotation.web.WebBind.Type;
import me.ixk.framework.aop.Advice;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.Model;
import me.ixk.framework.http.Response;
import me.ixk.framework.http.result.AsyncResult;
import me.ixk.framework.http.result.Result;
import me.ixk.framework.http.result.StreamResult;
import me.ixk.framework.ioc.Container;
import me.ixk.framework.ioc.binder.DataBinder.Converter;
import me.ixk.framework.ioc.factory.ObjectProvider;
import me.ixk.framework.ioc.type.TypeWrapper;
import me.ixk.framework.util.MergedAnnotation;
import me.ixk.framework.util.ResourceUtils;
import me.ixk.framework.util.ValidGroup;
import me.ixk.framework.util.ValidResult;
import me.ixk.framework.web.WebDataBinder;
import me.ixk.framework.web.async.WebAsyncTask;
import me.ixk.framework.web.async.WebDeferredTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/test")
public class TestController {

    private static final Logger log = LoggerFactory.getLogger(
        TestController.class
    );

    @Autowired
    List<Advice> list;

    @Autowired
    Advice[] advice;

    @Autowired
    List<Advice>[] lists;

    @Autowired
    ObjectProvider<Advice> adviceObjectProvider;

    @Autowired
    ObjectProvider<String> notFoundObjectProvider;

    @Autowired(value = "name", required = false)
    private String name;

    @GetMapping("/{id}")
    public String test(final int id) {
        return "test";
    }

    @GetMapping("/get-a")
    public int getAnnotation(@QueryValue final int id) {
        return id;
    }

    @PostMapping("/post-a")
    public int postAnnotation(@BodyValue final int id) {
        return id;
    }

    @CrossOrigin
    @GetMapping("/header-a")
    public String headerAnnotation(@HeaderValue final String host) {
        return host;
    }

    @PostMapping("/post")
    @ResponseStatus
    // @VerifyCsrf
    public String post(
        @BodyValue final JsonNode user,
        @DataBind(name = "user") final User user2,
        @WebBind(
            name = "name",
            type = Type.PATH,
            converter = TestConverter.class
        ) final String name,
        @WebBind(name = "user3") final User2 user3,
        @Valid @DataBind(name = "user4") final User2 user4,
        // 如果不传入这两个其中一个参数，则会抛出异常
        final ValidGroup validGroup,
        final ValidResult<User2> validResult
    ) {
        return "post";
    }

    @PostMapping("/body")
    public String body(
        @DataBind(name = "&body") final JsonNode body,
        @DataBind final User2 user2,
        @DataBind(name = "request") final HttpServletRequest request
    ) {
        return "body";
    }

    @GetMapping("/case")
    public String getCase(final String userName) {
        return userName;
    }

    @GetMapping("/session-context")
    public String sessionContext(final SessionTest sessionTest) {
        if (sessionTest.getName() == null) {
            sessionTest.setName("Otstar Lin");
            return sessionTest.getName();
        } else {
            return sessionTest.getName() + "-Copy";
        }
    }

    @GetMapping("/result-empty")
    public String resultEmpty() {
        return "empty:";
    }

    @GetMapping("/result-html")
    public String resultHtml() {
        return "html:<h1>Html Result</h1>";
    }

    @GetMapping("/result-json")
    public String resultJson() {
        return "json:{\"type\": \"Json Result\"}";
    }

    @GetMapping("/result-redirect")
    public String resultRedirect() {
        return "redirect:/result-json";
    }

    @GetMapping("/result-text")
    public String resultText() {
        return "text:Text Result";
    }

    @GetMapping("/result-view")
    public String resultView(final Model model) {
        model.addAttribute("name", "View Result");
        return "view:index";
    }

    @GetMapping("/result-no-match")
    public String resultNoMatch() {
        return "No Match Result";
    }

    @GetMapping("/result-ignore")
    public String resultIgnore() {
        return ":empty:";
    }

    @GetMapping("/result-status")
    public String resultStatus(final Model model) {
        model.status(HttpStatus.BAD_REQUEST);
        return "text:Status Result";
    }

    @GetMapping("/default")
    public String defaultValue(
        @QueryValue(name = "name", defaultValue = "default") final String name
    ) {
        return name;
    }

    @GetMapping("/stream")
    public StreamResult stream() throws FileNotFoundException {
        return Result.stream(
            "video/mp4",
            IoUtil.toStream(
                ResourceUtils.getFile(
                    "file:/E:/Data/Videos/天气之子/天气之子.mp4"
                )
            )
        );
    }

    @GetMapping("/file")
    public File file() {
        return FileUtil.file("file:/E:/Data/Videos/天气之子/天气之子.mp4");
    }

    @GetMapping("/async")
    public Callable<String> async() {
        return () -> {
            log.info("Callable");
            return "result";
        };
    }

    @GetMapping("/async-task")
    public WebAsyncTask<String> asyncTask() {
        final WebAsyncTask<String> asyncTask = new WebAsyncTask<>(
            () -> {
                log.info("WebAsyncTask");
                return "result";
            }
        );
        asyncTask.onCompletion(
            () -> {
                log.info("WebAsyncTask completion");
            }
        );
        return asyncTask;
    }

    @GetMapping("/async-result")
    public AsyncResult<String> asyncResult() {
        return context -> {
            Result
                .file("file:/E:/Data/Videos/天气之子/天气之子.mp4")
                .toResponse(context.request(), context.response(), null);
            return null;
        };
    }

    @GetMapping("/deferred")
    public WebDeferredTask<String> deferred() {
        final WebDeferredTask<String> deferredTask = new WebDeferredTask<>();
        new Thread(
            () -> {
                try {
                    Thread.sleep(1000L);
                } catch (final InterruptedException e) {
                    log.error("Deferred sleep error", e);
                }
                log.info("Deferred set result");
                deferredTask.result("deferred");
            }
        )
            .start();
        return deferredTask;
    }

    @GetMapping("/async-timeout")
    public WebAsyncTask<String> asyncTimeout() {
        final WebAsyncTask<String> asyncTask = new WebAsyncTask<>(
            () -> {
                log.info("Async timeout sleep");
                try {
                    Thread.sleep(3000L);
                } catch (final Exception e) {
                    log.error("Async sleep error", e);
                }
                return "result";
            }
        );
        asyncTask.timeout(1000L);
        asyncTask.onTimeout(
            () -> {
                log.info("Timeout");
                return "timeout";
            }
        );
        return asyncTask;
    }

    @GetMapping("/async-pool")
    @WebAsync("testAsyncExecutor")
    public Callable<String> asyncPool() {
        return () -> {
            log.info("Callable");
            return "result";
        };
    }

    @GetMapping("/reactive-mono")
    public Mono<String> reactiveMono() {
        return Mono.just("mono");
    }

    @GetMapping("/reactive-flux")
    public Flux<String> reactiveFlux(final Response response) {
        response.contentType("text/event-stream");
        response.header("Cache-Control", "no-cache");
        return Flux
            .interval(Duration.ofSeconds(1))
            .map(
                seq ->
                    String.format("id:%d\nevent:random\ndata:%d\n\n", seq, seq)
            )
            .doOnNext(System.out::println);
    }

    @Bean
    public static ExecutorService testAsyncExecutor() {
        return new ThreadPoolExecutor(
            3,
            3,
            0L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            ThreadUtil.newNamedThreadFactory("testAsyncExecutor-", true)
        );
    }

    @InitBinder
    public void binder(final WebDataBinder binder) {
        final User2 user2 = new User2();
        user2.setName("user3");
        user2.setAge(17);
        binder.addDefault("user3", user2);
        binder.addDefault("user4.name", "user4");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("TestController destroy");
    }

    public static class TestConverter implements Converter {

        @Override
        public Object before(
            final Object object,
            final String name,
            final TypeWrapper<?> type,
            final MergedAnnotation annotation,
            final Container container
        ) {
            return "test-converter";
        }
    }
}
