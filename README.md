# VolumeController
Allows to controll master sound volume on your PC (linux) from your android phone

# Add python script to startup (tested on Linux Mint 17 Qiana):

## Fully working ugly solution
1. Open application menu and search for "startup  applications", run it.
2. Click add button.
3. As command type: `mate-terminal --working-directory=/path/to/directory/with/script --geometry=0x0+0+0 --title="Volume Controller" -e "bash -c 'echo pass | sudo -S python3 ./VolumeController.py;$SHELL'"` where pass is your password.

## Solution without open and close url utility working
Run VolumeController.py script at startup as superuser. You can do this in any way.

## Solution without shutdown utility working
Seems same as first solution except it doesn't require password.

1. Open application menu and search for "startup  applications", run it.
2. Click add button.
3. As command type: `mate-terminal --working-directory=/path/to/directory/with/script --geometry=0x0+0+0 --title="Volume Controller" -e "bash -c 'python3 ./VolumeController.py;$SHELL'"`

## Solution with only volume controll utility working
Run VolumeController.py script at startup as any user. You can do this in any way.
