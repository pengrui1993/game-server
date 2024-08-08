package test.lucene;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HelloLuceneTest {
    static String path = "d:/temp/hospital.txt";
    static final String field = "content";
    static List<String> dataList = new ArrayList<>();
    static Directory dir = null;
    static Analyzer ana = null;
    static{
        URL resource = HelloLuceneTest.class.getClassLoader().getResource("hospital.txt");
        assert null!=resource;
        path = resource.toString();
//        System.out.println(resource);
    }
    static void createToDisk() throws IOException, ParseException {
        long begin = System.currentTimeMillis();
        buildIndexOnDisk();
        System.out.println("spend :"+(System.currentTimeMillis()-begin)+"ms");
        waitQuery();
    }
    static void createToMemory() throws IOException, ParseException {
        long begin = System.currentTimeMillis();
        buildIndexInMemory();
        System.out.println("spend :"+(System.currentTimeMillis()-begin)+"ms");
        waitQuery();
    }
    static void info() throws Exception {
        FSDirectory index = FSDirectory.open(Paths.get("/tmp/lucene"));
        System.out.println(Arrays.stream(index.listAll()).collect(Collectors.toList()));
        index.close();
    }
    public static void main(String[] args) throws Exception {
//        createToDisk();
//        createToMemory();
//        info();
        TopDocs hos = search("三级甲等", 11,110,500);
    }

    private static void loadData() throws IOException {
        Scanner s = //new Scanner(Files.newInputStream(Paths.get(path)));
                new Scanner(ClassLoader.getSystemResourceAsStream("hospital.txt"));
        while(s.hasNext()){
            String s1 = s.nextLine();
            if(null!=s1&& !s1.trim().isEmpty()){
                dataList.add(s1);
            }
        }
        s.close();
    }

    private static void loadData(Consumer<String> line) throws IOException {
        Scanner s = //new Scanner(Files.newInputStream(Paths.get(path)));
                new Scanner(ClassLoader.getSystemResourceAsStream("hospital.txt"));
        while(s.hasNext()){
            String s1 = s.nextLine();
            if(null!=s1&& !s1.trim().isEmpty()){
                line.accept(s1);
            }
        }
        s.close();
    }
    private static void buildIndexInMemory() throws IOException {
        Analyzer analyzer = //new StandardAnalyzer();
                new IKAnalyzer();
        ana = analyzer;
        // 1. create the index
        Directory index = new ByteBuffersDirectory();
        dir = index;
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);
        loadData();
        for (String s : dataList) {
            Document doc = new Document();
            doc.add(new TextField(field, s, Field.Store.YES));
            w.addDocument(doc);
        }
        w.close();
    }
    private static void buildIndexOnDisk() throws IOException {
        Analyzer analyzer = //new StandardAnalyzer();
                new IKAnalyzer();
        ana = analyzer;
        // 1. create the index
//        Directory index = new ByteBuffersDirectory();
        FSDirectory index = FSDirectory.open(Paths.get("/tmp/lucene"));
        dir = index;
        if(index.listAll().length>0)
            return;
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter w = new IndexWriter(index, config);
        loadData(s->{
            Document doc = new Document();
            doc.add(new TextField(field, s, Field.Store.YES));
            try {
                w.addDocument(doc);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        w.close();
    }

    /**
     * 医院
     * total:706 hits
     * 1.	华东医院 综合医院 三级甲等 上海市
     * 2.	浙江医院 综合医院 三级甲等 浙江省
     * 3.	开滦总医院 综合医院 三级甲等 河北省
     * 4.	烟台毓璜顶医院 综合医院 三级甲等 山东省
     * 5.	西安高新医院 综合医院 三级甲等 陕西省
     * 6.	北京博爱医院 综合医院 三级甲等 北京市
     * 7.	北京回龙观医院 专科医院 三级甲等 北京市
     * 8.	江苏大学附属医院 综合医院 三级甲等 江苏省
     * 9.	南通大学附属医院 综合医院 三级甲等 江苏省
     * 10.	淮北矿工总医院 综合医院 三级甲等 安徽省
     * next
     * total:706 hits
     * 1.	漳州市医院 综合医院 三级甲等 福建省
     * 2.	赣州市立医院 综合医院 三级甲等 江西省
     * 3.	枣庄市立医院 综合医院 三级甲等 山东省
     * 4.	河南大学淮河医院 综合医院 三级甲等 河南省
     * 5.	武汉大学中南医院 综合医院 三级甲等 湖北省
     * 6.	青海大学附属医院 综合医院 三级甲等 青海省
     * 7.	北京积水潭医院 综合医院 三级甲等 北京市
     * 8.	北京肿瘤医院 专科医院 三级甲等 北京市
     * 9.	卫生部北京医院 综合医院 三级甲等 北京市
     * 10.	天津市环湖医院 综合医院 三级甲等 天津市
     * @throws ParseException
     * @throws IOException
     */
    private static void waitQuery() throws ParseException, IOException {
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser qp = new QueryParser(field, ana);
        Scanner sc = new Scanner(System.in);
        String line;
        ScoreDoc pre = null;
        String preLine = null;
        dataList.clear();
        while(null!=(line=sc.nextLine())){
            if("quit".equals(line))
                break;
            else if("next".equals(line)){
                Query q = qp.parse(preLine);
//                TopDocs docs = searcher.searchAfter(pre, q,10, Sort.INDEXORDER);
                TopDocs docs = searcher.searchAfter(pre, q,10);
                pre = docs.scoreDocs[docs.scoreDocs.length-1];
                printDoc(docs,searcher);
                continue;
            }
            preLine = line;
            Query q = qp.parse(line);
            TopDocs docs = searcher.search(q, 10);
            pre = docs.scoreDocs[docs.scoreDocs.length-1];
            printDoc(docs,searcher);
        }
    }

    private static void printDoc(TopDocs docs,IndexSearcher searcher) throws IOException {
        ScoreDoc[] hits = docs.scoreDocs;
        System.out.println("total:"+docs.totalHits);
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ".\t" + d.get(field));
        }
    }
    static long now(){ return System.currentTimeMillis();}
    /*
        if pageNumber*pageSize > numHits then hit 0
     */
    public static TopDocs search(String query, int pageNumber,int numHits,int totalHitsTr) throws IOException, ParseException {
        long start = now();
        buildIndexOnDisk();
        IndexReader reader = DirectoryReader.open(dir);
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser parser = new QueryParser(field, ana);
        Query searchQuery = parser.parse(query);
        TopScoreDocCollector collector = TopScoreDocCollector.create(numHits, totalHitsTr);
        final int SEARCH_RESULT_PAGE_SIZE = 10;
        int startIndex = (pageNumber - 1) * SEARCH_RESULT_PAGE_SIZE;
        searcher.search(searchQuery, collector);
        TopDocs topDocs = collector.topDocs(startIndex, SEARCH_RESULT_PAGE_SIZE);
        printDoc(topDocs,searcher);
//        System.out.println(topDocs.totalHits.value);//719
//        System.out.println(topDocs.totalHits.relation);//GREATER_THAN_OR_EQUAL_TO
        System.out.println(topDocs.totalHits+" time:"+(now()-start));//719+ hits time:245
        reader.close();
        return topDocs;
    }
}
