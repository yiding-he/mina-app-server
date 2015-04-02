package demo.pojos;

import com.hyd.appserver.annotations.Description;
import com.hyd.appserver.annotations.ExposeablePojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 资源图片
 *
 * @author yiding.he
 */
@ExposeablePojo
public class ContentImage implements Serializable {

    @Description("图片ID")
    private long imageId;

    @Description("范畴（web/wap）")
    private String scope;

    @Description("图片地址")
    private String url;

    @Description("图片创建时间")
    private Date createTime;

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUrl() {
        return url == null ? null : url.trim();
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ContentImage");
        sb.append("{imageId=").append(imageId);
        sb.append(", scope='").append(scope).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", createTime=").append(createTime);
        sb.append('}');
        return sb.toString();
    }
}
