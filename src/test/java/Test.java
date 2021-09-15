import love.forte.utils.slowdown.CallbackTask;

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

        CallbackTask<String> firstTask = new CallbackTask<>(service, 2, TimeUnit.SECONDS);

        firstTask
                .setCallback(2, TimeUnit.SECONDS, aa -> {
                    System.out.println("task 1");
                    return Integer.parseInt(aa);
                })
                .setCallback(2, TimeUnit.SECONDS, i -> {
                    System.out.println("task 2");
                    return i + 5;
                })
                .end(2, TimeUnit.SECONDS, i -> {
                    System.out.println("i = " + i);
                    service.shutdown();
                });

        firstTask.invoke(a);

    }
}
