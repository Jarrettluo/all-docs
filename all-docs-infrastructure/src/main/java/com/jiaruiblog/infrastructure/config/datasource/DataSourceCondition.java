package com.jiaruiblog.config.datasource;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class DataSourceCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String dbType = context.getEnvironment().getProperty("app.datasource.type");
        return "mysql".equalsIgnoreCase(dbType) || "mongodb".equalsIgnoreCase(dbType);
    }


    public static class MongoDBCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String dbType = context.getEnvironment().getProperty("app.datasource.type");
            return dbType == null || "mongodb".equalsIgnoreCase(dbType);
        }
    }

    public static class MySQLCondition implements Condition {
        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            String dbType = context.getEnvironment().getProperty("app.datasource.type");
            return "mysql".equalsIgnoreCase(dbType);
        }
    }
}