import tkinter as tk
import socket

class Connection:
    SET_VOL = "SET_VOL"
    GET_VOL = "GET_VOL"

    IP = "192.168.1.11"
    PORT = 5656

    def __init__(self):
        pass

    def create_conn(self):
        # create an INET
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        # now connect to the server on port 5656
        s.connect((self.IP, self.PORT))

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
    def __init__(self, master=None):
        self.muted = False
        self.c = Connection()
        tk.Frame.__init__(self, master)
        self.pack()
        self.createWidgets()

        self.get()

    def createWidgets(self):
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
                                            command=root.destroy)
        self.QUIT.pack(side="bottom")

        self.mute = tk.Checkbutton(self, text="mute", command=self.mute)
        self.mute.pack(side="bottom")

    def get(self):
        if not self.muted:
            try:
                self.sv.set(self.c.get_vol())
            except Exception as ex:
                self.error_text.set(ex)

    def set(self):
        if not self.muted:
            try:
                self.c.set_vol(self.sv.get())
            except Exception as ex:
                self.error_text.set(ex)

            self.get()

    def mute(self):
        self.muted = not self.muted

        if self.muted:
            self.c.set_vol(0)
        else:
            self.set()

root = tk.Tk()
root.wm_title("VolumeController")
app = Application(master=root)
app.mainloop()
