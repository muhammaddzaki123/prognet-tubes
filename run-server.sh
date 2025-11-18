#!/bin/bash

echo "=== Memory Game Server ==="
echo ""
echo "Starting server on port 5000..."
echo "UDP broadcast on port 5001"
echo ""

# Get local IP addresses
echo "Server IP addresses:"
if [[ "$OSTYPE" == "darwin"* ]]; then
    # macOS
    ifconfig | grep "inet " | grep -v 127.0.0.1 | awk '{print "  - " $2}'
else
    # Linux
    hostname -I | tr ' ' '\n' | grep -v "^$" | awk '{print "  - " $1}'
fi

echo ""
echo "Clients can connect using above IP addresses"
echo "Press Ctrl+C to stop server"
echo "========================================"
echo ""

java -cp "bin:lib/*" Main server
