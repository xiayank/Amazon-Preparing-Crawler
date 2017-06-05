package crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.*;
// /import java.util.*;

/**
 * Created by NIC on 5/31/17.
 */
public class SubCategoryLinkCrawler {

    private final String proxyUser = "bittiger";
    private final String proxyPassword = "cs504";
    private List<String> proxyList;
    private int indexForProxyList = 0;




    public SubCategoryLinkCrawler(String proxyPath){
        initProxyList(proxyPath);
    }

    public void exploreSubCategoryLinks(String categoryUrlPath, String subCategoryUrlPath) throws IOException {

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, sdch, br");
        headers.put("Accept-Language", "en-US,en;q=0.8");
        String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";

        File file = new File(subCategoryUrlPath);
        if(!file.exists()){
            file.createNewFile();

        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));


        BufferedReader br = new BufferedReader(new FileReader(categoryUrlPath));

        String urlLine;
        Queue<String> queue = new LinkedList<>();
        Set<String>hashSet = new HashSet<>();
        while ((urlLine = br.readLine()) != null){
            if(hashSet.contains(urlLine)){
                continue;
            }
            hashSet.add(urlLine);
            queue.offer(urlLine);

        }
        while (!queue.isEmpty()){
            changeProxy();
            //change ip for every url
            String url = queue.poll();
            if (url.isEmpty())
                continue;
            url = url.trim();
            System.out.println("url need to be crawled " +url);
            try {
                Document doc = Jsoup.connect(url).headers(headers).userAgent(USER_AGENT).maxBodySize(0).timeout(1000000).get();

                //Elements elements = doc.select("li[class=sub-categories__list__item]>a");
                if(doc != null){
                    //Elements elements = doc.select("a[class=sub-categories__list__item__link]");
                    //Elements elements = doc.select("div[class=bxc-grid__image   bxc-grid__image--light]");
                    Elements elements = doc.select("span[class=nav-a-content]");
                    System.out.println(elements.size());
                    if(elements.size() == 0){
                        queue.offer(url);
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
                queue.offer(url);

            }catch (InterruptedException ex){
                Thread.currentThread().interrupt();
            }
        }

        bw.close();

    }

    public void getDetailProductInfo (String subCategoryUrlPath, String productDetailLogPath)throws IOException {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, sdch, br");
        headers.put("Accept-Language", "en-US,en;q=0.8");
        String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";

        BufferedReader br = new BufferedReader(new FileReader(subCategoryUrlPath));

        File file = new File(productDetailLogPath);
        if(!file.exists()){
            file.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()));
        String urlLine;
        while ((urlLine = br.readLine())!=null){
            try {
                Document doc = Jsoup.connect(urlLine).headers(headers).userAgent(USER_AGENT).timeout(1000000).get();
                Elements elements = doc.select("li[class=s-result-item  celwidget ]");
                System.out.println("url need to be crawled " +urlLine);
                System.out.println(elements.size());
            }catch (IllegalArgumentException e){

                System.out.println(e.toString());
                System.out.println(urlLine);
            }

        }


    }

    private  void initProxyList(String proxyFile) {
        proxyList = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(proxyFile))) {
            String line;
            while((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                String ip = fields[0].trim();
                proxyList.add(ip);
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        Authenticator.setDefault(
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(proxyUser, proxyPassword.toCharArray());
                    }
                }
        );

        System.setProperty("http.proxyUser", proxyUser);
        System.setProperty("http.proxyPassword", proxyPassword);
        System.setProperty("socksProxyPort", "61336");
    }
    public  void changeProxy() {
        indexForProxyList = (indexForProxyList + 1) % proxyList.size();
        String proxy = proxyList.get(indexForProxyList);
        System.setProperty("socksProxyHost", proxy);
    }
}
