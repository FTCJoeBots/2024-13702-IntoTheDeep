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
class Color(Enum):
   BLUE = 1,
   RED = 2
   YELLOW = 3
#===========================================================
def calculateBrushColor():
    if brushColor == Color.BLUE:
        return (255,0,0)
    elif brushColor == Color.RED:
        return (0,0,255)
    elif brushColor == Color.YELLOW:
        return (0,255,255)
    else:
        return (255,255,255)
# ===========================================================
def calculateBrushThickness():
    if mouseDown:
        return cv2.FILLED
    else:
        return 2
#===========================================================
def drawTriangle(painted):
    top = (lastMousePosition.x , lastMousePosition.y - 15)
    right = (lastMousePosition.x + 20, lastMousePosition.y + 15)
    left = (lastMousePosition.x - 20, lastMousePosition.y + 15)
    color = calculateBrushColor()
    thickness = calculateBrushThickness()
    cv2.drawContours(painted, [np.array([top,right,left])], 0, color, thickness)
#===========================================================
def drawCircle(painted):
    center = (lastMousePosition.x, lastMousePosition.y)
    radius = 16
    color = calculateBrushColor()
    thickness = calculateBrushThickness()
    cv2.circle(painted, center, radius, color, thickness)
# ===========================================================
def drawRect(painted):
    size = 32
    halfSize = np.int32(size / 2)
    topLeft = (lastMousePosition.x - halfSize, lastMousePosition.y - halfSize)
    bottomRight = (lastMousePosition.x + halfSize, lastMousePosition.y + halfSize)
    color = calculateBrushColor()
    thickness = calculateBrushThickness()
    cv2.rectangle(painted, topLeft, bottomRight, color, thickness)
#===========================================================
def getMask():
    if brushColor == Color.BLUE:
        return maskBlue[ currentImage ]
    elif brushColor == Color.RED:
       return maskRed[ currentImage ]
    else:
        return maskYellow[ currentImage ]
# ===========================================================
def showImage():
    image = imageHolder[currentImage].copy()

    if lastMousePosition != None:
        if brushShape == BrushShape.RECTANGLE:
            drawRect(image)
        elif brushShape == BrushShape.CIRCLE:
            drawCircle(image)
        elif brushShape == BrushShape.TRIANGLE:
            drawTriangle(image)

    mask = getMask()
    maskBGR = cv2.cvtColor( mask, cv2.COLOR_GRAY2BGR)

    brushColor = calculateBrushColor()

    maskBGR=cv2.multiply(maskBGR, brushColor)
    painted = np.hstack( [image, maskBGR] )

    cv2.imshow( windowName, painted )
#===========================================================
def mouseCallback(event, x, y, flags, param):
    global lastMousePosition
    global mouseDown

    lastMousePosition = Coordinate(x, y)

    if event == cv2.EVENT_LBUTTONDOWN:
       mouseDown = True

    elif event == cv2.EVENT_LBUTTONUP:
        mouseDown = False

    if mouseDown:
        mask = getMask()

        if brushShape == BrushShape.RECTANGLE:
            drawRect(mask)
        elif brushShape == BrushShape.CIRCLE:
            drawCircle(mask)
        elif brushShape == BrushShape.TRIANGLE:
             drawTriangle(mask)


    print("mouse position: " +str(x)+", " +str(y))
    showImage()

#===========================================================
windowName="Joe Paint"
fileNames = glob.glob('snapshots/*.png')
imageHolder = []
maskBlue = []
maskRed = []
maskYellow = []

for fileName in fileNames:
    image = cv2.imread(fileName)
    imageHolder.append( image )

    mask= np.zeros( image.shape[:2], dtype="uint8" )

    maskBlue.append( mask.copy() )
    maskRed.append( mask.copy() )
    maskYellow.append( mask.copy() )

currentImage = 0
lastKeyPressed = None
lastMousePosition = None
brushShape = BrushShape.RECTANGLE
brushColor = Color.BLUE
mouseDown = False

showImage()
cv2.setMouseCallback(windowName, mouseCallback)

while True:
    keyPressed = cv2.waitKey(1)

    #spacebar change brush shape
    if keyPressed == 32:
        if brushShape == BrushShape.TRIANGLE:
            brushShape = BrushShape.RECTANGLE
        else:
            brushShape = BrushShape( np.int32(brushShape.value) + 1)
        showImage()

    #change brush color
    if keyPressed == ord('1'):
        brushColor = Color.BLUE
        showImage()

    elif keyPressed == ord('2'):
        brushColor = Color.RED
        showImage()

    elif keyPressed == ord('3'):
        brushColor = Color.YELLOW
        showImage()


    # Left Arrow or [
    elif keyPressed== 2 or keyPressed == ord('['):
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

