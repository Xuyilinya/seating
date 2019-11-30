package handler;

import com.example.seating.handler.thead.LeaveThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
public class LeaveHandler {

    private static Map<Integer, LeaveThread> threads = new HashMap<>();

    public static void creatLeaveThread(int seatId) {
        log.info("》》》》》》》》收到暂离任务《《《《《《");
        LeaveThread thread = new LeaveThread(seatId);
        threads.put(seatId, thread);
        thread.start();
    }

    public static void removeLeaveThread(int key) {
        log.info(">>>>>>>>>>>>>>>>取消暂离<<<<<<<<<<<<<<<");
        Thread thread = threads.get(key);
        thread.interrupt();
    }

    public static void main(String[] args) throws Exception {
        creatThred("1");
        Thread thread = res.get("1");
        thread.start();
        System.out.println("在50秒之内按任意键中断线程!");
        thread.interrupt();
        System.out.println("线程已经退出!");
    }
    private static Map<String,Thread> res = new HashMap<>();

    public static void creatThred(String key){
        Thread thread = new ThreadInterrupt();
        res.put(key,thread);
    }

    public static class ThreadInterrupt extends Thread {
        @Override
        public void run() {
            try {
                sleep(20000);// 延迟50秒
                System.out.println("线程执行完成！");
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
