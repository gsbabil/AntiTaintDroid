AntiTaintDroid (a.k.a. ScrubDroid)
----------------------------------

[AntiTaintDroid][3] (a.k.a. [ScrubDroid][1]) is a proof-of-concept Android application offering a working implementation of the techniques presented in our [SECRYPT 2013][1] which can be exploited to bypass the security protections offered by [TaintDroid][2].


Compiling the code
------------------

I have included both the Eclipse and Ant project files. You should just be able to import it in Eclipse and hit the `Run` button. Or, you can just do `ant debug install` to compile and install it on your TaintDroid phone.


How it works
------------

The mechanisms to bypass TaintDroid protections are elaborated in our [paper][1]. Also, the code itself if pretty self-explanatory. Just go through `UntaintTricks.java` and you should be fine. You should note that the way this PoC app works is - first it collects some private information from the phone with `collectPrivateData()` and then it tries to leak it over the network. Where the data is leaked to depends on where you run the server component. AntiTaintDroid PoC comes with a simple Python server which you can find in `AntiTaintDroid-Server` directory. Just make sure that you `cd` inside the `AntiTaintDroid-Server` directory and then run the `python antitaintdroid-server.py` script. The server should start a very simple web-server on port `8000`. Now you can go back to the app and hit `menu > settings` to specify your server IP address and port number. That's it! Now you are ready to try all the AntiTaintDroid tricks. Each time you tap on a trick, some private data (depending on what you have in `collectPrivateData()`) should be stolen, leaked to your server (`antitaintdroid-server.py` will print it on the console) and of course there won't be any TaintDroid notification to alert you that your IMEI has just been stolen. Cheers.

If you don't want to be hassled with compiling the app yourself, you may just follow the workflow below:

```sh
  git clone git@github.com:gsbabil/AntiTaintDroid.git
  adb install AntiTaintDroid.apk # installs the app on your phone/emulator
  cd AntiTaintDroi/AntiTaintDroid-Server 
  python antitaintdroid-server.py # runs the server
```

  [1]: http://www.nicta.com.au/pub?id=6865
  [2]: http://appanalysis.org/
  [3]: http://babilonline.blogspot.com.au/2012/08/antitaintdroid-escaping-taint-analysis.html
