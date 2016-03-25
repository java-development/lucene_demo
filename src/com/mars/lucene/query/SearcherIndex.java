package com.mars.lucene.query;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;
/**
 * Author: marszhang
 * Date: 2016年3月12日
 * 
 * 正对中文搜索常用的查询方法：
 * 1. TermQuery:精确查找某一条数据；
 * 2. TermRangeQuery:查找某一范围内的匹配的字符串类型的数据；
 * 3. NumericRangeQuery:查找某一范围内的匹配的数字类型的数据；
 * 4. PrefixQuery:根据前缀搜索；
 * 5. WildcardQuery:根据通配符搜索；
 * 6. BooleanQuery:可以连接多个条件查询；
 * 7. QueryParser:关键字用逗号隔开，可以输入多个关键字查询，默认多个关键字是"或"的关系；
 * 
 * 注意：QueryParser方式使用很灵活，推荐使用；
 */
public class SearcherIndex {
    private Directory dir = null;
    private IndexData data = null;

    public SearcherIndex(String path) throws IOException {
        dir = FSDirectory.open(new File(path));
        data = new IndexData();
    }

    public void createIndex() {
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(dir,
                     new IndexWriterConfig(Version.LUCENE_35,
                     new StandardAnalyzer(Version.LUCENE_35)));
            //在创建新索引前删除所有索引，避免重复；
            writer.deleteAll();
            Document doc = null;
            for(int i=0;i<data.getIds().length;i++) {
                doc = new Document();
                doc.add(new Field("id", data.getIds()[i],
                            Field.Store.YES,
                            Field.Index.NOT_ANALYZED_NO_NORMS));
                doc.add(new Field("email",data.getEmails()[i],
                            Field.Store.YES,
                            Field.Index.NOT_ANALYZED));
                doc.add(new Field("content",data.getContents()[i],
                            Field.Store.NO,
                            Field.Index.ANALYZED));
                doc.add(new Field("name",data.getNames()[i],
                            Field.Store.YES,
                            Field.Index.NOT_ANALYZED_NO_NORMS));
                doc.add(new NumericField("attach",
                        Field.Store.YES, true).setIntValue(data.getAttachs()[i]));
                doc.add(new NumericField("date",
                        Field.Store.YES, true).setLongValue(data.getDates()[i].getTime()));
                String et = data.getEmails()[i].substring(data.getEmails()[i].lastIndexOf("@")+1);
                if(data.getScores().containsKey(et)) {
                    //排名加权
                    doc.setBoost(data.getScores().get(et));
                } else {
                    doc.setBoost(0.5f);
                }
                writer.addDocument(doc);
            }
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(writer != null) {
                try {
                    writer.close();
                } catch (CorruptIndexException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteIndex(String field, String keyWord) {
        IndexWriter writer = null;
        try {
            writer = new IndexWriter(dir,
                     new IndexWriterConfig(Version.LUCENE_35,
                     new StandardAnalyzer(Version.LUCENE_35)));
            writer.deleteDocuments(new Term(field, keyWord));
        } catch (CorruptIndexException e) {
            e.printStackTrace();
        } catch (LockObtainFailedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) writer.close();
            } catch (CorruptIndexException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TermQuery:精确查找某一条数据；
     */
    public void searcherByTerm(String field, String keyWord) {
        IndexSearcher searcher = null;
        try {
            searcher = IndexSearcherFactory.getIndexReader(dir);
            Query query = new TermQuery(new Term(field, keyWord));
            TopDocs topDocs = searcher.search(query, 10);
            System.out.println("totalHits(全部匹配的数量) : " + topDocs.totalHits);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc.get("id") + " ---> " + doc.get("name")
                        + " ---> " + doc.get("email")+" ---> "
                        + doc.get("attach") + " ---> "+doc.get("date"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (searcher != null) {
                try {
                    searcher.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * TermRangeQuery:查找某一范围内的匹配的字符串类型的数据；
     */
    public void searcherByTermRange(String field, String start, String end) {
        IndexSearcher searcher = null;
        try {
            searcher = IndexSearcherFactory.getIndexReader(dir);
            Query query = new TermRangeQuery(field, start, end, true, true);
            TopDocs topDocs = searcher.search(query, 10);
            System.out.println("totalHits(全部匹配的数量) : " + topDocs.totalHits);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc.get("id") + " ---> " + doc.get("name")
                        + " ---> " + doc.get("email")+" ---> "
                        + doc.get("attach") + " ---> "+doc.get("date"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (searcher != null) {
                try {
                    searcher.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * NumericRangeQuery:查找某一范围内的匹配的数字类型的数据；
     */
    public void searcherByNumericRange(String field, Integer min, Integer max) {
        IndexSearcher searcher = null;
        try {
            searcher = IndexSearcherFactory.getIndexReader(dir);
            Query query = NumericRangeQuery.newIntRange(field, min, max, true, true);
            TopDocs topDocs = searcher.search(query, 10);
            System.out.println("totalHits(全部匹配的数量) : " + topDocs.totalHits);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc.get("id") + " ---> " + doc.get("name")
                        + " ---> " + doc.get("email")+" ---> "
                        + doc.get("attach") + " ---> "+doc.get("date"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (searcher != null) {
                try {
                    searcher.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * PrefixQuery:根据前缀搜索；
     */
    public void searcherByPrefix(String field, String prefix) {
        IndexSearcher searcher = null;
        try {
            searcher = IndexSearcherFactory.getIndexReader(dir);
            Query query = new PrefixQuery(new Term(field, prefix));
            TopDocs topDocs = searcher.search(query, 10);
            System.out.println("totalHits(全部匹配的数量) : " + topDocs.totalHits);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc.get("id") + " ---> " + doc.get("name")
                        + " ---> " + doc.get("email")+" ---> "
                        + doc.get("attach") + " ---> "+doc.get("date"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (searcher != null) {
                try {
                    searcher.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * WildcardQuery:根据通配符搜索；
     */
    public void searcherByWildcard(String field, String wildCard) {
        IndexSearcher searcher = null;
        try {
            searcher = IndexSearcherFactory.getIndexReader(dir);
            Query query = new WildcardQuery(new Term(field, wildCard));
            TopDocs topDocs = searcher.search(query, 10);
            System.out.println("totalHits(全部匹配的数量) : " + topDocs.totalHits);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc.get("id") + " ---> " + doc.get("name")
                        + " ---> " + doc.get("email")+" ---> "
                        + doc.get("attach") + " ---> "+doc.get("date"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (searcher != null) {
                try {
                    searcher.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * BooleanQuery:可以连接多个条件查询；
     */
    public void searcherByBoolean() {
        IndexSearcher searcher = null;
        try {
            searcher = IndexSearcherFactory.getIndexReader(dir);
            BooleanQuery query = new BooleanQuery();
            /**
             * Occur.MUST:必须满足该条件；
             * Occur.MUST_NOT:必须不能满足该条件；
             * Occur.SHOULD:有没有都可以，相当于or；
             */
            query.add(new TermQuery(new Term("content", "football")), Occur.MUST);
            query.add(new TermQuery(new Term("content", "basketball")), Occur.SHOULD);
            TopDocs topDocs = searcher.search(query, 10);
            System.out.println("totalHits(全部匹配的数量) : " + topDocs.totalHits);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc.get("id") + " ---> " + doc.get("name")
                        + " ---> " + doc.get("email")+" ---> "
                        + doc.get("attach") + " ---> "+doc.get("date"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (searcher != null) {
                try {
                    searcher.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * QueryParser:关键字用逗号隔开，可以输入多个关键字查询，默认多个关键字是"或"的关系；
     */
    public void searcherByQueryParser(String field, String keyWords) {
        IndexSearcher searcher = null;
        try {
            searcher = IndexSearcherFactory.getIndexReader(dir);
            QueryParser parser = new QueryParser(Version.LUCENE_35, field,
                                 new StandardAnalyzer(Version.LUCENE_35));
            //默认keyWords中间的空格为OR的关系，再此处可以设置为AND的关系；
            //parser.setDefaultOperator(Operator.AND);
            Query query = parser.parse(keyWords);
            TopDocs topDocs = searcher.search(query, 10);
            System.out.println("totalHits(全部匹配的数量) : " + topDocs.totalHits);
            ScoreDoc[] scoreDocs = topDocs.scoreDocs;
            for (ScoreDoc scoreDoc : scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                System.out.println(doc.get("id") + " ---> " + doc.get("name")
                        + " ---> " + doc.get("email")+" ---> "
                        + doc.get("attach") + " ---> "+doc.get("date"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } finally {
            if (searcher != null) {
                try {
                    searcher.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
