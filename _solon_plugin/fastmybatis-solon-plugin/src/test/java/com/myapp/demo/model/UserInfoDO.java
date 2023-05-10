package com.myapp.demo.model;

/**
 * @author tanghc
 */
public class UserInfoDO {
    private Integer id;
    /** 用户名, 数据库字段：username */
    private String username;

    /** 状态, 数据库字段：state */
    private Byte state;

    /** 城市, 数据库字段：city */
    private String city;

    /** 街道, 数据库字段：address */
    private String address;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Byte getState() {
        return state;
    }

    public void setState(Byte state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "UserInfoDO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", state=" + state +
                ", city='" + city + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
