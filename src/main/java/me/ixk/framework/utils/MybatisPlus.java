package me.ixk.framework.utils;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.MybatisSqlSessionFactoryBuilder;
import java.util.List;
import javax.sql.DataSource;
import me.ixk.framework.exceptions.MybatisPlusException;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

public class MybatisPlus {
    protected SqlSessionFactory sessionFactory;

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
    }

    public SqlSessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    public <M> M getMapper(Class<M> mapperClass) {
        return this.sessionFactory.openSession(true).getMapper(mapperClass);
    }

    public <S> S getService(Class<S> serviceClass) {
        try {
            return serviceClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new MybatisPlusException("Instantiating service failed", e);
        }
    }
}
