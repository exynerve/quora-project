package com.upgrad.quora.service.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/*
 * "question" table stores all the info related to a user's question such as id, uuid, content, date, user_id.
 * ID is generated based on IDENTITY strategy. This table has a Many-to-One relationship with "users" table i.e
 * one user can have many questions.
 */

@Entity
@Table(name = "question")
@NamedQueries({
        @NamedQuery(name = "allQuestions", query = "SELECT q FROM QuestionEntity q"),
        @NamedQuery(name = "questionByUuid", query = "SELECT q FROM QuestionEntity q WHERE q.uuid = :uuid"),
        @NamedQuery(name = "questionByUserId", query = "SELECT q FROM QuestionEntity q WHERE q.userId.id = :userId")
})
public class QuestionEntity implements Serializable {

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name ="UUID")
    @NotNull
    private String uuid;

    @Column(name = "CONTENT")
    @NotNull
    private String content;

    @Column(name = "DATE")
    @NotNull
    private LocalDateTime date;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserEntity userId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public UserEntity getUserId() {
        return userId;
    }

    public void setUserId(UserEntity userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object obj) {
        return new EqualsBuilder().append(this, obj).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
