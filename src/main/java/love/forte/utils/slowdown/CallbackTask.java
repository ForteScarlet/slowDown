package love.forte.utils.slowdown;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author ForteScarlet
 */
public class CallbackTask<T> {

    private final ScheduledExecutorService service;

    private final long delay;
    private final TimeUnit timeUnit;

    private Consumer<T> callback;


    public CallbackTask(
            ScheduledExecutorService service,
            long delay,
            TimeUnit timeUnit
    ) {
        this.service = service;
        // this.task = task;
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    public void setCallback(Consumer<T> callback) {
        this.callback = callback;
    }

    public <R> CallbackTask<R> setCallback(long delay, TimeUnit timeUnit, Function<T, R> callbackFunction) {
        CallbackTask<R> callbackTask = new CallbackTask<>(
                service, delay, timeUnit
        );
        callback = t -> callbackTask.invoke(callbackFunction.apply(t));
        return callbackTask;
    }

    public void end(long delay, TimeUnit timeUnit, Consumer<T> callbackConsumer) {
        if (delay <= 0) {
            callback = callbackConsumer;
        } else {
            callback = t -> service.schedule(() -> callbackConsumer.accept(t), delay, timeUnit);
        }
    }

    public void invoke(T t) {
        if (delay <= 0) {
            if (callback != null) {
                callback.accept(t);
            }
        } else {
            service.schedule(() -> {
                if (callback != null) {
                    callback.accept(t);
                }
            }, delay, timeUnit);
        }
    }

}
