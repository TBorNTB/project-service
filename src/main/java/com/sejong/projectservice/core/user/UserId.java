package com.sejong.archiveservice.core.user;


import java.util.Objects;

public class UserId {

    private String value;

    private UserId(String value) {
        this.value = value;
    }

    public static UserId of(String value) {
        return new UserId(value);
    }

    public String userId() { return value; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "UserId{" + value + '}';
    }
}
