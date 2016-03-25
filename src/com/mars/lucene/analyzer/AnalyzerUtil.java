package com.mars.lucene.analyzer;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

public class AnalyzerUtil {
    public static void displayToken(Analyzer a, String str) {
        //TokenStream：分词器处理之后得到的一个数据流，这个流中存储了分词后的各种信息；
        TokenStream token = a.tokenStream("content", new StringReader(str));
        //会将这个属性添加到流中，随着流的增加而变化
        CharTermAttribute cta = token.addAttribute(CharTermAttribute.class);
        try {
            while(token.incrementToken()) {
                System.out.print("[" + cta + "]");
            }
            System.out.println();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void displayAllTokenInfo(Analyzer a, String str) {
        //TokenStream：分词器处理之后得到的一个数据流，这个流中存储了分词后的各种信息；
        TokenStream stream = a.tokenStream("content", new StringReader(str));
        //位置增量属性，表示元素与元素之间的间隔数，例如：how are you, how和you的位置增量为2；
        PositionIncrementAttribute pia = stream.addAttribute(PositionIncrementAttribute.class);
        //分词的元素属性；
        CharTermAttribute cta = stream.addAttribute(CharTermAttribute.class);
        //偏移量属性；
        OffsetAttribute oa = stream.addAttribute(OffsetAttribute.class);
        //类型属性；
        TypeAttribute ta = stream.addAttribute(TypeAttribute.class);
        try {
            while(stream.incrementToken()) {
                System.out.print(pia.getPositionIncrement() + ":");
                System.out.println(cta+"["+oa.startOffset()+"-"+oa.endOffset()+"]"+"-->"+ta.type());
            }
            System.out.println("=================");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
