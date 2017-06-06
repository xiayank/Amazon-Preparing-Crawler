package crawler;

import java.io.IOException;

/**
 * Created by NIC on 5/30/17.
 */
public class CrawlerMain {

    static final String categoryUrlPath = "/Users/NIC/Documents/504_BankEnd/HW/HW5_PriceMonitor/originalURL.txt";
    static final String subCategoryUrlPath="/Users/NIC/Documents/504_BankEnd/HW/HW5_PriceMonitor/logURL.txt";
    static final String proxyPath = "/Users/NIC/Documents/504_BankEnd/HW/HW3_Clawer/proxylist_bittiger.csv";
    static final String productDetailLogPath = "/Users/NIC/Documents/504_BankEnd/HW/HW5_PriceMonitor/productDetail.txt";
    public static void main(String args[]) throws IOException {

        SubCategoryCrawler subCategoryCrawler = new SubCategoryCrawler(proxyPath);
        //subCategoryCrawler.exploreSubCategoryLinks(categoryUrlPath, subCategoryUrlPath);
        //for(int i = 0; i < 10; i++){
        subCategoryCrawler.getDetailProductInfo(subCategoryUrlPath,productDetailLogPath);
        //}
        //String s =  subCategoryCrawler.getIdFromDetailUrl("https://www.amazon.com/Harbinger-Padded-Leather-Contoured-Weightlifting/dp/B001OJ36GG/ref=sr_1_6?s=sports-and-fitness&ie=UTF8&qid=1496716624&sr=1-6&keywords=workout+belt");
        //System.out.println(s);
    }

}
