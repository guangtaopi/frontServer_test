package com.v5.test.worker.bean;

/**
 * Created by piguangtao on 14-2-18.
 */
public class SingleMessageTask {


    private int fromId;

    private int total;

    private TaskType taskType = TaskType.Text;

    /**
     * 最大速率（由同一线程的任务的用户按照一定的速率放到阻塞队列中）
     */
    private int maxRate = 1;

    /**
     * 同一用户发送消息的时间间隔是否随机
     */
    private boolean sameUserSendIntervalRandom = false;

    enum TaskType{
         Text;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getMaxRate() {
        return maxRate;
    }

    public void setMaxRate(int maxRate) {
        this.maxRate = maxRate;
    }

    public boolean isSameUserSendIntervalRandom() {
        return sameUserSendIntervalRandom;
    }

    public void setSameUserSendIntervalRandom(boolean sameUserSendIntervalRandom) {
        this.sameUserSendIntervalRandom = sameUserSendIntervalRandom;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SingleMessageTask{");
        sb.append("fromId=").append(fromId);
        sb.append(", total=").append(total);
        sb.append(", taskType=").append(taskType);
        sb.append(", maxRate=").append(maxRate);
        sb.append(", sameUserSendIntervalRandom=").append(sameUserSendIntervalRandom);
        sb.append('}');
        return sb.toString();
    }
}
