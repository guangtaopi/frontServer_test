package com.v5.test.worker.service;

import com.v5.test.worker.bean.MessageInfo;
import com.v5.test.worker.util.MsgContentUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by piguangtao on 14-4-14.
 * 对发送消息和接受消息进行记录
 */
public class MessageLogService implements InitializingBean {

    private static Logger LOGGER = LoggerFactory.getLogger(MessageLogService.class);

    private static LinkedBlockingDeque<MessageInfo> sentMessageQueue = new LinkedBlockingDeque<>();

    private static LinkedBlockingDeque<MessageInfo> recevieMessageQueue = new LinkedBlockingDeque<>();

    @Value("${text.main.content.seq.start}")
    private long startSeq;

    @Value("${text.main.content.seq.end}")
    private long endSeq;

    private long numPerFile = 1000000;

    private int fileNum;

    private File[] sentMsgFiles;

    private File[] recMsgFiles;

    @Value("${test.statistic.data.dir}")
    private String dataDir;

    private static final String LOG_SPBLIT = "|";

    @Value("${test.statistic.msgdelay.enable}")
    private String enableStatisticMsgdelay;

    @Value("${test.send.msg.total}")
    private int msgTotal;

    private AtomicBoolean taskIsOver = new AtomicBoolean(false);

    @Override
    public void afterPropertiesSet() throws Exception {
        //根据消息的速录和时间计算发送消息的总数目
        long size = endSeq = startSeq;
        fileNum = (int) (size / numPerFile) + 1;
        sentMsgFiles = new File[fileNum];
        recMsgFiles = new File[fileNum];
        if("yes".equalsIgnoreCase(enableStatisticMsgdelay)){
            initFile();
            initWriteFileThread();
        }
    }

    private void initWriteFileThread() {
        new WriteFileThread("sent-msg-write-thread", sentMessageQueue, sentMsgFiles).start();
        new WriteFileThread("rec-msg-write-thread", recevieMessageQueue, recMsgFiles).start();
    }

    private void initFile() {
        String sentDir = dataDir + "/sent";
        File sentParentDir = new File(sentDir);
        if (!sentParentDir.exists()) {
            sentParentDir.mkdirs();
        }

        String recDir = dataDir + "/rec";
        File recParentDir = new File(recDir);
        if (!recParentDir.exists()) {
            recParentDir.mkdirs();
        }

        for (int i = 0; i < fileNum; i++) {
            sentMsgFiles[i] = new File(sentParentDir, i + ".log");
            if (sentMsgFiles[i].exists()) {
                sentMsgFiles[i].delete();
            }
            try {
                sentMsgFiles[i].createNewFile();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }

            recMsgFiles[i] = new File(recParentDir, i + ".log");
            if (recMsgFiles[i].exists()) {
                recMsgFiles[i].delete();
            }
            try {
                recMsgFiles[i].createNewFile();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }


    public void logSentMsg(String from, String to, String content, long sendTime) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setFrom(from);
        messageInfo.setTo(to);
        messageInfo.setSeq(Long.valueOf(MsgContentUtil.getSeqFromContent(content)));
        messageInfo.setContent(MsgContentUtil.getValidContent(content));
        messageInfo.setSendTime(sendTime);
        try {
            sentMessageQueue.put(messageInfo);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public void logReceiveMsg(String from, String to, String content, long receiveTime) {
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setFrom(from);
        messageInfo.setTo(to);
        messageInfo.setSeq(Long.valueOf(MsgContentUtil.getSeqFromContent(content)));
        messageInfo.setContent(MsgContentUtil.getValidContent(content));
        messageInfo.setSendTime(receiveTime);
        try {
            recevieMessageQueue.put(messageInfo);
        } catch (InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private  class WriteFileThread extends Thread {
        LinkedBlockingDeque<MessageInfo> messageQueue;

        FileWriter[] files;

        int fileNum;

        public WriteFileThread(String name, LinkedBlockingDeque<MessageInfo> messageQueue, File[] files) {
            super(name);
            this.messageQueue = messageQueue;
            this.files = new FileWriter[files.length];
            for(int i = 0;i < files.length;i++){
                try {
                    this.files[i] = new FileWriter(files[i]);
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(),e);
                }
            }
            fileNum = files.length != 0 ? files.length : 1;
        }

        public void run() {
            int[] recordNum = new int[fileNum];
            while (true) {
                try {
                    MessageInfo messageInfo = messageQueue.poll(30, TimeUnit.SECONDS);
                    if (null != messageInfo) {
                        long seq = messageInfo.getSeq();
                        int fileIndex =(int)seq % fileNum;
                        files[fileIndex].write(getFormatLog(messageInfo));
                        recordNum[fileIndex]++;
                        if(recordNum[fileIndex] > 100){
                            files[fileIndex].flush();
                            recordNum[fileIndex] = 0;
                        }
                    }
                    else{
                        for(int i = 0;i<recordNum.length;i++){
                            if(recordNum[i] > 0){
                                files[i].flush();
                                recordNum[i] = 0;
                                files[i].close();
                            }
                        }
                        if(taskIsOver.get()){
                            for(int i = 0;i<recordNum.length;i++){
                                if(recordNum[i] > 0){
                                    files[i].close();
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                }
            }
        }


        private String getFormatLog(MessageInfo messageInfo){
            StringBuilder sb = new StringBuilder();
            sb.append(messageInfo.getSeq()).append(LOG_SPBLIT)
              .append(messageInfo.getFrom()).append(LOG_SPBLIT)
              .append(messageInfo.getTo()).append(LOG_SPBLIT)
              .append(messageInfo.getSendTime()!=null ? messageInfo.getSendTime():"").append(LOG_SPBLIT)
              .append(messageInfo.getReceiveTime() != null ?messageInfo.getReceiveTime():"")
              .append(System.getProperty("line.separator"));
            return sb.toString();
        }
    }

    public void taskOver(){
        //延后一分钟设置任务发送结束,以便处理所有的接受消息
        new Thread("set-task-over"){
            public void run(){
                System.out.println("finishes to send task.");
                try {
                    Thread.sleep(10*1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                taskIsOver.set(true);
            }
        }.start();
    }
}
