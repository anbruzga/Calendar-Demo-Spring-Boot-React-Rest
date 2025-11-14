package lt.calendar.reminders.util;

public class MyStopWatch extends org.springframework.util.StopWatch {

    public MyStopWatch() {
        super();
        this.start();
    }

    public long stopAndGetMillis() {
        super.stop();
        return super.getTotalTimeMillis();
    }

    @Override
    @Deprecated
    public long getTotalTimeMillis() {
        return super.getTotalTimeMillis();
    }
}
