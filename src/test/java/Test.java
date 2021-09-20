import love.forte.utils.slowdown.RunnableTask;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ForteScarlet
 */
public class Test {
    public static void main(String[] args) {
        String a = "5";

        ScheduledExecutorService service = Executors.newScheduledThreadPool(1);

        RunnableTask firstTask = new RunnableTask(service);

        firstTask
                .then(() -> {
                    System.out.println("task 1");
                    System.out.println(LocalDateTime.now());
                    return 5;
                })
                .then(2, TimeUnit.SECONDS, i -> {
                    System.out.println("task 2");
                    System.out.println(LocalDateTime.now());
                    return i + 5.0;
                })
                .then(d -> d + 5)
                .then(d -> d * 5)
                .thenAlsoWait(5, TimeUnit.SECONDS)
                .then(2, TimeUnit.SECONDS, i -> {
                    System.out.println("Just Nothing. " + i);
                    System.out.println(LocalDateTime.now());
                })
                .then(2, TimeUnit.SECONDS, () -> {
                    System.out.println("OH, haha.");
                    System.out.println(LocalDateTime.now());
                    return "haha";
                })
                .end(2, TimeUnit.SECONDS, i -> {
                    System.out.println("i = " + i);
                    System.out.println(LocalDateTime.now());
                    service.shutdown();
                });

        firstTask.invoke();

    }
}
