import tkinter as tk
import os
import socket

class Connection:
    SET_VOL = "SET_VOL"
    GET_VOL = "GET_VOL"

    DEFAULT_IP = "192.168.1.10"
    PORT = 5656

    def __init__(self, ip):
        self.ip = ip

    def create_conn(self):
        # create an INET
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        # now connect to the server on port 5656
        s.connect((self.ip.get(), self.PORT))

        return s

    def get_vol(self):
        s = self.create_conn()
        try:
            txt = self.GET_VOL + '\n'
            s.send(txt.encode(encoding='UTF-8'))
            recived = s.recv(1024)
            recived = recived.decode(encoding="UTF-8")
            return int(recived)
        finally:
            self.close_conn(s)

    def set_vol(self, new_vol):
        s = self.create_conn()
        try:
            txt = self.SET_VOL + ' ' + str(new_vol) + '\n'
            s.send(txt.encode(encoding='UTF-8'))
        finally:
            self.close_conn(s)

    def close_conn(self, s):
        s.close()

class Application(tk.Frame):

    CONFIG_FILE = "config"
    CONFIG_FILE_PATH = os.path.dirname(os.path.realpath(__file__)) \
            + "/" + CONFIG_FILE

    def __init__(self, master=None):
        self.muted = False
        tk.Frame.__init__(self, master)
        self.pack()
        self.createWidgets()

        self.c = Connection(self.ip)

        self.get()

        self.master.protocol("WM_DELETE_WINDOW", self.onClose)

    def readIP(self):
        if os.path.isfile(Application.CONFIG_FILE_PATH):
            try:
                with open(Application.CONFIG_FILE_PATH) as f:
                    return f.read().splitlines()[0]
            except Exception as ex:
                return ""
        else:
            return ""

    def writeIP(self):
        with open(Application.CONFIG_FILE_PATH, 'w') as f:
            f.write(self.ip.get() + "\n")

    def createWidgets(self):
        self.ip_label = tk.Label(self, text="ip address:")
        self.ip_label.pack(side="top")

        self.ip = tk.StringVar()
        ip = self.readIP()
        if ip != "":
            self.ip.set(ip)
        else:
            self.ip.set(Connection.DEFAULT_IP)

        self.ip_txt = tk.Entry(self, textvariable=self.ip)
        self.ip_txt.pack(side="top")

        self.sv = tk.Scale(self, orient="horizontal", length=200)
        self.sv.pack(side="top")

        self.getb = tk.Button(self, text="get", command=self.get)
        self.getb.pack(side="left")

        self.setb = tk.Button(self, text="set", command=self.set)
        self.setb.pack(side="right")

        self.error_text = tk.StringVar()
        self.error = tk.Label(self, fg="RED", textvariable=self.error_text)
        self.error_text.set("")
        self.error.pack(side="bottom")

        self.QUIT = tk.Button(self, text="QUIT", fg="red",
                                            command=self.onClose)
        self.QUIT.pack(side="bottom")

        self.mute = tk.Checkbutton(self, text="mute", command=self.mute)
        self.mute.pack(side="bottom")

    def get(self):
        self.error_text.set("")
        self.master.update()
        if not self.muted:
            try:
                self.sv.set(self.c.get_vol())
            except Exception as ex:
                self.error_text.set(ex)

    def set(self):
        self.error_text.set("")
        self.master.update()
        if not self.muted:
            try:
                self.c.set_vol(self.sv.get())
            except Exception as ex:
                self.error_text.set(ex)

            self.get()

    def mute(self):
        self.error_text.set("")
        self.master.update()
        self.muted = not self.muted

        try:
            if self.muted:
                self.c.set_vol(0)
            else:
                self.set()
        except Exception as ex:
            self.error_text.set(ex)

    def onClose(self):
        self.writeIP() 
        root.destroy()

root = tk.Tk()
root.wm_title("VolumeController")
app = Application(master=root)
app.mainloop()
