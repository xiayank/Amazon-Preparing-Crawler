package crawler;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.apache.commons.lang.SerializationUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import product.Product;

import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.*;
import java.util.concurrent.TimeoutException;
// /import java.util.*;

/**
 * Created by NIC on 5/31/17.
 */
public class SubCategoryCrawler {

    private final String proxyUser = "bittiger";
    private final String proxyPassword = "cs504";
    private List<String> proxyList;
    private int indexForProxyList = 0;
    private List<String>titleListSelector;
    private List<String>resultSizeSelector;
    private  HashSet<String> crawledUrl = new HashSet<>();


    public SubCategoryCrawler(String proxyPath){
        initProxyList(proxyPath);
        initSelector();
    }
    private void initSelector(){
        titleListSelector = new ArrayList<>();
        titleListSelector.add(" > div > div:nth-child(3) > div:nth-child(1) > a > h2");
        titleListSelector.add("> div > div:nth-child(5) > div:nth-child(1) > a > h2");
        titleListSelector.add("> div > div.a-row.a-spacing-none > div.a-row.a-spacing-mini > a > h2");
        resultSizeSelector = new ArrayList<>();
        resultSizeSelector.add("li[class=s-result-item  celwidget ]");
        resultSizeSelector.add("li[class=s-result-item s-result-card-for-container a-declarative celwidget ]");
    }

    public void exploreSubCategoryLinks(String categoryUrlPath, String subCategoryUrlPath) throws IOException {

        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip");
        headers.put("Accept-Language", "en-US,en;q=0.8");
        String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";

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
            System.out.println("category link need to be crawled " +url);
            try {
                Document doc = Jsoup.connect(url).headers(headers).userAgent(USER_AGENT).maxBodySize(0).timeout(1000000).get();
                Set<String>subUrlHash = new HashSet<>();
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
                            if(subUrlHash.contains(href)){
                                continue;
                            }
                            subUrlHash.add(href);
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

    public void getDetailProductInfo (String subCategoryUrlPath, String productDetailLogPath) throws IOException, TimeoutException {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        headers.put("Accept-Encoding", "*");
        headers.put("Accept-Language", "en-US,en;q=0.8");
        String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.95 Safari/537.36";

        BufferedReader br = new BufferedReader(new FileReader(subCategoryUrlPath));


        BufferedWriter bwDetail = new BufferedWriter(new FileWriter(new File(productDetailLogPath).getAbsoluteFile()));
        BufferedWriter bwError = new BufferedWriter(new FileWriter(new File(productDetailLogPath).getAbsoluteFile()));
        String urlLine;
        while ((urlLine = br.readLine())!=null){
            try {
                Document doc = Jsoup.connect(urlLine).headers(headers).userAgent(USER_AGENT).timeout(1000000).get();
                changeProxy();

                int resultSize = getResultSzie(doc);
                System.out.println("product list page need to be crawled " +urlLine);
                System.out.println(resultSize);
                if(resultSize == 0){
                    bwError.write(urlLine);
                    continue;
                }
                //preparing message queue
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost("127.0.0.1");
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();

                for(int i = 0; i < resultSize; i++){
                    Product product = new Product();
                    //url
                    product.detailUrl  = getDetailUrlFromDoc(doc, i);
                    if(product.detailUrl.isEmpty()){
                        System.out.println("Empty url");
                        continue;
                    }
                    System.out.println("detailUrl --> " + product.detailUrl);


                    //Id
                    product.productId = getIdFromDetailUrl(product.detailUrl);
                    if(product.productId.isEmpty()){
                        continue;
                    }
                    System.out.println("Id        --> " + product.productId);

                    //title
                    product.title = getTitleFromDoc(doc,i);
                    if(product.title == ""){
                        System.out.println("Empty title");
                        continue;
                    }
                    System.out.println("title     --> " + product.title);
                    bwDetail.write("title     --> " + product.title);
                    bwDetail.newLine();

                    //price
                    product.price = getPriceFromDoc(doc,i);
                    if(product.price == 0){
                        System.out.println("Empty price");
                        continue;
                    }
                    System.out.println("price     --> " + product.price);
                    System.out.println("");

                    product.category = "Sports&Outdoors";

                    channel.queueDeclare("Q_demo",true,false,false,null);
                    channel.basicPublish("", "Q_demo", null, SerializationUtils.serialize(product));

                }
                //close message queue
                channel.close();
                connection.close();

            }catch (IllegalArgumentException e){

                System.out.println(e.toString());
                System.out.println(urlLine);
                bwError.write(urlLine);
                continue;
            }

        }


    }

    private double getPriceFromDoc(Document doc, int itemNum){
        String priceEleSelector = "#result_"+ Integer.toString(itemNum) + " > div > div:nth-child(4) > div:nth-child(1) > a > span";
        Element priceEle = doc.select(priceEleSelector).first();
        String rawPrice;
        String finalPrice;
        double dPrice;
        if(priceEle != null){
             rawPrice = priceEle.attr("aria-label");
            if(!rawPrice.isEmpty()){
                int found = rawPrice.indexOf('-');
                if(found == -1){
                    finalPrice = rawPrice.substring(1, rawPrice.length()).trim();
                    finalPrice = finalPrice.replace(".", "");

                }else{
                    finalPrice = rawPrice.substring(1, found -1).trim();
                    finalPrice = finalPrice.replace(".", "");
                }

                dPrice = Double.parseDouble(finalPrice) / 100.0;

                //System.out.println("double Price "+ dPrice);
                //System.out.println("raw price "+rawPrice);
                return dPrice;
            }
        }
        //finalPrice = "null Price";
        //rawPrice = "null Price";

        //System.out.println(rawPrice);
        return 0;
    }
    private String getIdFromDetailUrl(String url){
        int index = url.indexOf("/dp/");
        return index == -1 ? "": url.substring(index + 4, index + 14);
    }
    private int getResultSzie(Document doc){
        for(String resultSize: resultSizeSelector){
            Elements elements = doc.select(resultSize);
            if(elements.size() != 0){
                return elements.size();
            }
        }
        return  0;
    }

    private String getTitleFromDoc(Document doc, int itemNum){
        for(String titleSelector: titleListSelector){
            String titleEleSelector = "#result_"+Integer.toString(itemNum) + titleSelector;
            Element titleEle = doc.select(titleEleSelector).first();
            if(titleEle !=  null){
                return titleEle.text();
            }
        }

        return "";
    }

    private String getDetailUrlFromDoc(Document doc, int itemNum){
        String detailUrlSelector = "#result_"+Integer.toString(itemNum)+" > div > div:nth-child(3) > div:nth-child(1) > a";
        Element detailUrlEle = doc.select(detailUrlSelector).first();
        if(detailUrlEle != null){
            String detailUrl = detailUrlEle.attr("href");
            String normalizedUrl = normalizeUrl(detailUrl);
            if(crawledUrl.contains(normalizedUrl)){
                return "";
            }
            crawledUrl.add(normalizedUrl);
            return normalizedUrl;
        }else {
            return "";
        }

    }

    private String normalizeUrl(String url) {
        int i = url.indexOf("ref");
        return i == -1 ? url : url.substring(0, i - 1);// remove http, start at '8' instead of '0'

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
        System.out.println(proxy);
        System.setProperty("socksProxyHost", proxy);
    }
}
