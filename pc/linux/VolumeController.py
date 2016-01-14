import time
import subprocess
import socket
import datetime
import sys
import logging
import os
import math

CHROME_USER_DIR = "/home/chrome-user"

class VolumeController:

    def __init__(self):
        self.generateList()

    def setVolume(self, newVolume):
        newVolume = self.compToAmixerList[newVolume]
        p = subprocess.Popen(['amixer', '--quiet', 'set', 'Master', str(newVolume) + "%"], 
                                stderr=subprocess.PIPE)

        err = p.communicate()

    def getVolume(self):
        p = subprocess.Popen(['./get_volume.sh'], 
                                stdout=subprocess.PIPE, 
                                stderr=subprocess.PIPE)

        out, err = p.communicate()

        vol = int(out[:-2])
        vol = int(self.amixerToComputer(vol) + 0.5)

        return vol

    def generateList(self):
        self.compToAmixerList = []

        for i in range(0,101,1):
            self.compToAmixerList.append( \
                    int(self.computerToAmixer(i) + 0.5))

    def computerToAmixer(self, x):
        if x > 10.0:
            return 41.2412 * math.log(0.112528 * x)
        else:
            return 9.0
    
    def amixerToComputer(self, x):
        return 8.96 * math.exp(0.0241735 * x)

class BrowserController:
    def __init__(self):
        self.proc = None

    def openURL(self, url):
        # delete Lock if exists
        lockFile = CHROME_USER_DIR + '/SingletonLock'
        try:
            os.remove(lockFile)
        except Exception as ex:
            pass

        if self.proc != None:
            self.closeURL()

        self.proc = subprocess.Popen(['google-chrome',  
            '--user-data-dir=' + CHROME_USER_DIR, url])
        # self.proc = subprocess.Popen(['firefox',  '-new-window', url])

    def closeURL(self):
        if self.proc != None:
            self.proc.terminate()
            self.proc = None

class ComputerController:
    def shutdown(self):
        p = subprocess.Popen(['./shutdown.sh'])

class Server:
    SET_VOLUME = "SET_VOL"
    GET_VOLUME = "GET_VOL"
    OPEN_URL = "OPEN_URL"
    CLOSE_URL = "CLOSE_URL"
    SHUTDOWN = "SHUTDOWN"

    def __init__(self, port):
        self.port = port
        self.vc = VolumeController()
        self.bc = BrowserController()
        self.cc = ComputerController()

        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.bind(('', self.port))

    def accept(self):
        self.socket.listen(5)
        (clientSocket, addr) = self.socket.accept()
        try:
            recived = clientSocket.recv(1024)
            recived = recived.decode(encoding='UTF-8')
            logging.info('recived: ' + recived)
            recived = recived[:-1] # remove \n character from end

            if recived.startswith(self.GET_VOLUME):
                vol = "-1\n"
                try:
                    vol = str(self.vc.getVolume()) + '\n'
                except Exception as ex:
                    logging.error(ex)
                logging.info('response: ' + vol)
                clientSocket.send(vol.encode(encoding='UTF-8'))
            elif recived.startswith(self.SET_VOLUME):
                vol = int(recived.split(' ')[1])
                self.vc.setVolume(vol)
            elif recived.startswith(self.OPEN_URL):
                url = recived.split(' ')[1]
                self.bc.openURL(url)
            elif recived.startswith(self.CLOSE_URL):
                self.bc.closeURL()
            elif recived.startswith(self.SHUTDOWN):
                self.close()
                self.cc.shutdown()

        except Exception as ex:
            logging.error(ex)

    def close(self):
        self.socket.close()

if __name__ == '__main__':

    LOG_FILENAME = '/home/robert/volume-controller.log'
    FORMAT = '[%(asctime)s] p%(process)s {%(pathname)s:%(lineno)d} %(levelname)s - %(message)s'
    logging.basicConfig(filename=LOG_FILENAME, level=logging.DEBUG, format=FORMAT)

    logging.debug('--START DEBUG--')

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
