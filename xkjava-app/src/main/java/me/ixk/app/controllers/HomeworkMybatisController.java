/*
 * Copyright (c) 2020, Otstar Lin (syfxlin@gmail.com). All Rights Reserved.
 */

package me.ixk.app.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.ixk.app.entity.TbBill;
import me.ixk.app.entity.TbProvider;
import me.ixk.app.service.impl.TbBillServiceImpl;
import me.ixk.app.service.impl.TbProviderServiceImpl;
import me.ixk.framework.annotation.core.Autowired;
import me.ixk.framework.annotation.web.Controller;
import me.ixk.framework.annotation.web.GetMapping;
import me.ixk.framework.annotation.web.PostMapping;
import me.ixk.framework.annotation.web.RequestMapping;
import me.ixk.framework.http.result.RedirectResult;
import me.ixk.framework.http.result.Result;
import me.ixk.framework.http.result.ViewResult;

@Controller
@RequestMapping("/homework/mybatis")
public class HomeworkMybatisController {

    @Autowired
    TbBillServiceImpl tbBillService;

    @Autowired
    TbProviderServiceImpl tbProviderService;

    @GetMapping("/search")
    public ViewResult search(
        final Integer providerId,
        final Integer isPayment,
        final String productName
    ) {
        List<TbBill> bills = new ArrayList<>();
        final Map<String, TbProvider> providers = new HashMap<>();
        if (providerId != null && isPayment != null && productName != null) {
            bills =
                tbBillService
                    .query()
                    .eq("providerId", providerId)
                    .eq("isPayment", isPayment)
                    .like(true, "productName", productName)
                    .list();
            for (final TbProvider provider : tbProviderService.list()) {
                providers.put(provider.getId().toString(), provider);
            }
        }
        return Result.view(
            "homework/mybatis",
            Map.of("bills", bills, "providers", providers)
        );
    }

    @GetMapping("/add")
    public ViewResult addView() {
        return Result.view(
            "homework/add",
            Map.of("providers", tbProviderService.list())
        );
    }

    @PostMapping("/add")
    public RedirectResult add(final TbProvider provider) {
        provider.setCreatedBy(1L);
        provider.setCreationDate(LocalDateTime.now());
        tbProviderService.save(provider);
        return Result.redirect("/homework/mybatis/add");
    }

    @PostMapping("/delete")
    public RedirectResult delete(final Integer id) {
        tbProviderService.removeById(id);
        return Result.redirect("/homework/mybatis/add");
    }

    @PostMapping("/edit")
    public RedirectResult edit(final TbProvider provider) {
        provider.setModifyBy(1L);
        provider.setModifyDate(LocalDateTime.now());
        tbProviderService.updateById(provider);
        return Result.redirect("/homework/mybatis/add");
    }
}
