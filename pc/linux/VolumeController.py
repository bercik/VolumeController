import time
import subprocess
import socket
import datetime
import sys
import logging

class VolumeController:
    def setVolume(self, newVolume):
        p = subprocess.Popen(['amixer', '--quiet', 'set', 'Master', str(newVolume) + "%"], 
                                stderr=subprocess.PIPE)

        err = p.communicate()

    def getVolume(self):
        p = subprocess.Popen(['./get_volume.sh'], 
                                stdout=subprocess.PIPE, 
                                stderr=subprocess.PIPE)

        out, err = p.communicate()

        return int(out[:-2])

class BrowserController:
    def __init__(self):
        self.proc = None

    def openURL(self, url):
        if self.proc != None:
            self.closeURL()

        logging.debug('open url: ' + url)
        self.proc = subprocess.Popen(['google-chrome',  '--user-data-dir=/home/chrome-user', url])
        # self.proc = subprocess.Popen(['firefox',  '-new-window', url])

    def closeURL(self):
        if self.proc != None:
            logging.debug('close url: ')
            self.proc.terminate()
            self.proc = None

class Server:
    SET_VOLUME = "SET_VOL"
    GET_VOLUME = "GET_VOL"
    OPEN_URL = "OPEN_URL"
    CLOSE_URL = "CLOSE_URL"

    def __init__(self, port):
        self.port = port
        self.vc = VolumeController()
        self.bc = BrowserController()

        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.bind(('', self.port))

    def accept(self):
        self.socket.listen(5)
        (clientSocket, addr) = self.socket.accept()
        try:
            recived = clientSocket.recv(1024)
            recived = recived.decode(encoding='UTF-8')
            recived = recived[:-1] # remove \n character from end

            if recived.startswith(self.GET_VOLUME):
                vol = str(self.vc.getVolume()) + '\n'
                clientSocket.send(vol.encode(encoding='UTF-8'))
            elif recived.startswith(self.SET_VOLUME):
                vol = int(recived.split(' ')[1])
                self.vc.setVolume(vol)
            elif recived.startswith(self.OPEN_URL):
                url = recived.split(' ')[1]
                self.bc.openURL(url)
            elif recived.startswith(self.CLOSE_URL):
                self.bc.closeURL()

        except Exception as ex:
            logging.error(ex)

    def close(self):
        self.socket.close()

if __name__ == '__main__':

    LOG_FILENAME = '/home/robert/volume-controller.log'
    FORMAT = '%(asctime)-15s %(levelname)s: %(message)s'
    logging.basicConfig(filename=LOG_FILENAME, level=logging.DEBUG)

    logging.debug('--DEBUG--')
    logging.debug(datetime.datetime.now())

    server = None

    try:
        while True:
            try:
                server = Server(5656)

                while True:
                    server.accept()
            except Exception as ex:
                logging.error(ex)
                if server != None:
                    server.close()

                time.sleep(1)
    except Exception as ex:
        logging.error(ex)
        if server != None:
            server.close()
