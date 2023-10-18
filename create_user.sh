#!/bin/bash

# Script to add a user to the app_user table in the PostgreSQL database

# Ensure three arguments are passed
if [ "$#" -ne 3 ]; then
    echo "Usage: $0 <container_name> <username> <password>"
    exit 1
fi

CONTAINER_NAME=$1
USERNAME=$2
PASSWORD=$3

# Create an SQL script with the parameters
cat > insert_user.sql <<EOF
INSERT INTO app_user(username, password, enabled)
VALUES('${USERNAME}', '${PASSWORD}', true);
EOF

# Copy SQL script to PostgreSQL container
docker cp insert_user.sql $CONTAINER_NAME:/tmp/insert_user.sql

# Execute SQL script in the PostgreSQL container
docker exec $CONTAINER_NAME psql -U postgres -a -f /tmp/insert_user.sql

# Cleanup local SQL script
rm insert_user.sql

echo "User ${USERNAME} added successfully!"
