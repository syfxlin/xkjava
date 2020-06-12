package me.ixk.app;

import cn.hutool.core.util.ObjectUtil;

public class App {

    public static void main(String[] args) {
        System.out.println(
            ObjectUtil.equal(new String[] { "123" }, new String[] { "123" })
        );
    }
}
