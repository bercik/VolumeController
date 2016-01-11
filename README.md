# VolumeController
Allows to controll master sound volume on your PC (linux or windows) from your android phone

# Add python script to startup (Ubuntu 14.04):
1\. Go to /etc/init folder

2\. create volume-controller.conf file with this content:

> start on runlevel [2345]

> stop on runlevel [!2345]

> chdir /path/to/folder/with/python/script (for example: /home/user/VolumeController/pc/linux)

> exec ./run.sh

3\. That's it. You can start and stop script manually by calling:

> sudo service volume-controller start

> sudo service volume-controller stop
