package demo.pojos;

import com.hyd.appserver.annotations.Description;
import com.hyd.appserver.annotations.ExposeablePojo;

import java.util.Date;

/**
 * 用户评论
 *
 * @author yiding.he
 */
@ExposeablePojo
public class ContentComment implements Comparable<ContentComment> {

    @Description("评论ID")
    private long commentId;

    @Description("作者用户ID")
    private long author;

    @Description("作者昵称")
    private String authorName;

    @Description("评论时间")
    private Date commentTime;

    @Description("评论内容")
    private String content;

    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }

    public long getAuthor() {
        return author;
    }

    public void setAuthor(long author) {
        this.author = author;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Date getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Date commentTime) {
        this.commentTime = commentTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int compareTo(ContentComment o) {
        return this.commentTime.compareTo(o.commentTime);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("ContentComment");
        sb.append("{commentId=").append(commentId);
        sb.append(", author=").append(author);
        sb.append(", authorName='").append(authorName).append('\'');
        sb.append(", commentTime=").append(commentTime);
        sb.append(", content='").append(content).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
