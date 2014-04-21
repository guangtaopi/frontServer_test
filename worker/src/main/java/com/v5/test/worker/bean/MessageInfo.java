package com.v5.test.worker.bean;

import com.lmax.disruptor.EventFactory;

/**
 * Created by piguangtao on 14-2-19.
 */
public class MessageInfo {

    private String from;

    private String to;

    private String content;

    private Long sendTime;

    private Long receiveTime;

    private Long seq;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getSendTime() {
        return sendTime;
    }

    public void setSendTime(Long sendTime) {
        this.sendTime = sendTime;
    }

    public Long getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Long receiveTime) {
        this.receiveTime = receiveTime;
    }

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public final static EventFactory<MessageInfo> EVENT_FACTORY = new EventFactory<MessageInfo>() {
        public MessageInfo newInstance() {
            return new MessageInfo();
        }
    };

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MessageInfo{");
        sb.append("from='").append(from).append('\'');
        sb.append(", to='").append(to).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append(", sendTime=").append(sendTime);
        sb.append(", receiveTime=").append(receiveTime);
        sb.append(", seq=").append(seq);
        sb.append('}');
        return sb.toString();
    }
}
