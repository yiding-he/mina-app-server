package demo.pojos;

import com.hyd.appserver.annotations.Description;
import com.hyd.appserver.annotations.ExposeablePojo;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于网站的用户基本信息
 *
 * @author 贺一丁
 */
@ExposeablePojo
public class UserInfo implements Serializable {

    public static final UserInfo ANONYMOUS = new UserInfo();

    static {

    }

    @Description("用户ID")
    private long userid;

    @Description("分享平台的用户ID")
    private String shareUid;

    @Description("用户名")
    private String username;

    @Description("MD5密码")
    private String password;

    @Description("昵称")
    private String nickname;

    @Description("真实姓名")
    private String truename;

    @Description("生日")
    private Date birthday;

    @Description("手机号码")
    private String mobile;

    @Description("性别")
    private String sex;

    @Description("邮箱")
    private String email;

    @Description("QQ号码")
    private String qq;

    @Description("分享ID")
    private String fxid;

    @Description("头像地址")
    private String userlogo;

    @Description("是否今日已签到")
    private boolean checkedIn;

    @Description("上次登录时间")
    private Date lastLogin;

    @Description("下载数")
    private int downnum;

    @Description("分享数")
    private int sharenum;

    @Description("连续签到天数")
    private int continuousCheckin;

    @Description("手机品牌")
    private String phoneBrand;

    @Description("手机型号")
    private String phoneModel;

    @Description("手机操作系统")
    private String phoneOs;

    @Description("用户类型：0=普通用户,1=管理员,2=联通员工,3=营业厅员工(员工登陆营业厅时把类型改成3)," +
            "4=客服工号,5=社会渠道代理商,7=营业厅公共帐号（3G体验终端）,8=集团客户经理")
    private int userType;

    @Description("用户归属省份")
    private String province;

    @Description("各种下载计数：video/game/music/read/app")
    private Map<String, Integer> downCounts = new HashMap<String, Integer>();

    @Description("未领取的礼品数")
    private int presentNum;

    @Description("状态,0表示正常")
    private int status;

    @Description("验证中心会话ID")
    private String jsessionId;

    @Description("地址")
    private String address;

    @Description("邮编")
    private String postno;

    @Description("最后签到日期")
    private String lastCheckin;

    @Description("推荐注册人ID")
    private int ruid;

    @Description("用户在指定合作渠道一个月内渠道的次数")
    private int curMonthCheckinCount;

    @Description("银行名称")
    private String bank;

    @Description("开户支行名称")
    private String bankBranchName;

    @Description("银行帐号")
    private String bankAccount;

    @Description("银行帐号姓名")
    private String bankAccountName;

    public UserInfo() {
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBankBranchName() {
        return bankBranchName;
    }

    public void setBankBranchName(String bankBranchName) {
        this.bankBranchName = bankBranchName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankAccountName() {
        return bankAccountName;
    }

    public void setBankAccountName(String bankAccountName) {
        this.bankAccountName = bankAccountName;
    }

    public int getRuid() {
        return ruid;
    }

    public void setRuid(int ruid) {
        this.ruid = ruid;
    }

    public String getPostno() {
        return postno;
    }

    public void setPostno(String postno) {
        this.postno = postno;
    }

    public String getLastCheckin() {
        return lastCheckin;
    }

    public void setLastCheckin(String lastCheckin) {
        this.lastCheckin = lastCheckin;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getJsessionId() {
        return jsessionId;
    }

    public void setJsessionId(String jsessionId) {
        this.jsessionId = jsessionId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getShareUid() {
        return shareUid;
    }

    public void setShareUid(String shareUid) {
        this.shareUid = shareUid;
    }

    public Map<String, Integer> getDownCounts() {
        return downCounts;
    }

    public void setDownCounts(Map<String, Integer> downCounts) {
        this.downCounts = downCounts;
    }

    public int getPresentNum() {
        return presentNum;
    }

    public void setPresentNum(int presentNum) {
        this.presentNum = presentNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneOs() {
        return phoneOs;
    }

    public void setPhoneOs(String phoneOs) {
        this.phoneOs = phoneOs;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getContinuousCheckin() {
        return continuousCheckin;
    }

    public void setContinuousCheckin(int continuousCheckin) {
        this.continuousCheckin = continuousCheckin;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public long getUserid() {
        return userid;
    }

    public int getUseridInt() {
        return (int) userid;
    }

    public void setUserid(long userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getFxid() {
        return fxid;
    }

    public void setFxid(String fxid) {
        this.fxid = fxid;
    }

    public String getUserlogo() {
        return userlogo;
    }

    public void setUserlogo(String userlogo) {
        this.userlogo = userlogo;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }

    public int getDownnum() {
        return downnum;
    }

    public void setDownnum(int downnum) {
        this.downnum = downnum;
    }

    public int getSharenum() {
        return sharenum;
    }

    public void setSharenum(int sharenum) {
        this.sharenum = sharenum;
    }

    public String getPhoneBrand() {
        return phoneBrand;
    }

    public void setPhoneBrand(String phoneBrand) {
        this.phoneBrand = phoneBrand;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public int getCurMonthCheckinCount() {
        return curMonthCheckinCount;
    }

    public void setCurMonthCheckinCount(int curMonthCheckinCount) {
        this.curMonthCheckinCount = curMonthCheckinCount;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userid=" + userid +
                ", shareUid='" + shareUid + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", truename='" + truename + '\'' +
                ", birthday=" + birthday +
                ", mobile='" + mobile + '\'' +
                ", sex='" + sex + '\'' +
                ", email='" + email + '\'' +
                ", qq='" + qq + '\'' +
                ", fxid='" + fxid + '\'' +
                ", userlogo='" + userlogo + '\'' +
                ", checkedIn=" + checkedIn +
                ", lastLogin=" + lastLogin +
                ", downnum=" + downnum +
                ", sharenum=" + sharenum +
                ", continuousCheckin=" + continuousCheckin +
                ", phoneBrand='" + phoneBrand + '\'' +
                ", phoneModel='" + phoneModel + '\'' +
                ", phoneOs='" + phoneOs + '\'' +
                ", userType=" + userType +
                ", province='" + province + '\'' +
                ", downCounts=" + downCounts +
                ", presentNum=" + presentNum +
                ", status=" + status +
                ", jsessionId='" + jsessionId + '\'' +
                ", address='" + address + '\'' +
                ", postno='" + postno + '\'' +
                ", lastCheckin='" + lastCheckin + '\'' +
                ", ruid=" + ruid +
                ", curMonthCheckinCount=" + curMonthCheckinCount +
                ", bank='" + bank + '\'' +
                ", bankBranchName='" + bankBranchName + '\'' +
                ", bankAccount='" + bankAccount + '\'' +
                ", bankAccountName='" + bankAccountName + '\'' +
                '}';
    }
}
