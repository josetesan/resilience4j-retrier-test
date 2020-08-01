package es.josetesan.poc.retrier4j;

import es.josetesan.poc.retrier4j.services.Services;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Main {


    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {


        Services services = new Services();

        IntervalFunction intervalWithCustomExponentialBackoff = IntervalFunction
            .ofExponentialBackoff(IntervalFunction.DEFAULT_INITIAL_INTERVAL, 2d);

        RetryConfig config = RetryConfig.custom()
            .maxAttempts(5)
            .intervalFunction(intervalWithCustomExponentialBackoff)
            .retryExceptions(IOException.class)
            .build();

        RetryRegistry registry = RetryRegistry.of(config);

        var retryWithDefaultConfig = registry.retry("name1");

        var retryableSupplier = Retry
            .decorateCheckedSupplier(retryWithDefaultConfig, services::returnValue);

        var result = Try.of(retryableSupplier)
             .recover(throwable -> {
                 LOGGER.error(throwable.getMessage());
                 return -5;
             })
            .onFailure(throwable -> {
                LOGGER.error("Exiting !!");
                System.exit(-1);
            })

            ;
        /**
         *
         * with //            .recover(throwable -> -5)
         *
         * 23:35:47:599 [main] INFO es.josetesan.poc.retrier4j.services.Services - going to wait 86
         * 23:35:48:110 [main] INFO es.josetesan.poc.retrier4j.services.Services - going to wait 64
         * 23:35:49:110 [main] INFO es.josetesan.poc.retrier4j.services.Services - going to wait 72
         * 23:35:49:117 [main] INFO es.josetesan.poc.retrier4j.Main - The result is -5
         */

        /**
        23:36:06:128 [main] INFO es.josetesan.poc.retrier4j.Main - Retry name1 added
        23:36:06:139 [main] INFO es.josetesan.poc.retrier4j.services.Services - going to wait 37
        23:36:06:177 [main] INFO es.josetesan.poc.retrier4j.Main - The result is 4
         */

        /**
         * 23:36:29:924 [main] INFO es.josetesan.poc.retrier4j.Main - Retry name1 added
         * 23:36:29:935 [main] INFO es.josetesan.poc.retrier4j.services.Services - going to wait 28
         * 23:36:30:445 [main] INFO es.josetesan.poc.retrier4j.services.Services - going to wait 13
         * 23:36:30:462 [main] INFO es.josetesan.poc.retrier4j.Main - The result is 4
         */

        /**
         *
         *            .onFailure(throwable -> {
         *                 LOGGER.error("Exiting !!");
         *                 System.exit(-1);
         *             })
         *
        23:39:41:073 [main] INFO es.josetesan.poc.retrier4j.Main - Retry name1 added
        23:39:41:083 [main] INFO es.josetesan.poc.retrier4j.services.Services - going to wait 64
        23:39:41:594 [main] INFO es.josetesan.poc.retrier4j.services.Services - going to wait 56
        23:39:42:595 [main] INFO es.josetesan.poc.retrier4j.services.Services - going to wait 8
        23:39:42:601 [main] ERROR es.josetesan.poc.retrier4j.Main - Exiting !!
        */
        Integer value = result.get();
        LOGGER.info("The result is {}", value);
    }
}
