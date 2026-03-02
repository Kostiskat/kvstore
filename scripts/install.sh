#!/bin/bash

set -e

echo "starting kvstore installation..."

if [ "$EUID" -ne 0 ]; then
  echo "error: please run this script with sudo."
  exit 1
fi

if ! command -v java &> /dev/null; then
  echo "error: java is not installed. please install java 21+ and try again."
  exit 1
fi

echo "creating directories in /opt/kvstore/"
mkdir -p /opt/kvstore/conf
mkdir -p /opt/kvstore/data

echo "copying files..."
cp server/target/kvstore-server.jar /opt/kvstore/kvstore.jar
cp conf/kvstore.conf /opt/kvstore/conf/kvstore.conf
cat <<EOF > /etc/systemd/system/kvstore.service
[Unit]
Description=kvstore High-Performance Database
After=network.target

[Service]
WorkingDirectory=/opt/kvstore
ExecStart=/usr/bin/java -jar kvstore.jar
Restart=on-failure
RestartSec=3

[Install]
WantedBy=multi-user.target
EOF

echo "starting the database..."
systemctl daemon-reload
systemctl enable kvstore
systemctl start kvstore

echo "kvstore installed and running successfully!"
echo "you can check the status anytime with: sudo systemctl status kvstore"