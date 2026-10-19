PKG := com.ysengoku.ft_hangouts
PHONE_1_AVD=phone_1
PHONE_1_SERIAL=emulator-5554
PHONE_2_AVD=phone_2
PHONE_2_SERIAL=emulator-5556

.PHONY: all emulator-1 emulator-2 run seed-1 seed-2 logs-1 logs-2 logs-1-sms logs-2-sms stop uninstall-1 uninstall-2 clean

all:
	./tools/emulators.sh

emulator-1:
	./tools/emulators.sh $(PHONE_1_AVD)

emulator-2:
	./tools/emulators.sh $(PHONE_2_AVD)

run:
	./gradlew installDebug
	adb -s emulator-5554 shell am start -n $(PKG)/.MainActivity

seed-1:
	./tools/seed.sh $(PHONE_1_SERIAL)

seed-2:
	./tools/seed.sh $(PHONE_2_SERIAL)

logs-1:
	adb -s $(PHONE_1_SERIAL) logcat --pid=$$(adb -s $(PHONE_1_SERIAL) shell pidof $(PKG))

logs-2:
	adb -s $(PHONE_2_SERIAL) logcat --pid=$$(adb -s $(PHONE_2_SERIAL) shell pidof $(PKG))

logs-1-sms:
	adb -s $(PHONE_1_SERIAL) logcat -b radio | grep -E "sendTextForSubscriber|SEND_SMS"

logs-2-sms:
	adb -s $(PHONE_2_SERIAL) logcat -b radio | grep -E "sendTextForSubscriber|SEND_SMS"

# Free Gradle's memory
stop:
	./gradlew --stop

# Removes the app, its data and granted permissions.
uninstall-1:
	adb -s $(PHONE_1_SERIAL) uninstall com.ysengoku.ft_hangouts

uninstall-2:
	adb -s $(PHONE_1_SERIAL) uninstall com.ysengoku.ft_hangouts

clean: uninstall-1 uninstall-2
