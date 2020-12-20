/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import cn.hutool.core.io.IoUtil;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.FileNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import me.ixk.app.beans.SessionTest;
import me.ixk.app.beans.User;
import me.ixk.app.beans.User2;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.BodyValue;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.CrossOrigin;
import me.ixk.framework.annotations.DataBind;
import me.ixk.framework.annotations.GetMapping;
import me.ixk.framework.annotations.HeaderValue;
import me.ixk.framework.annotations.InitBinder;
import me.ixk.framework.annotations.PostMapping;
import me.ixk.framework.annotations.PreDestroy;
import me.ixk.framework.annotations.QueryValue;
import me.ixk.framework.annotations.RequestMapping;
import me.ixk.framework.annotations.ResponseStatus;
import me.ixk.framework.annotations.WebBind;
import me.ixk.framework.annotations.WebBind.Type;
import me.ixk.framework.http.HttpStatus;
import me.ixk.framework.http.MimeType;
import me.ixk.framework.http.Model;
import me.ixk.framework.http.result.FileResult;
import me.ixk.framework.http.result.Result;
import me.ixk.framework.http.result.StreamResult;
import me.ixk.framework.ioc.DataBinder.Converter;
import me.ixk.framework.utils.MergedAnnotation;
import me.ixk.framework.utils.ResourceUtils;
import me.ixk.framework.utils.ValidGroup;
import me.ixk.framework.utils.ValidResult;
import me.ixk.framework.web.WebDataBinder;

@Controller
@RequestMapping("/test")
public class TestController {

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
        @DataBind(name = "&BODY") final JsonNode body,
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
    public String resultView(Model model) {
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
    public String resultStatus(Model model) {
        model.setStatus(HttpStatus.BAD_REQUEST);
        return "text:Status Result";
    }

    @GetMapping("/default")
    public String defaultValue(
        @QueryValue(name = "name", defaultValue = "default") String name
    ) {
        return name;
    }

    @GetMapping("/stream")
    public StreamResult stream() throws FileNotFoundException {
        return Result.stream(
            MimeType.VIDEO_MPEG,
            IoUtil.toStream(
                ResourceUtils.getFile(
                    "file:/E:/Data/Videos/天气之子/天气之子.mp4"
                )
            )
        );
    }

    @GetMapping("/file")
    public FileResult file() {
        return Result.file("file:/E:/Data/Videos/天气之子/天气之子.mp4");
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
            final Class<?> type,
            final MergedAnnotation annotation
        ) {
            return "test-converter";
        }
    }
}
