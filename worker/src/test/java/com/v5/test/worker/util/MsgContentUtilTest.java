package com.v5.test.worker.util;

import com.v5.test.worker.constant.SystemConstant;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by piguangtao on 14-4-14.
 */
public class MsgContentUtilTest {
    String seq;
    String validContent;
    String content;
    @Before
    public void setUp() throws Exception {
         seq = "1";
         validContent = "test";
         content = seq + SystemConstant.TEXT_SEQ_CONTEXT_SPLIT + validContent;
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetContent() throws Exception {
        Assert.assertEquals(content,MsgContentUtil.getContent(Long.valueOf(seq),validContent));
    }

    @Test
    public void testGetSeqFromContent() throws Exception {
        Assert.assertEquals(seq,MsgContentUtil.getSeqFromContent(content));
    }

    @Test
    public void testGetValidContent() throws Exception {
        Assert.assertEquals(validContent,MsgContentUtil.getValidContent(content));
    }
}
