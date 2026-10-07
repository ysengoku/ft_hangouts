#!/bin/sh
set -e

wait_for_boot() {
    adb -s "$1" wait-for-device
    until [ "$(adb -s "$1" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')" = "1" ]; do
        sleep 2
    done
}

emulator -avd phone_1 > /dev/null 2>&1 &
wait_for_boot emulator-5554

emulator -avd phone_2 > /dev/null 2>&1 &
wait_for_boot emulator-5556

./gradlew installDebug
