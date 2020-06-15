# XK-Java

> 一个轻量的 Java 框架

## 描述 Description

本项目开发的动机同 [XK-PHP](https://github.com/syfxlin/xkphp) 一样，是一个为了熟悉对应的语言开发而开发的项目，由于先前也没有开发 Java 项目的经验，也没有开发过常驻内存的项目，所以本项目中可能会有很多不合理的设计，存在线程不安全的情况，或者有许多缺陷和漏洞，同 XK-PHP 一样，本项目不建议用于生产环境，仅用于学习就可以啦，若您有更好的建议或者发现不足的地方欢迎反馈。

项目是依照先前的 XK-PHP 的架构开发的，所以可能会有一些设计并不遵循 Java 的规范，

接口设计大部分是参考自 XK-PHP，不过也参考了 Spring 的部分设计，比如注解，切面，DispatchServlet 等等。

集成了一个 IoC 容器，容器中主要存储对象实例，配置信息和一些内容存储于 ApplicationContext，由于 Java 是常驻内存的，不同于 PHP 每次请求都重新加载容器，所以 Java 需要考虑到不同客户端请求间的线程安全，不同线程间独立的实例存储于 RequestContext，如 Request，Response 等，然后通过 ObjectFactory 动态代理的方式通过 getObject 从 RequestContext 动态获取该线程下的实例。RequestContext 使用 ThreadLocal 保证线程安全。

IoC 容器可以自定义注入器，默认的注入器可以支持大部分场景，如果有无法实现的场景可以使用自定义注入器，不过需要注意线程安全。

集成中间件处理器，可以很方便的对请求和响应进行拦截，预处理，后处理等，后续打算添加上 Servlet 的过滤器，作为上层中间件。

请求和响应扩展自 Jetty 的 Request 和 Response，添加了一些类似 Laravel 的接口，并内置 RequestBody 到 JsonNode 的转换功能。

添加了很多同 Spring 的注解，不过有一些小改动，注解大部分都支持通过 @Order 进行排序，同时注解也可以使用 @AliasFor 使用别名，同时提供一个通用的注解处理抽象类，如果需要处理自定义注解可以通过继承该类快速实现。

ORM 使用的是 Mybatis Plus，视图采用 Thymeleaf 渲染，路由是参考 FastRoute 制作的，HttpServer 采用 Jetty，参数验证使用的是 HibernateValidator（由于后续才引入的 HuTool，就懒得改了），类型转换器和一些其他工具使用的是 HuTool。

支持 AspectJ 切面，需要经过 IoC 容器处理后才能生效，同时需要实现 Advice 接口，可以直接继承自 AbstractAdvice，从容器中 Make 或者 Call，以及绑定到容器的 Bean 都可以注入切面。切面实现使用的是 Cglib。

支持使用注解开启事务，不过做的很简单，可能会有一些问题。

替换了 Jetty 自带的 ErrorHandler，改用支持动态响应 HTML 和 JSON 数据内容的 ErrorHandler。

## TODO

-   @ControllerAdvice（不完整）
-   @Application
-   @ConfigurationProperties
-   @Value
-   @ThreadSafe
-   注解关联

## 文档 Doc

暂无

## 维护者 Maintainer

XK-Java 由 [Otstar Lin](https://ixk.me/) 和下列 [贡献者](https://github.com/syfxlin/xkjava/graphs/contributors) 的帮助下撰写和维护。

> Otstar Lin - [Personal Website](https://ixk.me/) · [Blog](https://blog.ixk.me/) · [Github](https://github.com/syfxlin)

## 许可证 License

![Lincense](https://img.shields.io/github/license/syfxlin/xkjava.svg?style=flat-square)

根据 Apache License 2.0 许可证开源。
