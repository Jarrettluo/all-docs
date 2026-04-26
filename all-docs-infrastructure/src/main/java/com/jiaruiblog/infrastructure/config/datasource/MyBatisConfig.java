package com.jiaruiblog.infrastructure.config.datasource;

import com.jiaruiblog.common.enums.DocStateEnum;
import com.jiaruiblog.common.enums.PermissionEnum;
import com.jiaruiblog.infrastructure.config.mybatis.EnumTypeHandler;
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
            new EnumTypeHandler<>(PermissionEnum.class),
            new EnumTypeHandler<>(DocStateEnum.class)
        );
        return factory.getObject();
    }
}