package demo.pojos;

import com.hyd.appserver.annotations.Description;
import com.hyd.appserver.annotations.ExposeablePojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 作品资源文件
 *
 * @author yiding.he
 */
@ExposeablePojo
public class ContentFile implements Serializable {

    @Description("文件ID")
    private long fileId;

    @Description("文件地址")
    private String url;

    @Description("文件格式")
    private String format;

    @Description("适用平台")
    private String platform;

    @Description("版本号")
    private String version;

    @Description("文件大小（字节）")
    private long size;

    @Description("更新时间")
    private Date updateTime;

    public long getFileId() {
        return fileId;
    }

    public void setFileId(long fileId) {
        this.fileId = fileId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ContentFile");
        sb.append("{fileId=").append(fileId);
        sb.append(", url='").append(url).append('\'');
        sb.append(", format='").append(format).append('\'');
        sb.append(", platform='").append(platform).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", size=").append(size);
        sb.append(", updateTime=").append(updateTime);
        sb.append('}');
        return sb.toString();
    }
}
