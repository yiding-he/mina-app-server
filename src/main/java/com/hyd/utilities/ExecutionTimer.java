package com.hyd.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务执行计时器，方便记录程序执行的每个阶段花了多少时间。下面是一个例子：
 * <pre>
 * ExecutionTimer.snapshot("开始");
 * Thread.sleep(1000);
 * ExecutionTimer.snapshot("检查参数完毕");
 * Thread.sleep(1000);
 * ExecutionTimer.snapshot("查询数据库完毕");
 * Thread.sleep(1000);
 * ExecutionTimer.snapshot("生成返回值完毕");
 * System.out.println(ExecutionTimer.toTimelineString());
 * </pre>
 * <p/>
 * 最后将会输出：
 * <pre>
 * 开始: 1350634854294 (     0ms)
 * 检查参数完毕: 1350634855294 (  1000ms)
 * 查询数据库完毕: 1350634856294 (  1000ms)
 * 生成返回值完毕: 1350634857294 (  1000ms)
 * </pre>
 *
 * @author 贺一丁
 */
public class ExecutionTimer {

    private static final Logger LOG = LoggerFactory.getLogger(ExecutionTimer.class);

    private static ThreadLocal<List<Tag>> tags = new ThreadLocal<List<Tag>>();

    private static List<Tag> getTagList() {
        List<Tag> tagList = tags.get();
        if (tagList == null) {
            tagList = new ArrayList<Tag>();
            tags.set(tagList);
        }
        return tagList;
    }

    /**
     * 清空已有记录并记录当前时间
     *
     * @param tag 标签
     */
    public static void newSnapshot(String tag) {
        newSnapshot(tag, false);
    }

    /**
     * 清空已有记录并记录当前时间
     *
     * @param tag      标签
     * @param echoback 是否立即回显
     */
    public static void newSnapshot(String tag, boolean echoback) {
        getTagList().clear();
        snapshot(tag, echoback);
    }

    /**
     * 记录当前时间
     *
     * @param tag 标签
     */
    public static void snapshot(String tag) {
        snapshot(tag, false);
    }

    /**
     * 记录当前时间并立即回显
     *
     * @param tag      标签
     * @param echoback 是否立即回显
     */
    public static void snapshot(String tag, boolean echoback) {
        List<Tag> tagList = getTagList();

        Tag t;
        if (tagList.isEmpty()) {
            t = new Tag(tag);
        } else {
            t = new Tag(tag, tagList.get(tagList.size() - 1).timestamp);
        }

        tagList.add(t);

        if (echoback) {
            LOG.error(t.toString());
        }
    }

    /**
     * 获取描述记录历史的字符串
     *
     * @return 描述记录历史的字符串
     */
    public static String toTimelineString() {
        List<Tag> tagList = getTagList();
        StringBuilder sb = new StringBuilder();

        for (Tag tag : tagList) {
            sb.append(tag.toString()).append("\n");
        }

        return sb.toString();
    }

    ///////////////////////////////////////////////////////////////

    public static class Tag {

        public long timestamp;      // 记录时间

        public long delta;          // 距上次纪录的时长

        public String tag;          // 标签

        public Tag(String tag) {
            this(tag, 0);
        }

        public Tag(String tag, long lastTimestamp) {
            this.timestamp = System.currentTimeMillis();
            this.tag = tag;

            if (lastTimestamp != 0) {
                this.delta = this.timestamp - lastTimestamp;
            }
        }

        @Override
        public String toString() {
            return this.tag + ": " + timestamp + " (" + String.format("%6d", delta) + "ms)";
        }
    }
}
