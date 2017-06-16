package schedule;

import crawler.SubCategoryCrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
        String erroLogPath = "/Users/NIC/Documents/504_BankEnd/MyCode/Intellij_WorkSpace/crawler-test/src/main/resources/errorLog.txt";
        BufferedWriter bwError = null;

        try {
            bwError = new BufferedWriter(new FileWriter(new File(erroLogPath).getAbsoluteFile()));
            subCategoryCrawler.exploreSubCategoryLinks(categoryUrlPath, subCategoryUrlPath);
            subCategoryCrawler.getDetailProductInfo(subCategoryUrlPath, MQName, "Sports&Outdoors");
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
