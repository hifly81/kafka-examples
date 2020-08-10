package com.redhat.demo.kafka.quarkus.domain;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

@RegisterForReflection
public class Vote {

    private String id;
    private Long pollId;
    private Integer option;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getPollId() {
        return pollId;
    }

    public void setPollId(Long pollId) {
        this.pollId = pollId;
    }

    public Integer getOption() {
        return option;
    }

    public void setOption(Integer option) {
        this.option = option;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vote vote = (Vote) o;
        return Objects.equals(id, vote.id) &&
                Objects.equals(pollId, vote.pollId) &&
                Objects.equals(option, vote.option);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pollId, option);
    }
}
