AntiTaintDroid (a.k.a. ScrubDroid)
----------------------------------

[AntiTaintDroid][1] (a.k.a. [ScrubDroid][2]) is a proof-of-concept Android application offering a working implementation of the [techniques presented in our paper][3] at [SECRYPT 2013][4] which can be exploited to bypass the security protections offered by [TaintDroid][5], a real-time privacy monitoring mechanism based on dynamic taint analysis.

AntiTaintDroid/ScrubDroid is a work by [NICTA][6]. When referencing this work, please use the following citation:
* Golam Sarwar, Olivier Mehani, Roksana Boreli, and Mohammed Ali Kaafar. “On the Effectiveness of Dynamic Taint Analysis for Protecting Against Private Information Leaks on Android-based Devices”. In: SECRYPT 2013, 10th International Conference on Security and Cryptography. Ed. by P. Samarati. ACM SIGSAC. Reykjávik, Iceland: SciTePress, July 2013.  url: http://www.nicta.com.au/pub?id=6865;
* A [BibTeX file][7] is also available.

###Quick Installation Guide###

If you don't want to be hassled with compiling the app yourself, you may just follow the workflow below:

```sh
  git clone git@github.com:gsbabil/AntiTaintDroid.git # checkout my source-code
  cd AntiTaintDroid # change current directory to AntiTaintDroid
  adb install bin/AntiTaintDroid.apk # install the app on your phone/emulator
  cd AntiTaintDroi/AntiTaintDroid-Server # change directory to AntiTaintDroid server
  python antitaintdroid-server.py # runs the server
```

###Compiling the code###

I have included both the Eclipse and Ant project files. You should just be able to import it in Eclipse and hit the `Run` button. Or, you can just do `ant debug install` to compile and install it on your TaintDroid phone.


###How it works###

The mechanisms to bypass TaintDroid protections are [elaborated in our paper][2]. Also, the code itself if pretty self-explanatory. Just go through `UntaintTricks.java` and you should be fine. You should note that the way this PoC app works is - first it collects some private information (e.g. IMEI, Android ID etc.) from the phone with `collectPrivateData()` and then it tries to leak it over the network. Where the data is leaked to depends on where you run the server component. AntiTaintDroid PoC comes with a simple Python server which you can find in `AntiTaintDroid-Server` directory. Just make sure that you `cd` inside the `AntiTaintDroid-Server` directory and then run the `python antitaintdroid-server.py` script. The server should start a very simple web-server on port `8000`. Now you can go back to the app and hit `menu > settings` to specify your server IP address and port number. That's it! Now you are ready to try all the AntiTaintDroid tricks. Each time you tap on a trick, some private data (depending on what you have in `collectPrivateData()`) should be stolen, leaked to your server (`antitaintdroid-server.py` will print it on the console) and of course there won't be any TaintDroid notification to alert you that your IMEI has just been stolen. Cheers!

###References###

[TaintDroid][5] is a joint collaboration between [Intel Labs][8], [Penn State][9] and [Duke University][10], and funded by the [U.S. National Science Foundation][11].
* William Enck, Peter Gilbert, Byung-Gon Chun, Landon P. Cox, Jaeyeon Jung, Patrick McDaniel and Anmol N.  Sheth, "TaintDroid: An information-flow tracking system for realtime privacy monitoring on smartphones," in OSDI 2010, 9th USENIX Symposium on Operating Systems Design and Implementation, R. Arpaci-Dusseau and B. Chen, Eds., USENIX; ACM SIGOPS.    Berkeley, CA, USA: USENIX Association, Oct. 2012. [Online]. Available: http://static.usenix.org/events/osdi10/tech/full_papers/Enck.pdf

  [1]: http://babilonline.blogspot.com.au/2012/08/antitaintdroid-escaping-taint-analysis.html
  [2]: http://www.nicta.com.au/pub?id=7091
  [3]: http://www.nicta.com.au/pub?id=6865
  [4]: http://secrypt.icete.org/?y=2013
  [5]: http://appanalysis.org/
  [6]: http://www.nicta.com.au/
  [7]: https://github.com/gsbabil/AntiTaintDroid/blob/master/2013sarwar_scrubdroid.bib
  [8]: http://www.intel.com/research/
  [9]: http://www.cse.psu.edu/
  [10]: http://www.cs.duke.edu/
  [11]: http://www.nsf.gov/
