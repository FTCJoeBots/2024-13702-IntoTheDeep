from tkinter.font import names
import numpy as np
from enum import Enum
import cv2
import glob
#===========================================================
class Coordinate:
    def __init__(self, x, y):
        self.x = x
        self.y = y
#===========================================================
class BrushShape(Enum):
    RECTANGLE = 1,
    CIRCLE = 2,
    TRIANGLE = 3,
#===========================================================
def drawTriangle(painted):
    top = (lastMousePosition.x , lastMousePosition.y - 15)
    right = (lastMousePosition.x + 20, lastMousePosition.y + 15)
    left = (lastMousePosition.x - 20, lastMousePosition.y + 15)
    color = (0, 0, 255)
    thickness = 2
    cv2.drawContours(painted, [np.array([top,right,left])], 0, color, thickness)
#===========================================================
def drawCircle(painted):
    center = (lastMousePosition.x, lastMousePosition.y)
    radius = 16
    color = (0, 255, 0)
    thickness = 2
    cv2.circle(painted, center, radius, color, thickness)
# ===========================================================
def drawRect(painted):
    size = 32
    halfSize = np.int32(size / 2)
    topLeft = (lastMousePosition.x - halfSize, lastMousePosition.y - halfSize)
    bottomRight = (lastMousePosition.x + halfSize, lastMousePosition.y + halfSize)
    color = (255, 255, 255)
    thickness = 2
    cv2.rectangle(painted, topLeft, bottomRight, color, thickness)
#===========================================================
def showImage():
    painted=imageHolder[currentImage].copy()
    if lastMousePosition != None:
        if brushShape == BrushShape.RECTANGLE:
            drawRect(painted)
        elif brushShape == BrushShape.CIRCLE:
            drawCircle(painted)
        elif brushShape == BrushShape.TRIANGLE:
            drawTriangle(painted)
    cv2.imshow(windowName,painted)
#===========================================================
def mouseCallback(event, x, y, flags, param):
    global lastMousePosition

    lastMousePosition = Coordinate(x, y)
    print("mouse position: " +str(x)+", " +str(y))
    showImage()

#===========================================================
windowName="Joe Paint"
fileNames = glob.glob('snapshots/*.png')
imageHolder = []
for fileName in fileNames:
    imageHolder.append( cv2.imread(fileName) )

currentImage = 0
lastKeyPressed = None
lastMousePosition = None
brushShape = BrushShape.RECTANGLE

showImage()
cv2.setMouseCallback(windowName, mouseCallback)

while True:
    keyPressed = cv2.waitKey(1)
    #spacebar change brush
    if keyPressed == 32:
        if brushShape == BrushShape.TRIANGLE:
            brushShape = BrushShape.RECTANGLE
        else:
            brushShape = BrushShape( np.int32(brushShape.value) + 1)
        showImage()

    # Left Arrow or [
    if keyPressed== 2 or keyPressed == ord('['):
        currentImage -= 1
        if currentImage < 0:
            currentImage = len(imageHolder)-1
        showImage()

    # Right Arrow or ]
    if keyPressed == 3 or keyPressed == ord(']'):
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

