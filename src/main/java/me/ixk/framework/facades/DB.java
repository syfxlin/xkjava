package me.ixk.framework.facades;

import me.ixk.framework.utils.MybatisPlus;
import org.apache.ibatis.session.SqlSessionFactory;

public class DB extends AbstractFacade {

    protected static MybatisPlus make() {
        return app.make(MybatisPlus.class);
    }

    public static SqlSessionFactory getSessionFactory() {
        return make().getSessionFactory();
    }

    public static <M> M getMapper(Class<M> mapperClass) {
        return make().getMapper(mapperClass);
    }

    public static <S> S getService(Class<S> serviceClass) {
        return make().getService(serviceClass);
    }
}
