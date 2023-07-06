package ee.kemit.aks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class SchedulingService {

    private final XRoadService xRoadService;

    // 04:00
    @Scheduled(cron = "0 0 4 * * *")
    public void runAdsSyncCron() {
        log.info("Starting to find possible address updates");
        xRoadService.checkUpdatedAddresses();
        log.info("Finished syncing address updated with ADS");
    }
}

