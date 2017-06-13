package schedule;

import crawler.SubCategoryCrawler;

/**
 * Created by NIC on 6/13/17.
 */
public class LevelTwo implements Runnable {
    static final String categoryUrlPath = "src/main/resources/OriginURL_Electronics&Computer.txt";
    static final String subCategoryUrlPath="src/main/resources/LogURL_Electronics&Computer.txt";
    static final String proxyPath = "src/main/resources/proxylist_bittiger.csv";
    static final String MQName = "LevelTwo";
    @Override
    public void run() {
        SubCategoryCrawler subCategoryCrawler = new SubCategoryCrawler(proxyPath);
        try {
            subCategoryCrawler.exploreSubCategoryLinks(categoryUrlPath, subCategoryUrlPath);
            subCategoryCrawler.getDetailProductInfo(subCategoryUrlPath, MQName, "Electronics&Computer");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
