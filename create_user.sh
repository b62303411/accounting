#!/bin/bash

# Parse application.properties for database connection details
DATABASE_URL=$(grep 'spring.datasource.url' application.properties | cut -d'=' -f2)
DATABASE_NAME=$(echo $DATABASE_URL | awk -F"/" '{print $NF}' | awk -F"?" '{print $1}') # Extracting the DB name, while accounting for potential URL parameters
DATABASE_USER=$(grep 'spring.datasource.username' application.properties | cut -d'=' -f2)
DATABASE_PASSWORD=$(grep 'spring.datasource.password' application.properties | cut -d'=' -f2)

# User details for insertion into app_user table
NEW_USER_USERNAME=$1
NEW_USER_PASSWORD=$2

if [[ -z $NEW_USER_USERNAME || -z $NEW_USER_PASSWORD ]]; then
    echo "Usage: $0 <username> <password>"
    exit 1
fi

# Assume your PostgreSQL is running in a Docker container
CONTAINER_NAME=YOUR_POSTGRES_CONTAINER_NAME

# Create a temporary SQL script
TMP_SQL_FILE="/tmp/insert_user_$$.sql"
echo "INSERT INTO app_user(username, password, enabled) VALUES ('$NEW_USER_USERNAME', '$NEW_USER_PASSWORD', true);" > $TMP_SQL_FILE

# Copy the SQL script to the PostgreSQL container
docker cp $TMP_SQL_FILE $CONTAINER_NAME:$TMP_SQL_FILE

# Execute the SQL script in the PostgreSQL container
docker exec $CONTAINER_NAME psql -U $DATABASE_USER -d $DATABASE_NAME -a -f $TMP_SQL_FILE

# Optionally, remove the temporary SQL file
rm $TMP_SQL_FILE
