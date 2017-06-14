package schedule;

import crawler.SubCategoryCrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
        String erroLogPath = "/Users/NIC/Documents/504_BankEnd/MyCode/Intellij_WorkSpace/crawler-test/src/main/resources/errorLog.txt";
        BufferedWriter bwError = null;
        try {
            bwError = new BufferedWriter(new FileWriter(new File(erroLogPath).getAbsoluteFile()));
            //subCategoryCrawler.exploreSubCategoryLinks(categoryUrlPath, subCategoryUrlPath);
            subCategoryCrawler.getDetailProductInfo(subCategoryUrlPath, MQName, "Electronics&Computer");

        } catch (IOException e) {
            System.out.println(e.toString());
            try {
                bwError.write(e.toString());
                bwError.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }




    }
}
