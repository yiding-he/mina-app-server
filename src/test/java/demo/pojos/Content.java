package demo.pojos;

import com.hyd.appserver.annotations.Description;
import com.hyd.appserver.annotations.ExposeablePojo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 内容
 *
 * @author yiding.he
 */
@ExposeablePojo
public class Content implements Serializable {

    @Description("旧平台内容ID")
    private String contentId;

    @Description("新平台内容ID")
    private String contentKey;

    @Description("内容名称")
    private String contentName;

    @Description("状态")
    private String status;

    @Description("描述")
    private String description;

    @Description("作者")
    private String author;

    @Description("分类ID")
    private int typeId;

    @Description("分类名称")
    private String typeName;

    @Description("上级分类ID")
    private int parentTypeId;

    @Description("上级分类名称")
    private String parentTypeName;

    @Description("用户评论")
    private List<ContentComment> comments;

    @Description("内容提供商ID")
    private Long provider;

    @Description("内容提供商名称")
    private String providerName;

    @Description("内容提供商的内容ID")
    private String providerPid;

    @Description("内容提供商标签")
    private List<String> providerTags;

    @Description("内容提供商权重，用于调整搜索结果排列")
    private int providerWeight;

    @Description("记录创建时间")
    private Date createTime;

    @Description("记录更新时间")
    private Date updateTime;

    @Description("访问次数")
    private int visitCount;

    @Description("下载次数")
    private int downloadCount;

    @Description("订购次数")
    private int orderCount;

    @Description("资源文件列表")
    private List<ContentFile> files;

    @Description("适用平台")
    private List<String> platform;

    @Description("评分")
    private double rating;

    @Description("评分次数")
    private int ratingCount;

    @Description("总评分")
    private int ratingTotal;

    @Description("顶次数")
    private int thumbsUpCount;

    @Description("顶次数基数")
    private int thumbsUpCountBase;

    @Description("踩次数")
    private int thumbsDownCount;

    @Description("截图列表")
    private List<ContentImage> screenshots;

    @Description("图标列表")
    private List<ContentImage> logos;

    @Description("分享次数")
    private int shareCount;

    @Description("成功分享次数")
    private int shareSuccessCount;

    @Description("价格（分）")
    private Price price;

    @Description("当前是否免费")
    private String free;

    @Description("评论次数")
    private int commentCount;

    @Description("特殊图标：hot/elite/price")
    private String specialIcon;

    @Description("提供给第三方的下载地址")
    private String thirdPartyDownloadUrl;

    @Description("已通过的安全检测：nuoxi/360/qq")
    private Map<String, Integer> safety;

    @Description("其他描述")
    private List<Map<String, String>> descriptions;

    @Description("扩展属性")
    private List<Property> ext;

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public int getThumbsUpCountBase() {
        return thumbsUpCountBase;
    }

    public void setThumbsUpCountBase(int thumbsUpCountBase) {
        this.thumbsUpCountBase = thumbsUpCountBase;
    }

    public int getParentTypeId() {
        return parentTypeId;
    }

    public void setParentTypeId(int parentTypeId) {
        this.parentTypeId = parentTypeId;
    }

    public String getParentTypeName() {
        return parentTypeName;
    }

    public void setParentTypeName(String parentTypeName) {
        this.parentTypeName = parentTypeName;
    }

    public int getProviderWeight() {
        return providerWeight;
    }

    public void setProviderWeight(int providerWeight) {
        this.providerWeight = providerWeight;
    }

    public int getThumbsUpCount() {
        return thumbsUpCount;
    }

    public void setThumbsUpCount(int thumbsUpCount) {
        this.thumbsUpCount = thumbsUpCount;
    }

    public int getThumbsDownCount() {
        return thumbsDownCount;
    }

    public void setThumbsDownCount(int thumbsDownCount) {
        this.thumbsDownCount = thumbsDownCount;
    }

    public List<Property> getExt() {
        return ext;
    }

    public void setExt(List<Property> ext) {
        this.ext = ext;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getSpecialIcon() {
        return specialIcon;
    }

    public void setSpecialIcon(String specialIcon) {
        this.specialIcon = specialIcon;
    }

    public String getThirdPartyDownloadUrl() {
        return thirdPartyDownloadUrl;
    }

    public void setThirdPartyDownloadUrl(String thirdPartyDownloadUrl) {
        this.thirdPartyDownloadUrl = thirdPartyDownloadUrl;
    }

    public Map<String, Integer> getSafety() {
        return safety;
    }

    public void setSafety(Map<String, Integer> safety) {
        this.safety = safety;
    }

    public List<Map<String, String>> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<Map<String, String>> descriptions) {
        this.descriptions = descriptions;
    }

    public String getContentId() {
        return contentId;
    }

    public void setContentId(String contentId) {
        this.contentId = contentId;
    }

    public String getContentKey() {
        return contentKey;
    }

    public void setContentKey(String contentKey) {
        this.contentKey = contentKey;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<ContentComment> getComments() {
        return comments;
    }

    public void setComments(List<ContentComment> comments) {
        this.comments = comments;
    }

    public Long getProvider() {
        return provider;
    }

    public void setProvider(Long provider) {
        this.provider = provider;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderPid() {
        return providerPid;
    }

    public void setProviderPid(String providerPid) {
        this.providerPid = providerPid;
    }

    public List<String> getProviderTags() {
        return providerTags;
    }

    public void setProviderTags(List<String> providerTags) {
        this.providerTags = providerTags;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public List<ContentFile> getFiles() {
        return files;
    }

    public void setFiles(List<ContentFile> files) {
        this.files = files;
    }

    public List<String> getPlatform() {
        return platform;
    }

    public void setPlatform(List<String> platform) {
        this.platform = platform;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public int getRatingTotal() {
        return ratingTotal;
    }

    public void setRatingTotal(int ratingTotal) {
        this.ratingTotal = ratingTotal;
    }

    public List<ContentImage> getScreenshots() {
        return screenshots;
    }

    public void setScreenshots(List<ContentImage> screenshots) {
        this.screenshots = screenshots;
    }

    public List<ContentImage> getLogos() {
        return logos;
    }

    public void setLogos(List<ContentImage> logos) {
        this.logos = logos;
    }

    public int getShareCount() {
        return shareCount;
    }

    public void setShareCount(int shareCount) {
        this.shareCount = shareCount;
    }

    public int getShareSuccessCount() {
        return shareSuccessCount;
    }

    public void setShareSuccessCount(int shareSuccessCount) {
        this.shareSuccessCount = shareSuccessCount;
    }

    public Price getPrice() {
        return price;
    }

    public void setPrice(Price price) {
        this.price = price;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Content");
        sb.append("{contentId='").append(contentId).append('\'');
        sb.append(", contentKey='").append(contentKey).append('\'');
        sb.append(", contentName='").append(contentName).append('\'');
        sb.append(", status='").append(status).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", author='").append(author).append('\'');
        sb.append(", typeId=").append(typeId);
        sb.append(", typeName='").append(typeName).append('\'');
        sb.append(", parentTypeId=").append(parentTypeId);
        sb.append(", parentTypeName='").append(parentTypeName).append('\'');
        sb.append(", comments=").append(comments);
        sb.append(", provider=").append(provider);
        sb.append(", providerName='").append(providerName).append('\'');
        sb.append(", providerPid='").append(providerPid).append('\'');
        sb.append(", providerTags=").append(providerTags);
        sb.append(", providerWeight=").append(providerWeight);
        sb.append(", createTime=").append(createTime);
        sb.append(", updateTime=").append(updateTime);
        sb.append(", visitCount=").append(visitCount);
        sb.append(", downloadCount=").append(downloadCount);
        sb.append(", orderCount=").append(orderCount);
        sb.append(", files=").append(files);
        sb.append(", platform=").append(platform);
        sb.append(", rating=").append(rating);
        sb.append(", ratingCount=").append(ratingCount);
        sb.append(", ratingTotal=").append(ratingTotal);
        sb.append(", thumbsUpCount=").append(thumbsUpCount);
        sb.append(", thumbsUpCountBase=").append(thumbsUpCountBase);
        sb.append(", thumbsDownCount=").append(thumbsDownCount);
        sb.append(", screenshots=").append(screenshots);
        sb.append(", logos=").append(logos);
        sb.append(", shareCount=").append(shareCount);
        sb.append(", shareSuccessCount=").append(shareSuccessCount);
        sb.append(", price=").append(price);
        sb.append(", free='").append(free).append('\'');
        sb.append(", commentCount=").append(commentCount);
        sb.append(", specialIcon='").append(specialIcon).append('\'');
        sb.append(", thirdPartyDownloadUrl='").append(thirdPartyDownloadUrl).append('\'');
        sb.append(", safety=").append(safety);
        sb.append(", descriptions=").append(descriptions);
        sb.append(", ext=").append(ext);
        sb.append('}');
        return sb.toString();
    }
}
