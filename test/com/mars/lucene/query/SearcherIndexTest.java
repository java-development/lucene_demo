package com.mars.lucene.query;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
/**
 * Author: marszhang
 * Date: 2016年3月12日
 */
public class SearcherIndexTest {
    private static final String INDEX_DIR = "D:/TestFiles/lucene/index02";
    private SearcherIndex searcher = null;

    @Before
    public void init() throws IOException {
        searcher = new SearcherIndex(INDEX_DIR);
    }

    @Test
    public void testCreateIndex() {
        searcher.createIndex();
    }

    @Test
    public void testdeleteIndex() {
        searcher.deleteIndex("id", "1");
    }

    @Test
    public void testSearcherByTerm() {
        searcher.searcherByTerm("id", "1");
    }

    @Test
    public void testSearcherByTermRange() {
        searcher.searcherByTermRange("id", "2", "5");
    }

    @Test
    public void testSearcherByNumericRange() {
        searcher.searcherByNumericRange("attach", 1, 3);
    }

    @Test
    public void testSearcherByPrefix() {
        searcher.searcherByPrefix("name", "z");
    }

    @Test
    public void testSearcherByWildcard() {
        /**
         * 通配符：
         * '*': 匹配所有的；
         * '?': 匹配一个；
         */
        searcher.searcherByWildcard("name", "*i*");
        System.out.println("--------------------");
        searcher.searcherByWildcard("name", "j???");
    }

    @Test
    public void testSearcherByBoolean() {
        searcher.searcherByBoolean();
    }

    @Test
    public void testSearcherByQueryParser() {
        searcher.searcherByQueryParser("content", "football basketball");
        //content是默认的搜索域，但是在搜索的时候可以指定搜索域"name"，并进行通配符匹配；
        //搜索出"name"域中以"j"开头的匹配项和"content"域中含有"basketball"的匹配项；
        searcher.searcherByQueryParser("content", "name:j* basketball");
        //"-"表示非，"+"表示是，必须放在域或关键字的前面；
        //搜索出"name"域不是以"j"开头，但是含有"content"域中的"basketball";
        searcher.searcherByQueryParser("content", "- name:j* + basketball");
        //匹配一个闭区间范围的字符串(不能匹配数字)，TO必须是大写，搜索出id为1、2和3的数据；
        searcher.searcherByQueryParser("content", "id:[1 TO 3]");
        //匹配一个开区间范围的字符串(不能匹配数字)，TO必须是大写，只搜索出id为2的数据；
        searcher.searcherByQueryParser("content", "id:{1 TO 3}");
        //短语匹配，将关键字用双引号(不能使用单引号)括起来，充当一个关键短语；
        searcher.searcherByQueryParser("content", "\"I like football\"");
        //匹配"I football"这个短语中间缺省1个单词的短语："I like football"
        searcher.searcherByQueryParser("content", "\"I football\"~1");
        //"~"表示模糊匹配，可以匹配到"name"域中"mike"和"jake"这两个相似的名字；
        searcher.searcherByQueryParser("content", "name:make~");
    }
}
