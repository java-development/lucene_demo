package com.mars.lucene.query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
/**
 * Author: marszhang
 * Date: 2016年3月12日
 */
public class IndexData {
    private String[] ids = {"1","2","3","4","5","6"};
    private String[] emails = {
            "zhangsan@itat.org","lisi@itat.org","john@cc.org",
            "jetty@sina.org","mike@zttc.edu","jake@itat.org"};
    private String[] contents = {
            "welcome to visited the space,I like book",
            "hello boy, I like pingpeng ball",
            "my name is cc I like game",
            "I like football",
            "I like football and I like basketball too",
            "I like movie and basketball"};
    private Date[] dates = null;
    private int[] attachs = {2,3,1,4,5,5};
    private String[] names = {"zhangsan","lisi","john","jetty","mike","jake"};
    private Map<String,Float> scores = new HashMap<String,Float>();

    private void setScores() {
        scores.put("itat.org",2.0f);
        scores.put("zttc.edu", 1.5f);
    }

    private void setDates() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            dates = new Date[ids.length];
            dates[0] = sdf.parse("2010-02-19");
            dates[1] = sdf.parse("2012-01-11");
            dates[2] = sdf.parse("2011-09-19");
            dates[3] = sdf.parse("2010-12-22");
            dates[4] = sdf.parse("2012-01-01");
            dates[5] = sdf.parse("2011-05-19");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String[] getIds() {
        return ids;
    }

    public String[] getEmails() {
        return emails;
    }

    public String[] getContents() {
        return contents;
    }

    public Date[] getDates() {
        setDates();
        return dates;
    }

    public int[] getAttachs() {
        return attachs;
    }

    public String[] getNames() {
        return names;
    }

    public Map<String, Float> getScores() {
        setScores();
        return scores;
    }
}
