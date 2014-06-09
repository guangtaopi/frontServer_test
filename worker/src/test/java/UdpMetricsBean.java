/**
 * Created by sunhao on 14-3-4.
 */
public class UdpMetricsBean {
    private String nodeId;
    private String serviceNodeId;
    private long time;
    private double curRecvByte;
    private double limitRecvByte;
    private double curRecvPacket;
    private double limitRecvPacket;
    private double curSendByte;
    private double limitSendByte;
    private double curSendPacket;
    private double limitSendPacket;

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getServiceNodeId() {
        return serviceNodeId;
    }

    public void setServiceNodeId(String serviceNodeId) {
        this.serviceNodeId = serviceNodeId;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public double getCurRecvByte() {
        return curRecvByte;
    }

    public void setCurRecvByte(double curRecvByte) {
        this.curRecvByte = curRecvByte;
    }

    public double getLimitRecvByte() {
        return limitRecvByte;
    }

    public void setLimitRecvByte(double limitRecvByte) {
        this.limitRecvByte = limitRecvByte;
    }

    public double getCurRecvPacket() {
        return curRecvPacket;
    }

    public void setCurRecvPacket(double curRecvPacket) {
        this.curRecvPacket = curRecvPacket;
    }

    public double getLimitRecvPacket() {
        return limitRecvPacket;
    }

    public void setLimitRecvPacket(double limitRecvPacket) {
        this.limitRecvPacket = limitRecvPacket;
    }

    public double getCurSendByte() {
        return curSendByte;
    }

    public void setCurSendByte(double curSendByte) {
        this.curSendByte = curSendByte;
    }

    public double getLimitSendByte() {
        return limitSendByte;
    }

    public void setLimitSendByte(double limitSendByte) {
        this.limitSendByte = limitSendByte;
    }

    public double getCurSendPacket() {
        return curSendPacket;
    }

    public void setCurSendPacket(double curSendPacket) {
        this.curSendPacket = curSendPacket;
    }

    public double getLimitSendPacket() {
        return limitSendPacket;
    }

    public void setLimitSendPacket(double limitSendPacket) {
        this.limitSendPacket = limitSendPacket;
    }



    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UdpMetricsBean{");
        sb.append("nodeId='").append(nodeId).append('\'');
        sb.append(", serviceNodeId='").append(serviceNodeId).append('\'');
        sb.append(", time=").append(time);
        sb.append(", curRecvByte=").append(curRecvByte);
        sb.append(", limitRecvByte=").append(limitRecvByte);
        sb.append(", curRecvPacket=").append(curRecvPacket);
        sb.append(", limitRecvPacket=").append(limitRecvPacket);
        sb.append(", curSendByte=").append(curSendByte);
        sb.append(", limitSendByte=").append(limitSendByte);
        sb.append(", curSendPacket=").append(curSendPacket);
        sb.append(", limitSendPacket=").append(limitSendPacket);
        sb.append('}');
        return sb.toString();
    }
}
