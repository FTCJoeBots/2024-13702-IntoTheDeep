from tkinter.font import names

import cv2
import glob
#===========================================================
def showImage():
    cv2.imshow("Joe Paint!!", imageHolder[currentImage])

#===========================================================
fileNames = glob.glob('snapshots/*.png')
imageHolder = []
for fileName in fileNames:
    imageHolder.append( cv2.imread(fileName) )

currentImage = 0
showImage()

lastKeyPressed = None
while True:
    keyPressed = cv2.waitKey(1) & 0xFF

    if keyPressed== 2:
        currentImage -= 1
        if currentImage < 0:
            currentImage = len(imageHolder)-1
        showImage()

    if keyPressed == 3:
        currentImage += 1
        if currentImage == len(imageHolder):
            currentImage = 0
        showImage()

    #Escape or Q exits Joe Paint!!!
    if keyPressed == 27 or keyPressed == ord('q'):
        break

    if keyPressed != lastKeyPressed:
        lastKeyPressed = keyPressed
        if keyPressed!= 255:
            print(keyPressed)


#image = cv2.imread('yellowSample.png')
#image = cv2.imread('redSample.png')
##image = cv2.imread('snapshots/blueSample.png')
#image = cv2.imread('yellowBest2.png')
##showImage('Joe Paint!', image)

