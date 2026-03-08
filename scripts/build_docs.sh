#!/bin/bash
# Used by Cloudflare Pages to quickly deploy the documentation

# Exit immediately if a command exits with a non-zero status
set -e

echo "Downloading Doxygen..."
wget -qO doxygen.tar.gz https://doxygen.nl/files/doxygen-1.10.0.linux.bin.tar.gz
tar -xzf doxygen.tar.gz

echo "Downloading Graphviz..."
wget -qO graphviz.tar.gz https://gitlab.com/api/v4/projects/4207231/packages/generic/graphviz-releases/9.0.0/graphviz-9.0.0.tar.gz
tar -xzf graphviz.tar.gz

echo "Configuring PATH..."
# Add both downloaded tools to the system PATH so Cloudflare can find them
export PATH=$PWD/doxygen-1.10.0/bin:$PWD/graphviz-9.0.0/bin:$PATH

echo "Generating Documentation..."
doxygen Doxyfile