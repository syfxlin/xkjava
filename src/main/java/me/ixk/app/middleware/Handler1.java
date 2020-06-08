package me.ixk.app.middleware;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import me.ixk.app.mapper.UsersMapper;
import me.ixk.app.service.impl.UsersServiceImpl;
import me.ixk.framework.http.Request;
import me.ixk.framework.http.Response;
import me.ixk.framework.ioc.Application;
import me.ixk.framework.middleware.Handler;
import me.ixk.framework.servlet.DispatcherServlet;
import me.ixk.framework.utils.Thymeleaf;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.thymeleaf.context.WebContext;

import javax.sql.DataSource;
import java.io.IOException;

public class Handler1 implements Handler {

    @Override
    public Object handle(Request request, Response response) {
        MybatisSqlSessionFactoryBuilder builder = new MybatisSqlSessionFactoryBuilder();
        DataSource dataSource = new PooledDataSource(
            "com.mysql.cj.jdbc.Driver",
            "jdbc:mysql://localhost:3306/xkjava?serverTimezone=GMT%2B8",
            "demo",
            "123456"
        );
        Environment environment = new Environment(
            "mysql",
            new JdbcTransactionFactory(),
            dataSource
        );
        Configuration configuration = new MybatisConfiguration(environment);
        configuration.addMappers("me.ixk.app.mapper");
        SqlSession session = builder.build(configuration).openSession();
        UsersMapper usersMapper = session.getMapper(UsersMapper.class);
        UsersServiceImpl usersService = new UsersServiceImpl();
        usersService.setBaseMapper(usersMapper);
        usersService.count();
        WebContext webContext = new WebContext(
            request.getOriginRequest(),
            response.getOriginResponse(),
            Application
                .getInstance()
                .make(DispatcherServlet.class)
                .getServletContext()
        );
        try {
            return response.content(
                Application
                    .getInstance()
                    .make(Thymeleaf.class)
                    .getTemplateEngine()
                    .process("index", webContext)
            );
        } catch (IOException e) {
            return null;
        }
    }
}
