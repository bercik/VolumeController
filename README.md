# VolumeController
Allows to controll master sound volume on your PC (linux) from your android phone

# prerequisites
- python in version 3
- amixer command utility installed
- superuser priviliges if you want shutdown utility

# How to run server on PC (adding python script to startup (tested on Linux Mint 17 Qiana)):

## Fully working ugly solution
1. Open application menu and search for "startup  applications", run it.
2. Click add button.
3. As command type: `mate-terminal --working-directory=/path/to/directory/with/script --geometry=0x0+0+0 --title="Volume Controller" -e "bash -c 'echo pass | sudo -S python3 ./VolumeController.py;$SHELL'"` where pass is your password.

This will open terminal window on startup so you will see small black box on your screen with Volume Controller title. Don't close it!

## Solution without open and close url utility working
Run VolumeController.py script at startup as superuser. You can do this in any way.

## Solution without shutdown utility working
Seems same as first solution except it doesn't require password.

1. Open application menu and search for "startup  applications", run it.
2. Click add button.
3. As command type: `mate-terminal --working-directory=/path/to/directory/with/script --geometry=0x0+0+0 --title="Volume Controller" -e "bash -c 'python3 ./VolumeController.py;$SHELL'"`

This will open terminal window on startup so you will see small black box on your screen with Volume Controller title. Don't close it!

## Solution with only volume controll utility working
Run VolumeController.py script at startup as any user. You can do this in any way.
