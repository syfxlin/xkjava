package me.ixk.app;

import com.fasterxml.jackson.databind.node.ObjectNode;
import me.ixk.framework.utils.JSON;

public class App {

    public static void main(String[] args) {
        ObjectNode node = JSON.createObject();
        System.out.println(node.toString());
    }
}
