package love.forte.utils.slowdown;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author ForteScarlet
 */
public class FunctionTask<T> {

    private final ScheduledExecutorService service;

    private long delay;
    private TimeUnit timeUnit;

    private Consumer<T> callback;


    public FunctionTask(
            ScheduledExecutorService service
    ) {
        this.service = service;
    }

    public <R> FunctionTask<R> then(long delay, TimeUnit timeUnit, Function<T, R> callbackFunction) {
        delay(delay, timeUnit);

        FunctionTask<R> functionTask = new FunctionTask<>(service);
        callback = t -> functionTask.invoke(callbackFunction.apply(t));
        return functionTask;
    }

    public FunctionTask<T> thenAlsoWait(long delay, TimeUnit timeUnit) {
        delay(delay, timeUnit);

        FunctionTask<T> functionTask = new FunctionTask<>(service);
        callback = functionTask::invoke;
        return functionTask;
    }

    public <R> FunctionTask<R> then(Function<T, R> callbackFunction) {
        delay();

        FunctionTask<R> functionTask = new FunctionTask<>(service);
        callback = t -> functionTask.invoke(callbackFunction.apply(t));
        return functionTask;
    }

    public RunnableTask then(long delay, TimeUnit timeUnit, Consumer<T> callbackFunction) {
        delay(delay, timeUnit);

        RunnableTask callbackTask = new RunnableTask(service);
        callback = t -> {
            callbackFunction.accept(t);
            callbackTask.invoke();
        };
        return callbackTask;
    }

    public RunnableTask thenWait(long delay, TimeUnit timeUnit) {
        delay(delay, timeUnit);

        RunnableTask callbackTask = new RunnableTask(service);
        callback = t -> callbackTask.invoke();
        return callbackTask;
    }

    public RunnableTask then(Consumer<T> callbackFunction) {
        delay();

        RunnableTask callbackTask = new RunnableTask(service);
        callback = t -> {
            callbackFunction.accept(t);
            callbackTask.invoke();
        };
        return callbackTask;
    }

    public void end(long delay, TimeUnit timeUnit, Consumer<T> callbackConsumer) {
        delay(delay, timeUnit);

        // if (delay <= 0) {
        callback = callbackConsumer;
        // } else {
        //     callback = t -> service.schedule(() -> callbackConsumer.accept(t), delay, timeUnit);
        // }
    }

    public void end(Consumer<T> callbackConsumer) {
        delay();

        callback = callbackConsumer;
    }


    private void delay(long delay, TimeUnit timeUnit) {
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    private void delay() {
        this.delay = 0;
        this.timeUnit = TimeUnit.SECONDS;
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
