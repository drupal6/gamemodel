package com.gl.script;

import java.time.LocalTime;

//心跳脚本
public interface ITimerEventScript extends IScript{

    default void secondHandler(LocalTime localTime) {

    }

    default void minuteHandler(LocalTime localTime) {

    }

    default void hourHandler(LocalTime localTime) {

    }

    default void dayHandler(LocalTime localTime) {

    }
}
