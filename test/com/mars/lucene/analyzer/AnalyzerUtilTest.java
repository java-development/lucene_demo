package com.mars.lucene.analyzer;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;
import org.junit.Test;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;

public class AnalyzerUtilTest {
    @Test
    public void testDisplayToken() {
        //以下是常用的四个分词器，但是对中文不起作用；
        Analyzer a1 = new SimpleAnalyzer(Version.LUCENE_35);
        Analyzer a2 = new StandardAnalyzer(Version.LUCENE_35);
        Analyzer a3 = new WhitespaceAnalyzer(Version.LUCENE_35);
        Analyzer a4 = new StopAnalyzer(Version.LUCENE_35);

        //String str = "我来自中国陕西省西安市长安区！";
        String str = "I am a boy,I come from china!";

        AnalyzerUtil.displayToken(a1, str);
        AnalyzerUtil.displayToken(a2, str);
        AnalyzerUtil.displayToken(a3, str);
        AnalyzerUtil.displayToken(a4, str);
    }

    @Test
    public void testDisplayAllTokenInfo() {
        //以下是常用的四个分词器，但是对中文不起作用；
        Analyzer a1 = new SimpleAnalyzer(Version.LUCENE_35);
        Analyzer a2 = new StandardAnalyzer(Version.LUCENE_35);
        Analyzer a3 = new WhitespaceAnalyzer(Version.LUCENE_35);
        Analyzer a4 = new StopAnalyzer(Version.LUCENE_35);

        String str = "how are you thank you";

        AnalyzerUtil.displayAllTokenInfo(a1, str);
        AnalyzerUtil.displayAllTokenInfo(a2, str);
        AnalyzerUtil.displayAllTokenInfo(a3, str);
        AnalyzerUtil.displayAllTokenInfo(a4, str);
    }

    @Test
    public void testMyStopAnalyzer() {
        String[] stopWord = {"I", "love", "you"};
        Analyzer a1 = new MyStopAnalyzer(stopWord);

        String str = "how are you thank you and I love a girl";

        AnalyzerUtil.displayToken(a1, str);
    }

    //中文分词器测试；
    @Test
    public void testChineseAnalyzer() {
        File file = new File("D:/TestFiles/lucene/data");
        Analyzer a1 = new ComplexAnalyzer(file);

        String str = "我来自中国陕西省西安市雁塔白云山！";

        AnalyzerUtil.displayToken(a1, str);
    }
}
