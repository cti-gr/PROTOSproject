#!/bin/bash

# bash script for automating the .deb file generation process.
# Last updated: 2016-01-11

# Example call (as root): ./make_deb_and_targz.sh DIRECTORY
# ...where DIRECTORY contains the source files


# We need root privileges to run the script
if [ $(id -u) -ne 0 ]; then
    echo 'Error: You need to be root to run this script'
    exit 1
fi

# Ensure script was called with correct number of arguments
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 DIRECTORY" >&2
    exit 1
fi
if ! [ -e "$1" ]; then
    echo "Error: $1 not found" >&2
    exit 1
fi
if ! [ -d "$1" ]; then
    echo "Error: $1 not a directory" >&2
    exit 1
fi

# Trim any trailing slashes...
src_dir=$(echo $1 | sed 's:/*$::')

# Delete any backup files (*~, *.bak)
echo "0) Deleting any backup files (*~, *.bak) in $src_dir..."
find $src_dir -name '*~' -print -exec rm -f {} \;
find $src_dir -name '*.bak' -print -exec rm -f {} \;

echo -e "\n1) Calculating value for Installed-Size in file:"
echo "$src_dir/DEBIAN/control"
# Calculation performed according to:
# https://www.debian.org/doc/debian-policy/ch-controlfields.html#s-f-Installed-Size

du_size=$(du -sb --exclude DEBIAN $src_dir | sed -r -e "s;\s\s*; ;g" -e "s; ^ *;;" | cut -d' ' -f1)
float_size=$(echo "$du_size" / 1024 | bc -l)
installed_size=$(echo "($float_size+0.5)/1" | bc)
echo "Installed-Size: $installed_size"

echo -e "\n2) Setting permissions and ownership..."
chmod -R 755 $src_dir/DEBIAN
chown -R root.root $src_dir

echo -e "\n3) Generating .deb file..."
dpkg-deb -b $src_dir

echo -e "\n4) Generating .tar.gz file..."
tar cf $src_dir.tar $src_dir
gzip $src_dir.tar
