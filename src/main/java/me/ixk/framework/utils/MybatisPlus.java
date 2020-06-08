package me.ixk.framework.utils;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import javax.sql.DataSource;
import java.util.List;

public class MybatisPlus {
    protected SqlSessionFactory sessionFactory;

    protected SqlSession session;

    public MybatisPlus(DataSource dataSource, List<String> mapperPackages) {
        SqlSessionFactoryBuilder builder = new MybatisSqlSessionFactoryBuilder();
        Environment environment = new Environment(
            "database",
            new JdbcTransactionFactory(),
            dataSource
        );
        Configuration configuration = new MybatisConfiguration(environment);
        for (String mapperPackage : mapperPackages) {
            configuration.addMappers(mapperPackage);
        }
        this.sessionFactory = builder.build(configuration);
        this.session = this.sessionFactory.openSession();
    }

    public SqlSessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public SqlSession getSession() {
        return this.session;
    }

    public <M> M getMapper(Class<M> mapperClass) {
        return this.session.getMapper(mapperClass);
    }

    public <S> S getService(Class<S> serviceClass) {
        try {
            return serviceClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            return null;
        }
    }
}
