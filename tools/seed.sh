#!/bin/sh
set -e
PKG=com.ysengoku.ft_hangouts
DB=databases/ft_hangouts.db
SERIAL=${1:-emulator-5554}

adb -s "$SERIAL" shell am force-stop "$PKG"
if ! adb -s "$SERIAL" shell run-as "$PKG" test -f "$DB"; then
  echo "Database not found on $SERIAL. Launch the app once so it creates $DB." >&2
  exit 1
fi

adb -s "$SERIAL" shell run-as "$PKG" sqlite3 -bail "$DB" < "$(dirname "$0")/seed.sql"
echo "Database on $SERIAL is successfully seeded with demo data."
