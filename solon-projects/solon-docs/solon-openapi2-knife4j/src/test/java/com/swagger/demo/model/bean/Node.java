package com.swagger.demo.model.bean;

import java.util.List;

/**
 * @author noear 2023/7/19 created
 */
public class Node {
    int id;
    int pid;

    List<Node> children;

    Node last;
}
