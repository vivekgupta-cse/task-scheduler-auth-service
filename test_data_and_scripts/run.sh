#!/bin/bash -x
URL='http://localhost:8081'
HEADER='Content-Type: application/json'

# Source external files containing user and task data
source Users.sh
source Tasks.sh

# --- 1. Register Users ---
echo "--- Registering Users ---"
for (( i=0; i<${#USERS[@]}; i+=2 )); do
    USERNAME=${USERS[i]}
    PASSWORD=${USERS[i+1]}

    echo "Registering user: $USERNAME"
    # Use a "here document" for cleaner JSON
    curl --location "${URL}/api/auth/register" --header "${HEADER}" --data @- <<EOF
{
  "username": "$USERNAME",
  "password": "$PASSWORD"
}
EOF
    echo ""
done

# --- 2. Login and Capture Token ---
echo "--- Logging in as 'vivek' to get token ---"
# Use -s for silent mode to ensure only the token is captured
TOKEN=$(curl -s --location "${URL}/api/auth/login" --header "${HEADER}" --data @- <<EOF
{
  "username": "vivek",
  "password": "vivek123"
}
EOF
)

if [ -z "$TOKEN" ]; then
    echo "Error: Failed to get token. Exiting."
    exit 1
fi
echo "Token captured successfully."
echo ""