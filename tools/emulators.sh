#!/bin/sh
set -e

PHONE_1_AVD=phone_1
PHONE_1_PORT=5554
PHONE_1_MODEM_PORT=6520
PHONE_2_AVD=phone_2
PHONE_2_PORT=5556
PHONE_2_MODEM_PORT=6521

wait_for_boot() {
    adb -s "$1" wait-for-device
    until [ "$(adb -s "$1" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]; do
        sleep 2
    done
}

emulator -avd "$PHONE_1_AVD" -port "$PHONE_1_PORT" -modem-simulator-port "$PHONE_1_MODEM_PORT" > /dev/null 2>&1 &
wait_for_boot "emulator-$PHONE_1_PORT"

emulator -avd "$PHONE_2_AVD" -port "$PHONE_2_PORT" -modem-simulator-port "$PHONE_2_MODEM_PORT" > /dev/null 2>&1 &
wait_for_boot "emulator-$PHONE_2_PORT"

./gradlew installDebug
