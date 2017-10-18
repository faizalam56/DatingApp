package com.across.senzec.datingapp.bus.events;

/**
 * Created by power hashing on 4/24/2017.
 */

public class TopCardMovedEvent {

    // region Fields
    private final float posX;
    // endregion

    // region Constructors
    public TopCardMovedEvent(float posX) {
        this.posX = posX;
    }
    // endregion

    // region Getters
    public float getPosX() {
        return posX;
    }
    // endregion
}
