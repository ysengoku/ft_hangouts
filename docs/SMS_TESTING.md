# SMS Testing

How to test sending and receiving SMS on emulators, and why an SMS cannot go from one emulator to another.

## Setup

Two AVDs, `phone_1` and `phone_2`, on the `system-images;android-36;google_apis;x86_64` image (emulator 37.2.12).

| AVD | adb name | Phone number |
|---|---|---|
| `phone_1` | `emulator-5554` | `+15555215554` |
| `phone_2` | `emulator-5556` | `+15555215556` |

Each emulator uses about 4.5 GB of RAM. Close Android Studio and stop Gradle (`./gradlew --stop`) before starting the second one, or the machine freezes.

```sh
emulator -avd phone_1 &
emulator -avd phone_2 &      # after phone_1 has booted
./gradlew installDebug       # installs on both
```

With two emulators running, every `adb` command needs `-s emulator-5554` or `-s emulator-5556`.

## Sending

The app hands the SMS to `SmsManager`. The message is added to the conversation when the system accepts it, and an error is shown otherwise.

The radio log shows the result:

```sh
adb -s emulator-5554 logcat -b radio | grep -E "sendTextForSubscriber|SEND_SMS"
```

```
SmsController: sendTextForSubscriber caller=com.ysengoku.ft_hangouts
RILJ    : [0249]< SEND_SMS { mMessageRef = 2, mErrorCode = 0, ... }
```

`caller` is the app that sent the SMS, and `mErrorCode = 0` means it was accepted.

## Receiving

An incoming SMS can be simulated from the emulator console, with Extended controls > Phone > SMS, or from the terminal:

```sh
adb -s emulator-5556 emu sms send 5554 "Hello"
```

The SMS goes through the same path as a real one: emulated radio → Android → `SMS_RECEIVED` broadcast → `SmsReceiver`, which saves it and creates the contact if the number is unknown. The app needs the `RECEIVE_SMS` permission.

## Why SMS between two emulators does not work

The [Android documentation](https://developer.android.com/studio/run/emulator-networking-voice) says an emulator can send an SMS to another one by using its port number (`5556`) as the address. On this setup, it does not work, even with Google Messages.

Verified:
- The sender's radio accepts the SMS (`SEND_SMS … mErrorCode = 0`), from the app and from Google Messages.
- The receiver gets nothing, with `5556`, `15555215556` or `+15555215556`, after a cold boot (`-no-snapshot-load`), and even when an emulator sends to its own number.
- The receiver works: SMS sent from its console arrive.
- With `emulator -debug modem`, the emulator program logs nothing when an SMS is sent.

Likely explanation (not confirmed): the documentation describes the old emulated modem, which was part of the emulator program and forwarded SMS to the other emulator's console port. Since API 31, system images use a modem simulator that runs inside the emulated phone. The SMS never reaches the emulator program, as the `-debug modem` test shows, so the forwarding is never used. Google's issue tracker also has reports of problems with this modem simulator.

The app's code is not involved. Google Messages fails the same way.
