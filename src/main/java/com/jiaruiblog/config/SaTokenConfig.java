package com.jiaruiblog.config;

import cn.dev33.satoken.strategy.SaStrategy;
import com.jiaruiblog.entity.User;
import com.jiaruiblog.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.annotation.PostConstruct;

/**
 * @author zys
 * @since 2024-06-27
 */
@Configuration
@RequiredArgsConstructor
public class SaTokenConfig {
	private final MongoTemplate mongoTemplate;

	@PostConstruct
	public void rewriteSaStrategy() {
		// 重写 Token 生成策略
		SaStrategy.instance.createToken = (loginId, loginType) -> {
			Query query = new Query(Criteria.where("_id").is(loginId));
			User one = mongoTemplate.findOne(query, User.class, "user");
			return JwtUtil.createToken(one);
		};
	}

}
