package com.mars.lucene.analyzer;

import java.io.Reader;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LetterTokenizer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.util.Version;
/**
 * 自定义分词器
 * Author: marszhang
 * Date: 2016年3月20日
 */
public class MyStopAnalyzer extends Analyzer {
    private Set<Object> stopWords;

    public MyStopAnalyzer(String[] stopWords) {
        this.stopWords = StopFilter.makeStopSet(Version.LUCENE_35, stopWords, true);
        //在自定义的停用词里面再添加上原始停用词；
        this.stopWords.addAll(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
    }

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        return new StopFilter(Version.LUCENE_35,
               new LowerCaseFilter(Version.LUCENE_35,
               new LetterTokenizer(Version.LUCENE_35, reader)),
               stopWords);
    }
}
