package schedule;

import crawler.SubCategoryCrawler;

/**
 * Created by NIC on 6/13/17.
 */
public class LevelOne implements Runnable{
    static final String categoryUrlPath = "src/main/resources/OriginURL_Sports&Outdoors.txt";
    static final String subCategoryUrlPath="src/main/resources/LogURL_Sports&Outdoors.txt";
    static final String proxyPath = "src/main/resources/proxylist_bittiger.csv";
    static final String MQName = "LevelOne";
    @Override
    public void run() {
        SubCategoryCrawler subCategoryCrawler = new SubCategoryCrawler(proxyPath);
        try {
            subCategoryCrawler.exploreSubCategoryLinks(categoryUrlPath, subCategoryUrlPath);
            subCategoryCrawler.getDetailProductInfo(subCategoryUrlPath, MQName, "Sports&Outdoors");

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
