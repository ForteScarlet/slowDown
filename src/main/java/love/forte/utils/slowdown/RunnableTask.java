package love.forte.utils.slowdown;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author ForteScarlet
 */
public class RunnableTask {

    private final ScheduledExecutorService service;

    private long delay;
    private TimeUnit timeUnit;

    private Runnable callback;


    // public RunnableTask(
    //         ScheduledExecutorService service,
    //         long delay,
    //         TimeUnit timeUnit
    // ) {
    //     this.service = service;
    //     // this.task = task;
    //     this.delay = delay;
    //     this.timeUnit = timeUnit;
    // }

    public RunnableTask(ScheduledExecutorService service) {
        this.service = service;
        // this.task = task;
        this.delay = 0;
        this.timeUnit = TimeUnit.MILLISECONDS;
    }

    public <R> FunctionTask<R> then(long delay, TimeUnit timeUnit, Supplier<R> callbackSupplier) {
        delay(delay, timeUnit);

        FunctionTask<R> functionTask = new FunctionTask<>(service);
        callback = () -> functionTask.invoke(callbackSupplier.get());
        return functionTask;
    }

    public RunnableTask thenWait(long delay, TimeUnit timeUnit) {
        delay(delay, timeUnit);

        RunnableTask callbackTask = new RunnableTask(service);
        callback = callbackTask::invoke;
        return callbackTask;
    }

    public <R> FunctionTask<R> then(Supplier<R> callbackSupplier) {
        delay();

        FunctionTask<R> functionTask = new FunctionTask<>(service);
        callback = () -> functionTask.invoke(callbackSupplier.get());
        return functionTask;
    }

    public RunnableTask then(long delay, TimeUnit timeUnit, Runnable callbackFunction) {
        delay(delay, timeUnit);

        RunnableTask callbackTask = new RunnableTask(service);
        callback = () -> {
            callbackFunction.run();
            callbackTask.invoke();
        };
        return callbackTask;
        // RunnableTask callbackTask = new RunnableTask(
        //         service, delay, timeUnit
        // );
        // callback = () -> {
        //     callbackFunction.run();
        //     callbackTask.invoke();
        // };
        // return callbackTask;
    }

    public RunnableTask then(Runnable callbackFunction) {
        delay();

        RunnableTask callbackTask = new RunnableTask(service);
        callback = () -> {
            callbackFunction.run();
            callbackTask.invoke();
        };
        return callbackTask;
    }

    public void end(long delay, TimeUnit timeUnit, Runnable callbackRunnable) {
        delay(delay, timeUnit);

        // if (delay <= 0) {
        callback = callbackRunnable;
        // } else {
        //     callback = () -> service.schedule(callbackRunnable, 0, TimeUnit.MILLISECONDS);
        // }
    }

    public void end(Runnable callbackRunnable) {
        delay();
        callback = callbackRunnable;
    }

    private void delay(long delay, TimeUnit timeUnit) {
        this.delay = delay;
        this.timeUnit = timeUnit;
    }

    private void delay() {
        this.delay = 0;
        this.timeUnit = TimeUnit.SECONDS;
    }

    public void invoke() {
        if (delay <= 0) {
            if (callback != null) {
                callback.run();
            }
        } else {
            service.schedule(() -> {
                if (callback != null) {
                    callback.run();
                }
            }, delay, timeUnit);
        }
    }
}
