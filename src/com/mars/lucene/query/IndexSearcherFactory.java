package com.mars.lucene.query;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
/**
 * Author: marszhang
 * Date: 2016年3月12日
 */
public class IndexSearcherFactory {
    private static IndexReader reader = null;

    //采用单例模式，整个进程中就只有一个IndexReader实例；
    public synchronized static IndexSearcher getIndexReader(Directory dir) {
        try {
            if (reader == null) {
                reader = IndexReader.open(dir);
            } else {
                IndexReader changReader = IndexReader.openIfChanged(reader);
                if (changReader != null) {
                    reader.close();
                    reader = changReader;
                }
            }
            return new IndexSearcher(reader);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
