package crawler;

import schedule.LevelOne;
import schedule.LevelTwo;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by NIC on 5/30/17.
 */
public class CrawlerMain {



    public static void main(String args[]) throws Exception {

        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
        LevelOne levelOne = new LevelOne();
        LevelTwo levelTwo = new LevelTwo();
        scheduledThreadPool.scheduleAtFixedRate(levelOne,1,120, TimeUnit.SECONDS);
        scheduledThreadPool.scheduleAtFixedRate(levelTwo,1,180, TimeUnit.SECONDS);









    }

}
