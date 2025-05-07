#!/bin/bash

# Set the path to the acme.json file
ACME_PATH="./letsencrypt/acme.json"

# Check if the ACME configuration file exists
if [[ ! -f $ACME_PATH ]]; then
    echo "Warning: $ACME_PATH does not exist."
else
    # Check permissions of acme.json
    if [[ ! -w $ACME_PATH ]]; then
        echo "Warning: $ACME_PATH is not writable."
    fi
    if [[ $(stat -c %a $ACME_PATH) -ne "600" ]]; then
        echo "Warning: It's recommended to set permissions of $ACME_PATH to 600 for security reasons."
    fi
fi

# Check if docker is running
if ! docker info &>/dev/null; then
    echo "Error: Docker is not running or current user doesn't have permission to access Docker daemon."
    exit 1
fi

# Check if traefik-public network exists
if ! docker network ls | grep -q "traefik-public"; then
    echo "Warning: traefik-public network doesn't exist."
fi

# Check if the Traefik image exists locally
if ! docker images | grep -q "traefik"; then
    echo "Warning: Traefik image is not available locally. It will be pulled from the Docker Hub during the first run."
fi

# Print a message for things that can't be checked with the script
echo "Please note:"
echo "- Ensure that the email provided"


