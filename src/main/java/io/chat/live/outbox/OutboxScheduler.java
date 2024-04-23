package io.chat.live.outbox;

import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OutboxScheduler {

    private final OutboxService service;

    private transient boolean active = true;

    @SchedulerLock(name = "outbox-scheduler", lockAtMostFor = "30s")
    @Scheduled(fixedRate = 10L)
    public void send() {
        if (active) {
            service.sendPendingMessages();
        }
    }

    public void pause() {
        active = false;
    }

    public void carryOn() {
        active = true;
    }

}
