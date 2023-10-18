#!/bin/bash

# The path to the application.properties file should be provided as the first argument
PROPERTIES_PATH=$1
NEW_USER_USERNAME=$2
NEW_USER_PASSWORD=$3
CONTAINER_NAME=$4

if [[ -z $PROPERTIES_PATH || -z $NEW_USER_USERNAME || -z $NEW_USER_PASSWORD ]]; then
    echo "Usage: $0 <path_to_application.properties> <username> <password>"
    exit 1
fi

# Parse application.properties for database connection details
DATABASE_URL=$(grep 'spring.datasource.url' $PROPERTIES_PATH | cut -d'=' -f2)
DATABASE_NAME=$(echo $DATABASE_URL | awk -F"/" '{print $NF}' | awk -F"?" '{print $1}') # Extracting the DB name, while accounting for potential URL parameters
DATABASE_USER=$(grep 'spring.datasource.username' $PROPERTIES_PATH | cut -d'=' -f2)
DATABASE_PASSWORD=$(grep 'spring.datasource.password' $PROPERTIES_PATH | cut -d'=' -f2)

# Create a temporary SQL script
TMP_SQL_FILE="/tmp/insert_user_$$.sql"
echo "INSERT INTO app_user(username, password, enabled) VALUES ('$NEW_USER_USERNAME', '$NEW_USER_PASSWORD', true);" > $TMP_SQL_FILE

# Copy the SQL script to the PostgreSQL container
docker cp $TMP_SQL_FILE $CONTAINER_NAME:$TMP_SQL_FILE

# Execute the SQL script in the PostgreSQL container
docker exec $CONTAINER_NAME psql -U $DATABASE_USER -d $DATABASE_NAME -a -f $TMP_SQL_FILE

# Optionally, remove the temporary SQL file
rm $TMP_SQL_FILE
