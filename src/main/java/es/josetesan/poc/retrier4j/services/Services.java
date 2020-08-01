package es.josetesan.poc.retrier4j.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Services {

    private static final Logger LOGGER = LoggerFactory.getLogger(Services.class);

    public  Integer  returnValue() throws InterruptedException, IOException {

        long wait = new Random().nextInt(5000) ;
        LOGGER.info("going to wait {} ms", Math.abs(wait));
        if ((int) wait % 2 == 0) throw new IOException("Wait is odd");
        TimeUnit.MILLISECONDS.sleep(Math.abs(wait));
        return 4;

    }
}
