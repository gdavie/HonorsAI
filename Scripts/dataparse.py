import os, os.path

for root, _, files in os.walk(GASPAC):
    for f in files:
        fullpath = os.path.join(root, f)
        print fullpath
        #if os.path.getsize(fullpath) < 200 * 1024:
            #os.remove(fullpath)
