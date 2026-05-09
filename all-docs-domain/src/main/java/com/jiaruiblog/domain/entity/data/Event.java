package com.jiaruiblog.domain.entity.data;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName Event
 * @Description 用户触发的事件
 * @author luojiarui
 * @Date 2023/2/2 22:24
 * @Version 1.0
 **/

/**
 *  事件(将用户触发的事件封装成一个对象)
 */
@Builder
@Getter
public class Event {

    private String topic;    // 事件的主题
    private String userId;  // 事件的来源,触发的人
    private String entityType; // 事件发生在哪种类型上
    private String entityId;  // 事件发生在的实体的id
    private String entityUserId; //事件发生的实体对应的作者的id
    @Default
    private Map<String,Object> data = new HashMap<>();

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public Event setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getEntityType() {
        return entityType;
    }

    public Event setEntityType(String entityType) {
        this.entityType = entityType;
        return this;
    }

    public String getEntityId() {
        return entityId;
    }

    public Event setEntityId(String entityId) {
        this.entityId = entityId;
        return this;
    }

    public String getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(String entityUserId) {
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