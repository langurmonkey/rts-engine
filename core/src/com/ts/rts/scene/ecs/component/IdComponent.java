package com.ts.rts.scene.ecs.component;

import com.badlogic.ashley.core.Component;

/**
 * Contains the unique identifier
 */
public class IdComponent implements Component {
    private static long uniqueIdSeq = 0;
    public long uniqueId;
}
