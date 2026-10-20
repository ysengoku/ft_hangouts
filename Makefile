PKG := com.ysengoku.ft_hangouts
DB := databases/ft_hangouts.db
PHONE_1_AVD=phone_1
PHONE_1_SERIAL=emulator-5554
PHONE_2_AVD=phone_2
PHONE_2_SERIAL=emulator-5556

.PHONY: all emulator-1 emulator-2 run seed-1 seed-2 logs-1 logs-2 logs-1-sms logs-2-sms clean-db-1 clean-db-2 stop uninstall-1 uninstall-2 clean

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

clean-db-1:
	adb -s $(PHONE_1_SERIAL) shell am force-stop $(PKG)
	adb -s $(PHONE_1_SERIAL) shell run-as $(PKG) rm -f $(DB)

clean-db-2:
	adb -s $(PHONE_2_SERIAL) shell am force-stop $(PKG)
	adb -s $(PHONE_2_SERIAL) shell run-as $(PKG) rm -f $(DB)

# Free Gradle's memory
stop:
	./gradlew --stop

# Removes the app, its data and granted permissions.
uninstall-1:
	@if adb -s $(PHONE_1_SERIAL) shell pm list packages $(PKG) | grep -q $(PKG); then \
		echo "Uninstalling $(PKG) from $(PHONE_1_SERIAL)"; \
		adb -s $(PHONE_1_SERIAL) uninstall $(PKG); \
	else \
		echo "$(PKG) is not installed on $(PHONE_1_SERIAL)"; \
	fi

uninstall-2:
	@if adb -s $(PHONE_2_SERIAL) shell pm list packages $(PKG) | grep -q $(PKG); then \
		echo "Uninstalling $(PKG) from $(PHONE_2_SERIAL)"; \
		adb -s $(PHONE_2_SERIAL) uninstall $(PKG); \
	else \
		echo "$(PKG) is not installed on $(PHONE_2_SERIAL)"; \
	fi

clean: uninstall-1 uninstall-2

stop-emulators:
	@for serial in $(PHONE_1_SERIAL) $(PHONE_2_SERIAL); do \
		if adb devices | grep -q "^$$serial[[:space:]]"; then \
			adb -s $$serial emu kill; \
		else \
			echo "$$serial is not running"; \
		fi; \
	done
