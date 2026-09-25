#!/bin/sh
set -e
PKG=com.ysengoku.ft_hangouts
DB=databases/ft_hangouts.db

adb shell am force-stop "$PKG"
if ! adb shell run-as "$PKG" test -f "$DB"; then
  echo "Database not found. Launch the app once so it creates $DB." >&2
  exit 1
fi

adb shell run-as "$PKG" sqlite3 "$DB" < "$(dirname "$0")/seed.sql"
echo "Database is successfully seeded with demo data."
