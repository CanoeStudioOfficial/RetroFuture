package com.canoestudio.retrofuturethewildupdate.block;

public enum SculkVibration {
    STEP(1),
    ENTITY_INTERACT(6),
    ENTITY_DAMAGE(7),
    BLOCK_ACTIVATE(10),
    BLOCK_DESTROY(12),
    BLOCK_PLACE(13),
    ENTITY_DIE(15);

    private final int frequency;

    SculkVibration(int frequency) {
        this.frequency = frequency;
    }

    public int getFrequency() {
        return this.frequency;
    }
}
