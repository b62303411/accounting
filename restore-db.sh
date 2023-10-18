#!/bin/bash

# Variables
BACKUP_FILE="$1" # The first argument is the backup file path
DB_CONTAINER_NAME="db" # Name of your PostgreSQL container

if [[ -z "$BACKUP_FILE" ]]; then
    echo "You need to specify the backup file to restore from!"
    exit 1
fi

# Restore from the backup
cat $BACKUP_FILE | docker exec -i $DB_CONTAINER_NAME pg_restore -U [YOUR_DB_USER] -d [YOUR_DB_NAME] --clean --no-owner --role=[YOUR_DB_USER]

echo "Restore from $BACKUP_FILE completed!"
