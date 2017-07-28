#!/bin/bash

SRC_METADATA_ROOT="https://devfest2017.gdgnantes.com/api/v1/schedule"
DEST_METADATA_ROOT="../app/src/main/res/raw/seed.json"

curl "$SRC_METADATA_ROOT" | jq '.' > "$DEST_METADATA_ROOT"

echo "Need etag is `curl -sI $SRC_METADATA_ROOT | grep "etag" | sed 's/etag: \(.*\)/\1/'`"
