package com.mars.lucene.index;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

/**
 * Author: marszhang
 * Date: 2016年3月11日
 */
public class CRUDIndex {

    /**
     * 创建索引步骤:
     * 1. 创建Directory
     * 2. 创建IndexWriter
     * 3. 创建Document
     * 4. 为Document添加Field
     * 5. 通过IndexWriter将Document添加到Index中
     * 6. 关闭IO流
     */
    public void createIndex(String indexDir, String fileDir) {
        IndexWriter iw = null;
        try {
            Directory dir = FSDirectory.open(new File(indexDir));
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35,
                    new StandardAnalyzer(Version.LUCENE_35));
            iw = new IndexWriter(dir, iwc);
            File files = new File(fileDir);
            for (File file : files.listFiles()) {
                Document doc = new Document();
                doc.add(new Field("fileContent", FileUtils.readFileToString(file),
                        Field.Store.NO,
                        Field.Index.ANALYZED));
                doc.add(new Field("fileName", file.getName(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                doc.add(new Field("filePath", file.getAbsolutePath(),
                        Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
                iw.addDocument(doc);
            }
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                iw.close();
            } catch (CorruptIndexException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 搜索步骤：
     * 1. 创建Directory
     * 2. 创建IndexReader
     * 3. 根据IndexReader创建IndexSearcher
     * 4. 创建Query
     * 5. 根据IndexSearcher搜索返回TopDocs
     * 6. 根据TopDocs返回ScoreDoc
     * 7. 根据IndexSearcher和ScoreDoc获取对应的Document
     * 8. 根据Document对象获取需要的值
     */
    public void searcher(String indexDir, String field, String keyWord) {
        IndexSearcher searcher = null;
        IndexReader indexReader = null;
        try {
            Directory dir = FSDirectory.open(new File(indexDir));
            indexReader = IndexReader.open(dir);

            System.out.println("numDocs : " + indexReader.numDocs());
            System.out.println("maxDoc : " + indexReader.maxDoc());
            System.out.println("numDeletedDocs : " + indexReader.numDeletedDocs());

            searcher = new IndexSearcher(indexReader);
            QueryParser parser = new QueryParser(Version.LUCENE_35, field,
                    new StandardAnalyzer(Version.LUCENE_35));
            Query query = parser.parse(keyWord);
            TopDocs topDocs = searcher.search(query, 10);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println("fileName : " + doc.get("fileName"));
                System.out.println("filePath : " + doc.get("filePath"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            try {
                searcher.close();
                indexReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除索引;
     * 说明：该方法不会完全删除Document，而是会将Document放在一个"回收站"中，可以恢复；
     *     回收站：在索引目录中会出现"_0_1.del"这样的一个文件；
     */
    public void deleteIndex(String indexDir) {
        IndexWriter iw = null;
        try {
            Directory dir = FSDirectory.open(new File(indexDir));
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35,
                    new StandardAnalyzer(Version.LUCENE_35));
            iw = new IndexWriter(dir, iwc);
            iw.deleteDocuments(new Term("fileName", "requirement - 副本 (97).txt"));
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                iw.close();
            } catch (CorruptIndexException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 恢复被删除的索引;
     */
    public void unDeleteIndex(String indexDir) {
        IndexReader reader = null;
        try {
            Directory dir = FSDirectory.open(new File(indexDir));
            //恢复时，必须把IndexReader的只读(readOnly)设置为false；
            reader = IndexReader.open(dir, false);
            reader.undeleteAll();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (CorruptIndexException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 跟新索引;
     */
    public void updateIndex(String indexDir) {
        IndexWriter iw = null;
        try {
            Directory dir = FSDirectory.open(new File(indexDir));
            IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35,
                    new StandardAnalyzer(Version.LUCENE_35));
            iw = new IndexWriter(dir, iwc);

            //要修改成为的Document；
            Document doc = new Document();
            doc.add(new Field("fileContent", "This is the new java file content",
                    Field.Store.NO,
                    Field.Index.ANALYZED));
            doc.add(new Field("fileName", "This is the new file name",
                    Field.Store.YES,
                    Field.Index.NOT_ANALYZED));
            doc.add(new Field("filePath", "This is the new file path",
                    Field.Store.YES,
                    Field.Index.NOT_ANALYZED));

            //将Term("fileName", "requirement - 副本 (97).txt")这条索引替换成doc
            iw.updateDocument(new Term("fileName", "requirement - 副本 (97).txt"), doc);
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                iw.close();
            } catch (CorruptIndexException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
