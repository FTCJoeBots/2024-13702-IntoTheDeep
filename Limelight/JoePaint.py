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
def calculateBrushColor( color ):
    if color == Color.BLUE:
        return 255, 0, 0
    elif color == Color.RED:
        return 0, 0, 255
    elif color == Color.YELLOW:
        return 0, 255, 255
    else:
        return 255, 255, 255
# ===========================================================
def calculateBrushThickness():
    if mouseDown:
        return cv2.FILLED
    else:
        return 2
#===========================================================
def drawTriangle(painted, color):
    top = (lastMousePosition.x , lastMousePosition.y - 15)
    right = (lastMousePosition.x + 20, lastMousePosition.y + 15)
    left = (lastMousePosition.x - 20, lastMousePosition.y + 15)
    thickness = calculateBrushThickness()
    cv2.drawContours(painted, [np.array([top,right,left])], 0, color, thickness)
#===========================================================
def drawCircle(painted, color):
    center = (lastMousePosition.x, lastMousePosition.y)
    radius = 16
    thickness = calculateBrushThickness()
    cv2.circle(painted, center, radius, color, thickness)
# ===========================================================
def drawRect(painted, color):
    size = 32
    halfSize = np.int32(size / 2)
    topLeft = (lastMousePosition.x - halfSize, lastMousePosition.y - halfSize)
    bottomRight = (lastMousePosition.x + halfSize, lastMousePosition.y + halfSize)
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

    currentMask       = getMask()
    currentBlueMask   = maskBlue[ currentImage ]
    currentRedMask    = maskRed[ currentImage ]
    currentYellowMask = maskYellow[ currentImage ]

    cv2.subtract( image, currentBlueMask,   image )
    cv2.subtract( image, currentRedMask,    image )
    cv2.subtract( image, currentYellowMask, image )

    currentColor = calculateBrushColor( brushColor )
    redColor     = calculateBrushColor( Color.RED )
    blueColor    = calculateBrushColor( Color.BLUE )
    yellowColor  = calculateBrushColor( Color.YELLOW )

    tintedMaskBGR       = cv2.multiply( currentMask,       currentColor )
    tintedRedMaskBGR    = cv2.multiply( currentRedMask,    redColor     )
    tintedBlueMaskBGR   = cv2.multiply( currentBlueMask,   blueColor    )
    tintedYellowMaskBGR = cv2.multiply( currentYellowMask, yellowColor  )

    #image = image+getMask()
    height, width, _ = image.shape
   # composedImage = np.zeros( ( height, width, 3 ), dtype="uint8" )

    cv2.add( image, tintedRedMaskBGR, image )
    cv2.add( image, tintedBlueMaskBGR, image )
    cv2.add( image, tintedYellowMaskBGR, image )

    if lastMousePosition != None:
        if brushShape == BrushShape.RECTANGLE:
            drawRect( image, calculateBrushColor( brushColor ) )
        elif brushShape == BrushShape.CIRCLE:
            drawCircle( image, calculateBrushColor(brushColor) )
        elif brushShape == BrushShape.TRIANGLE:
            drawTriangle( image, calculateBrushColor(brushColor) )

    painted = np.hstack( [ image, tintedMaskBGR ] )

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
        currentMask = getMask()

        white = 255, 255, 255

        if brushShape == BrushShape.RECTANGLE:
            drawRect( currentMask, white )
        elif brushShape == BrushShape.CIRCLE:
            drawCircle( currentMask, white )
        elif brushShape == BrushShape.TRIANGLE:
             drawTriangle( currentMask, white )

#    print("mouse position: " +str(x)+", " +str(y))
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

    height, width, _ = image.shape
    mask = np.zeros( ( height, width, 3 ), dtype="uint8" )
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
