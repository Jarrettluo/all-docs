package com.jiaruiblog.infrastructure.config.datasource;

import com.jiaruiblog.common.enums.DocStateEnum;
import com.jiaruiblog.common.enums.PermissionEnum;
import com.jiaruiblog.common.enums.ThumbSizeEnum;
import com.jiaruiblog.common.enums.ThumbnailEnum;
import com.jiaruiblog.infrastructure.config.mybatis.BooleanTypeHandler;
import com.jiaruiblog.infrastructure.config.mybatis.DocStateEnumTypeHandler;
import com.jiaruiblog.infrastructure.config.mybatis.EnumTypeHandler;
import com.jiaruiblog.infrastructure.config.mybatis.RedisActionEnumTypeHandler;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

@Configuration
@MapperScan("com.jiaruiblog.infrastructure.repository.mysql")
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        factory.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml")
        );
        factory.setTypeHandlers(
            new BooleanTypeHandler(),
            new EnumTypeHandler<>(PermissionEnum.class),
            new DocStateEnumTypeHandler(),
            new RedisActionEnumTypeHandler(),
            new EnumTypeHandler<>(ThumbnailEnum.class),
            new EnumTypeHandler<>(ThumbSizeEnum.class)
        );
        return factory.getObject();
    }
}
