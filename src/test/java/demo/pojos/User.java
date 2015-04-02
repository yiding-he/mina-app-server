package demo.pojos;

import com.hyd.appserver.annotations.Description;

import java.util.Date;

/**
 * (description)
 *
 * @author yiding.he
 */
public class User {

    private int id;

    @Description("生日")
    private Date birthday;

    @Description("用户名")
    private String username;
    
    @Description("密码")
    private String password;
    
    @Description("角色：1=管理员，2=普通用户")
    private int role;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("User");
        sb.append("{password='").append(password).append('\'');
        sb.append(", username='").append(username).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
