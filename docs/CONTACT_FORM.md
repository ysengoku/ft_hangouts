# Contact Form

## Mode 

`ContactFormScreen` is used for 2 modes: adding a new contact and editing an existing one.   
If `contactId` is passed from `Ǹavigator`, the screen considers it in edit mode.

## Avatar image

- **Picker:** `MediaStore.ACTION_PICK_IMAGES`, the system photo picker (API 33+). Built into Android, so no library and no storage permission are needed.
- **Result:** received in `onActivityResult`, forwarded from `MainActivity` through the `Navigator` (see [Navigation](NAVIGATION.md#screens)).
- **Storage:** the image is copied to `filesDir/photos` and the file path is saved in the database. The URI returned by the picker is only readable for a limited time, so it cannot be stored.
- **Display:** `bindAvatar` shows the image, or the initials when there is no picture.

## Phone

## State restoration

Android restores the text of `EditText` views that have an ID. Other values (`picture`, `birthday`) are saved by `saveState(outState: Bundle)` and restored in `init`.

In edit mode, the database load finishes after the restore. When `savedState` is not null, it only sets `original` and does not fill the fields, so the user's edits are kept.
