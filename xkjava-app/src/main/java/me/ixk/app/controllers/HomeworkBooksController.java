/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import me.ixk.app.entity.Books;
import me.ixk.app.entity.Users;
import me.ixk.app.entity.Visitors;
import me.ixk.app.service.impl.BooksServiceImpl;
import me.ixk.app.service.impl.UsersServiceImpl;
import me.ixk.app.service.impl.VisitorsServiceImpl;
import me.ixk.framework.annotations.Autowired;
import me.ixk.framework.annotations.Controller;
import me.ixk.framework.annotations.GetMapping;
import me.ixk.framework.annotations.PostMapping;
import me.ixk.framework.annotations.RequestMapping;
import me.ixk.framework.http.result.RedirectResult;
import me.ixk.framework.http.result.Result;
import me.ixk.framework.http.result.ViewResult;

/**
 * @author Otstar Lin
 * @date 2020/10/16 上午 10:21
 */
@Controller
@RequestMapping("/books")
public class HomeworkBooksController {
    @Autowired
    VisitorsServiceImpl visitorsService;

    @Autowired
    UsersServiceImpl usersService;

    @Autowired
    BooksServiceImpl booksService;

    @GetMapping("")
    public ViewResult index() {
        final Visitors visitorsLogin = visitorsService.getById(3);
        final String users = visitorsLogin.getUsers();
        final List<Users> usersList = users == null || users.isEmpty()
            ? new ArrayList<>()
            : Arrays
                .stream(users.split(","))
                .map(Long::parseLong)
                .map(usersService::getById)
                .collect(Collectors.toList());
        return Result.view("books/index", Map.of("users", usersList.size()));
    }

    @GetMapping("/add")
    public ViewResult addView() {
        return Result.view("books/add");
    }

    @PostMapping("/add")
    public RedirectResult add(final Books books) {
        booksService.save(books);
        return Result.redirect("/books/add?success");
    }

    @GetMapping("/list")
    public ViewResult list(Integer page) {
        final IPage<Books> books = booksService.page(
            new Page<>(page == null ? 1 : page, 5)
        );
        return Result.view("books/list", Map.of("books", books));
    }

    @GetMapping("/delete/{id}")
    public RedirectResult delete(Long id) {
        booksService.removeById(id);
        return Result.redirect("/books/list");
    }

    @GetMapping("/update/{id}")
    public ViewResult updateView(Long id) {
        final Books book = booksService.getById(id);
        return Result.view("books/update", Map.of("book", book));
    }

    @PostMapping("/update")
    public RedirectResult update(Books books) {
        booksService.updateById(books);
        return Result.redirect("/books/list");
    }

    @GetMapping("/view/{id}")
    public ViewResult view(Long id) {
        final Books book = booksService.getById(id);
        return Result.view("books/view", Map.of("book", book));
    }
}
