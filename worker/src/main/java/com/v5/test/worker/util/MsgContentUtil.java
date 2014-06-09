package com.v5.test.worker.util;

import com.v5.test.worker.constant.SystemConstant;

/**
 * Created by piguangtao on 14-4-14.
 */
public class MsgContentUtil {

    public static String getContent(Long seq, String validContent) {
        return seq + SystemConstant.TEXT_SEQ_CONTEXT_SPLIT + validContent;
    }

    public static String getSeqFromContent(String content) {
        if (null != content) {
            int index = content.indexOf(SystemConstant.TEXT_SEQ_CONTEXT_SPLIT);
            return -1 != index ? content.substring(0, index) : null;
        } else {
            return null;
        }
    }

    public static String getValidContent(String content) {
        if (null != content) {
            int index = content.indexOf(SystemConstant.TEXT_SEQ_CONTEXT_SPLIT);
            return -1 != index ? content.substring(index+1, content.length()) : content;
        } else {
            return null;
        }
    }
}
