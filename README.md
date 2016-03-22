# VolumeController
Allows to controll master sound volume on your PC (linux) from your android phone or another PC (linux and windows), open and close websites in google chrome on your PC, shutdown computer and view songs played on polskastacja web radio.

# prerequisites
## pc server
- python in version 3
- amixer command utility installed
- superuser priviliges if you want shutdown utility
- google chrome if you want open and close websites utility. You will also need to create empty directory /home/chrome-user (this can be owned by root).
## pc client
- python3
- tkinter python3 module
## android client
- minimum version ... of android

# How to run server on PC (adding python script to startup (tested on Linux Mint 17 Qiana and Ubuntu 14.04 (without open and close url utility))):

You need to run server/pc/linux/VolumeController.py script if you want this application to work. You can do this manually every time you need this or add it to startup so it will run automatically every time you start your computer. You can set it on startup in various ways which i listed below.

## Fully working ugly solution on linux mint
1. Open application menu and search for "startup  applications", run it.
2. Click add button.
3. As command type: `mate-terminal --working-directory=/path/to/directory/with/script --geometry=0x0+0+0 --title="Volume Controller" -e "bash -c 'echo pass | sudo -S python3 ./VolumeController.py;$SHELL'"` where pass is your password.

This will open terminal window on startup so you will see small black box on your screen with Volume Controller title. Don't close it!

## Solution without open and close url utility working
Run VolumeController.py script at startup as superuser. You can do this in any way. For example you can create upstart job like this:

1\. Go to /etc/init folder

2\. As a superuser create and edit file volume-controller.conf

3\. Paste into it:
```
# When to start the service
start on runlevel [2345]

# When to stop the service
stop on runlevel [016]

# Automatically restart process if crashed
respawn

# Specify working directory
chdir /path/to/folder/VolumeController/server/pc/linux

# Specify the process/command to start, e.g.
exec ./run.sh
```
4\. Now you can start and stop job manually and it will start automatically at system startup.

## Solution without shutdown utility working
Seems same as first solution except it doesn't require password.

1. Open application menu and search for "startup  applications", run it.
2. Click add button.
3. As command type: `mate-terminal --working-directory=/path/to/directory/with/script --geometry=0x0+0+0 --title="Volume Controller" -e "bash -c 'python3 ./VolumeController.py;$SHELL'"`

This will open terminal window on startup so you will see small black box on your screen with Volume Controller title. Don't close it!

## Solution with only volume controll utility working
Run VolumeController.py script at startup as any user. You can do this in any way.

# Installing android application
For now you can just open whole android project in android studio, attach your phone via USB cable and run application.

# Connecting client to server
The last thing you need to do is set good ip address of your server on client. To check your ip address on linux server open terminal and type in:
`ifconfig`
Then search for eth0 and inet address is your local ip address. Now in client (android and PC) go to settings and change ip address to this.

If you have got some issues with application feel free to write me an email.
