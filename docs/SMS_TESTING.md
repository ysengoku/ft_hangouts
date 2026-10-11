# SMS Testing

How to test sending and receiving SMS on emulators, and why an SMS cannot go from one emulator to another.

## Prerequistes

Needs two AVDs named `phone_1` and `phone_2` (API 33+). Check: `emulator -list-avds`

- To create with the command-line tools:
  ```sh
  sdkmanager "system-images;android-37;google_apis;x86_64"
  avdmanager create avd -n phone_1 -k "system-images;android-37;google_apis;x86_64" -d medium_phone
  avdmanager create avd -n phone_2 -k "system-images;android-37;google_apis;x86_64" -d medium_phone
  ```

## Setup

Start from the terminal with port: 6520–6527. (Device Manager can't add the option.)

Each emulator uses about 4.5 GB of RAM. Close Android Studio and stop Gradle (`./gradlew --stop`) before starting the second one, or the machine freezes.

```sh
emulator -avd phone_1 -modem-simulator-port 6520 &
 # after phone_1 has booted
emulator -avd phone_2 -modem-simulator-port 6521 &  
./gradlew installDebug
```

| AVD | adb name | Number |
|---|---|---|
| `phone_1` | `emulator-5554` | `6520` / `15555216520` |
| `phone_2` | `emulator-5556` | `6521` / `15555216521` |

- adb name = start order, not the modem port.
- No `+`: `+15555216521` -> `DIAL error 44`.

## Sending

The app hands the SMS to `SmsManager`. The message is added to the conversation when the system accepts it, and an error is shown otherwise.

Run this command, then send an SMS from the app.

```sh
adb -s emulator-5554 logcat -b radio | grep -E "sendTextForSubscriber|SEND_SMS"
```

 When the SMS is accepted, these two lines appear in the log:
```
SmsController: sendTextForSubscriber caller=com.ysengoku.ft_hangouts
RILJ    : [0249]< SEND_SMS { mMessageRef = 2, mErrorCode = 0, ... }
```

`caller` is the app that sent the SMS, and `mErrorCode = 0` means it was accepted.

## Receiving

An incoming SMS can be simulated from the emulator console, with Extended controls > Phone > SMS, or from the terminal:

```sh
adb -s emulator-5554 emu sms send 6521 "Hello"
```

This sends "Hello" to `phone_1`, as if it came from `6521` (`phone_2`). The number after `send` is the sender, so use the number of a saved contact to test receiving from a contact.

The SMS goes through the same path as a real one: emulated radio -> Android -> `SMS_RECEIVED` broadcast -> `SmsReceiver`, which saves it and creates the contact if the number is unknown. The app needs the `RECEIVE_SMS` permission.

## Emulator limitation

An SMS sent from one emulator never arrives on the other emulator. This is a limitation of the Android Emulator, not of the app: sending and receiving each work on their own (see above), and Google Messages fails the same way.

The [Android documentation](https://developer.android.com/studio/run/emulator-networking-voice) says an emulator can send an SMS to another one by using its port number (`5556`) as the address. On this setup, it does not work, even with Google Messages.

This text has not changed since [2009](https://web.archive.org/web/20090213153538/http://developer.android.com/guide/developing/tools/emulator.html). It describes the old emulated modem, which forwarded calls and SMS to the other emulator's console port.

Current system images use the Cuttlefish modem simulator instead. It runs in the emulator program on the host. It can forward calls and SMS to another emulator, but only to a modem port between 6520 and 6527, set with `-modem-simulator-port`. Without this option, the modem port is a random 5-digit number, so nothing is forwarded, and a call to `5556` only looks "active" on the caller.

Verified with `-modem-simulator-port`:
- Calls work in both directions (`6520` <-> `6521`).
- The sender's radio accepts the SMS (`SEND_SMS … mErrorCode = 0`), from the app and from Google Messages, but the sender never connects to the other emulator's modem port.
- The receiver gets nothing, with `6520`, `15555216520` or `+15555216520`, and even when an emulator sends to its own number.
- Sending `REM0` and `AT+REMOTESMS=<pdu>` directly to the receiver's modem port does nothing. The same test with `AT+REMOTECALL` makes it ring.
- The receiver works: SMS sent from its console arrive. `adb emu sms send` skips the SMS code of the modem simulator and sends the "new SMS" notification (`+CMT`) directly.
- The old modem can't be used: with `-feature -ModemSimulator`, the images have no SIM and stay out of service.

Verified without the option: the receiver gets nothing with `5556`, `15555215556` or `+15555215556`, after a cold boot (`-no-snapshot-load`), and even when an emulator sends to its own number. `emulator -debug modem` logs nothing, because this option is for the old modem.

Google's issue tracker has the same problem, reported as feature requests:
- [Issue 265547214](https://issuetracker.google.com/issues/265547214) (January 2023): following the documentation, a call to `5556` stays on the caller and SMS go nowhere (API 32 and 33, x86_64).
- [Issue 354005993](https://issuetracker.google.com/issues/354005993) (July 2024): a request for calls and SMS between emulators.

The app's code is not involved. Google Messages fails the same way.

## Tested on

- Mac (macOS 26.5.2, Apple M4): emulator 36.4.10, 37.1.11 and 37.2.12, with `android-36` and `android-37.0` (`google_apis`, `arm64-v8a`).
- Linux (x86_64): not tested yet. To check: calls between `6520` and `6521`, SMS between the two emulators, `tools/sms-relay.sh`, and `adb emu sms send`.
