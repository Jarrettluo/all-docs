package com.jiaruiblog.entity.data;

/**
 * @ClassName Event
 * @Description TODO
 * @Author luojiarui
 * @Date 2023/2/2 22:24
 * @Version 1.0
 **/

import java.util.HashMap;
import java.util.Map;

/**
 *  事件(将用户触发的事件封装成一个对象)
 */
public class Event {

    private String topic;    // 事件的主题
    private int userId;  // 事件的来源,触发的人
    private int entityType; // 事件发生在哪种类型上
    private int entityId;  // 事件发生在的实体的id
    private int entityUserId; //事件发生的实体对应的作者的id
    private Map<String,Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }

    @Override
    public String toString() {
        return "Event{" +
                "topic='" + topic + '\'' +
                ", userId=" + userId +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", entityUserId=" + entityUserId +
                ", data=" + data +
                '}';
    }
}