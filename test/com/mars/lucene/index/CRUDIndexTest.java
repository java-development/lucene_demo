package com.mars.lucene.index;

import org.junit.Before;
import org.junit.Test;

public class CRUDIndexTest {
    private static CRUDIndex index = null;
    private static final String INDEX_DIR = "D:/TestFiles/lucene/index01";
    private static final String FILE_DIR = "D:/TestFiles/lucene/example";

     @Before
     public void init() {
          index = new CRUDIndex();
     }

     @Test
     public void testCreateIndex() {
          index.createIndex(INDEX_DIR, FILE_DIR);
     }

     @Test
     public void testSearcher() {
         index.searcher(INDEX_DIR, "fileContent", "java");
     }

     @Test
     public void testDeleteIndex() {
         index.deleteIndex(INDEX_DIR);
     }

     @Test
     public void testUnDeleteIndex() {
         index.unDeleteIndex(INDEX_DIR);
     }

     @Test
     public void testUpdateIndex() {
         index.updateIndex(INDEX_DIR);
     }
}
