package org.easygroup.easytaxi.domain;

import java.util.concurrent.atomic.AtomicLong;

public class Driver {
    private static final AtomicLong NEXT_UNASSIGNED_ID = new AtomicLong(Long.MAX_VALUE);

    private final long id;
    private boolean available;

    public Driver(boolean available){
        this(NEXT_UNASSIGNED_ID.getAndDecrement(), available);
    }

    public Driver(long id, boolean available) {
        if (id <= 0) {
            throw new IllegalArgumentException("Driver id must be positive");
        }
        this.id = id;
        this.available = available;
    }

    public long getId() {
        return id;
    }

    public boolean isAvailable(){
        return available;
    }
}
