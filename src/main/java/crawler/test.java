package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by NIC on 5/30/17.
 */
public class test {


    public static void main(String args[]) throws IOException {

        AmazonCrawler amazonCrawler = new AmazonCrawler("/Users/NIC/Documents/504_BankEnd/HW/HW3_Clawer/proxylist_bittiger.csv");

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, sdch, br");
        headers.put("Accept-Language", "en-US,en;q=0.8");
        String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";

        String cralwedUrlPath="/Users/NIC/Documents/504_BankEnd/HW/HW5_PriceMonitor/logURL.txt";
        File file = new File(cralwedUrlPath);
//        if(!file.exists()){
//            file.createNewFile();
//        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));

        File cFile = new File("/Users/NIC/Documents/504_BankEnd/HW/HW5_PriceMonitor/crawlAgain_size.txt");
//        if(!cFile.exists()){
//            cFile.createNewFile();
//        }
        BufferedWriter cralwAgain_size = new BufferedWriter(new FileWriter(cFile.getAbsoluteFile()));

        File sFile = new File("/Users/NIC/Documents/504_BankEnd/HW/HW5_PriceMonitor/crawlAgain_string.txt");
//        if(!sFile.exists()){
//            sFile.createNewFile();
//        }
        BufferedWriter cralwAgain_string = new BufferedWriter(new FileWriter(sFile.getAbsoluteFile()));


        String categoryUrlPath = "/Users/NIC/Documents/504_BankEnd/HW/HW5_PriceMonitor/originalURL.txt";
        BufferedReader br = new BufferedReader(new FileReader(categoryUrlPath));
        //String url = "https://www.amazon.com/Handmade-Stationery-Party-Supplies/b/ref=nav_shopall_HM_stationery?ie=UTF8&node=11435470011";
        String urlLine;
        Queue<String> queue = new LinkedList<>();
        while ((urlLine = br.readLine()) != null){
            queue.offer(urlLine);

        }
        while (!queue.isEmpty()){

            amazonCrawler.changeProxy();
            String url = queue.poll();
            if (url.isEmpty())
                continue;
            url = url.trim();
            System.out.println("url need to be crawled " +url);
            try {
                Document doc = Jsoup.connect(url).headers(headers).userAgent(USER_AGENT).timeout(1000000).get();
                //System.out.println(doc.text());
                //Elements elements = doc.select("li[class=sub-categories__list__item]>a");
                if(doc != null){
                    //Elements elements = doc.select("a[class=sub-categories__list__item__link]");
                    //Elements elements = doc.select("div[class=bxc-grid__image   bxc-grid__image--light]");
                    Elements elements = doc.select("span[class=nav-a-content]");
                    System.out.println(elements.size());
                    if(elements.size() == 0){
//                        cralwAgain_size.write(url);
//                        cralwAgain_size.newLine();
                    }
                    for(int i = 2; i <= elements.size(); i++){
                        String css = "#nav-subnav > a:nth-child(" + Integer.toString(i) +")";
                        Element element = doc.select(css).first();
                        if(element != null){
                            String href = element.attr("href");
                            System.out.println("https://www.amazon.com"+href);
                            bw.write("https://www.amazon.com"+href);
                            bw.newLine();

                        }

                    }
                    Thread.sleep(2000);

                }

            } catch (IllegalArgumentException e) {
                System.out.println(e.toString());
                cralwAgain_string.write(url);
                cralwAgain_string.newLine();
                //e.printStackTrace();
            }catch (InterruptedException ex){
                Thread.currentThread().interrupt();
            }
        }

        bw.close();
        cralwAgain_size.close();
        cralwAgain_string.close();
    }


}
