package handler;

import com.example.seating.handler.thead.LeaveThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class LeaveHandler {

    private static Map<Integer, LeaveThread> threads = new HashMap<>();

    public static void creatLeaveThread(int orderId) {
        log.info("》》》》》》》》收到暂离任务《《《《《《");
        LeaveThread thread = new LeaveThread(orderId);
        threads.put(orderId, thread);
        thread.start();
    }

    public static void removeLeaveThread(int key) {
        log.info(">>>>>>>>>>>>>>>>取消暂离<<<<<<<<<<<<<<<");
        Thread thread = threads.get(key);
        thread.interrupt();
    }
}
