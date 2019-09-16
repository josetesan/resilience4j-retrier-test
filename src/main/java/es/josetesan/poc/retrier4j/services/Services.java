package es.josetesan.poc.retrier4j.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Services {

    private static final Logger LOGGER = LoggerFactory.getLogger(Services.class);

    public  Integer  returnValue() throws InterruptedException, IOException {

        Double wait = new Random().nextDouble() * 100d;
        LOGGER.info("going to wait {}", wait.intValue());
        if (wait.intValue()% 2 == 0) throw new IOException("Wait is odd");
        TimeUnit.MILLISECONDS.sleep(wait.longValue());
        return 4;

    }
}
