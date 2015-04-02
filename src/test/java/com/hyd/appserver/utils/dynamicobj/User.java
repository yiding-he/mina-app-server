package com.hyd.appserver.utils.dynamicobj;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * (描述)
 *
 * @author 贺一丁
 */
public class User {

    private String name;

    private int age;

    private boolean alive;

    private Date birthday;

    private Book[] likeBooks;

    private List<String> nicknames;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public List<String> getNicknames() {
        return nicknames;
    }

    public void setNicknames(List<String> nicknames) {
        this.nicknames = nicknames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public Book[] getLikeBooks() {
        return likeBooks;
    }

    public void setLikeBooks(Book[] likeBooks) {
        this.likeBooks = likeBooks;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", alive=" + alive +
                ", birthday=" + birthday +
                ", likeBooks=" + (likeBooks == null ? null : Arrays.asList(likeBooks)) +
                ", nicknames=" + nicknames +
                '}';
    }
}
