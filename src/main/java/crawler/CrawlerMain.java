package crawler;

import java.io.IOException;

/**
 * Created by NIC on 5/30/17.
 */
public class test {

    static final String categoryUrlPath = "/Users/NIC/Documents/504_BankEnd/HW/HW5_PriceMonitor/originalURL.txt";
    static final String subCategoryUrlPath="/Users/NIC/Documents/504_BankEnd/HW/HW5_PriceMonitor/logURL.txt";
    public static void main(String args[]) throws IOException {

        SubCategoryLinkCrawler subCategoryLinkCrawler = new SubCategoryLinkCrawler("/Users/NIC/Documents/504_BankEnd/HW/HW3_Clawer/proxylist_bittiger.csv");
        subCategoryLinkCrawler.exploreSubCategoryLinks(categoryUrlPath, subCategoryUrlPath);


    }

}
