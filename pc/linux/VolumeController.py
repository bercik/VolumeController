import time
import subprocess
import socket
import sys

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

class Server:

    SET_VOLUME = "SET_VOL"
    GET_VOLUME = "GET_VOL"

    def __init__(self, port):
        self.port = port
        self.vc = VolumeController()

        self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.socket.bind(('', self.port))

    def accept(self):
        self.socket.listen(5)
        (clientSocket, addr) = self.socket.accept()
        try:
            recived = clientSocket.recv(1024)
            recived = recived.decode(encoding='UTF-8')

            if recived.startswith(self.GET_VOLUME):
                vol = str(self.vc.getVolume()) + '\n'
                clientSocket.send(vol.encode(encoding='UTF-8'))
            elif recived.startswith(self.SET_VOLUME):
                vol = int(recived.split(' ')[1])
                self.vc.setVolume(vol)
        except Exception as ex:
            print(ex)

    def close(self):
        self.socket.close()

if __name__ == '__main__':
    server = None

    while True:
        server = Server(5656)

        while True:
            server.accept()

        if server != None:
            server.close()