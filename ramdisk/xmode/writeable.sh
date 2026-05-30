#!/system/bin/sh
busybox mkdir -p /data/mode;
echo mode>/data/mode/rw;
busybox insmod /aufs.ko;
busybox mkdir -p /data/sysrw;
busybox mount -t aufs -o br:/data/sysrw:/system=ro none /system;
echo system now writeable.
