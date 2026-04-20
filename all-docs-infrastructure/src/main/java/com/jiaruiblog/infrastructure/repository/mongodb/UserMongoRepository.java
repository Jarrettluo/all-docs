package com.jiaruiblog.infrastructure.repository.mongodb;

import com.jiaruiblog.config.datasource.DataSourceCondition;
import com.jiaruiblog.domain.entity.User;
import com.jiaruiblog.infrastructure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * <p></p>
 * edit at 2025/7/1 11:05
 *
 * @author Jarrett Luo
 * @version 1.0
 */
@Repository
@Conditional(DataSourceCondition.MongoDBCondition.class)
public class UserMongoRepository implements UserRepository {

    private static final String COLLECTION_NAME = "user";
    private static final String OBJECT_ID = "_id";
    private static final String USER_BANNING = "banning";
    public static final String AVATAR = "avatar";
    public static final String USERNAME = "username";
    public static final String ROLE = "permissionEnum";
    public static final String UPDATE_TIME = "updateDate";


    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public User findById(String id) {
        return mongoTemplate.findById(id, User.class);
    }

    @Override
    public List<User> findByUsername(String username) {
        Query query = new Query().addCriteria(Criteria.where(USERNAME).is(username));
        return mongoTemplate.find(query, User.class, COLLECTION_NAME);
    }

    @Override
    public void insert(User user) {
        mongoTemplate.save(user, COLLECTION_NAME);
    }

    @Override
    public int update(User user) {
        if (user == null || user.getId() == null) {
            return 0;
        }
        Query query = new Query().addCriteria(Criteria.where(OBJECT_ID).is(user.getId()));
        Update update = new Update();
        if (user.getPermissionEnum() != null) update.set(ROLE, user.getPermissionEnum());
        if (user.getPassword() != null) update.set("password", user.getPassword());
        if (user.getPhone() != null) update.set("phone", user.getPhone());
        if (user.getMail() != null) update.set("mail", user.getMail());
        if (user.getMale() != null) update.set("male", user.getMale());
        if (user.getDescription() != null) update.set("description", user.getDescription());
        if (user.getBirthtime() != null) update.set("birthtime", user.getBirthtime());
        update.set(UPDATE_TIME, new Date());
        mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
        return 1;
    }

    @Override
    public void updateLoginTime(User user) {
        Query query1 = new Query(Criteria.where("_id").is(user.getId()));
        Update update = new Update();
        update.set("lastLogin", new Date());
        mongoTemplate.updateFirst(query1, update, User.class, COLLECTION_NAME);
    }

    @Override
    public void blockUser(User user) {
        Query query = new Query();
        query.addCriteria(Criteria.where(OBJECT_ID).is(user.getId()));
        Update update = new Update().set(USER_BANNING, !Optional.ofNullable(user.getBanning()).orElse(true));
        mongoTemplate.updateFirst(query, update, User.class, COLLECTION_NAME);
    }

    @Override
    public int deleteById(String id) {
        Query query = new Query().addCriteria(Criteria.where(OBJECT_ID).is(id));
        var result = mongoTemplate.remove(query, User.class, COLLECTION_NAME);
        return (int) result.getDeletedCount();
    }

    @Override
    public User save(User user) {
        return mongoTemplate.save(user);
    }

    @Override
    public long count() {
        return mongoTemplate.count(new Query(), User.class, COLLECTION_NAME);
    }

    @Override
    public List<User> findByPage(int pageNum, int pageSize, Sort sort) {
        Query query = new Query();
        query.skip((long) (pageNum - 1) * pageSize);
        query.limit(pageSize);
        query.with(sort);
        return mongoTemplate.find(query, User.class, COLLECTION_NAME);

    }
}