package net.zimi.client;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class Mineshark implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Mineshark");

    public static long PACKET_DELAY_MS = 400;
    public static boolean FILTER_MOVEMENT = true;
    public static boolean BYPASS_KEEP_ALIVE = true;

    public static final AtomicInteger IN_COUNTER = new AtomicInteger(0);
    public static final AtomicInteger OUT_COUNTER = new AtomicInteger(0);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Mineshark initialized. Delay: {}ms, Filter Movement: {}", PACKET_DELAY_MS, FILTER_MOVEMENT);
    }
}