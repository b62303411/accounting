#!/bin/bash

# Variables
DATE=$(date +"%Y-%m-%d_%H-%M")
BACKUP_DIR="backup"
DB_CONTAINER_NAME="db" # Name of your PostgreSQL container

# Create backup
docker exec $DB_CONTAINER_NAME pg_dump -U odoo -F c agile > $BACKUP_DIR/db_backup_$DATE.sql

# Keep only the 7 most recent backups
find $BACKUP_DIR -type f -name '*.sql' | sort -r | tail -n +8 | xargs rm -f

echo "Backup for date $DATE completed!"
