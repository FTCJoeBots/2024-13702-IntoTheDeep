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
   BLUE   = 1,
   RED    = 2,
   YELLOW = 3,
   ERASE  = 4,
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
    cursorRatio = brushSize / 32
    halfHeight  = np.int32( 15 * cursorRatio )
    halfWidth   = np.int32( 20 * cursorRatio )

    top = (lastMousePosition.x , lastMousePosition.y - halfHeight)
    right = (lastMousePosition.x + halfWidth, lastMousePosition.y + halfHeight)
    left = (lastMousePosition.x - halfWidth, lastMousePosition.y + halfHeight)
    thickness = calculateBrushThickness()
    cv2.drawContours(painted, [np.array([top,right,left])], 0, color, thickness)
#===========================================================
def drawCircle(painted, color):
    center = (lastMousePosition.x, lastMousePosition.y)
    radius = np.int32( brushSize / 2 )
    thickness = calculateBrushThickness()
    cv2.circle(painted, center, radius, color, thickness)
# ===========================================================
def drawRect(painted, color):
    halfSize = np.int32( brushSize / 2 )
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
    #make copy of captured image so that we can modify it
    image = imageHolder[currentImage].copy()

    #compute masks
    currentMask       = getMask()
    currentBlueMask   = maskBlue  [ currentImage ]
    currentRedMask    = maskRed   [ currentImage ]
    currentYellowMask = maskYellow[ currentImage ]

    #remove painted regions
    cv2.subtract( image, currentBlueMask,   image )
    cv2.subtract( image, currentRedMask,    image )
    cv2.subtract( image, currentYellowMask, image )

    #compute tinted masks
    currentColor        = calculateBrushColor( brushColor   )
    blueColor           = calculateBrushColor( Color.BLUE   )
    redColor            = calculateBrushColor( Color.RED    )
    yellowColor         = calculateBrushColor( Color.YELLOW )
    tintedMaskBGR       = cv2.multiply( currentMask,       currentColor )
    tintedBlueMaskBGR   = cv2.multiply( currentBlueMask,   blueColor    )
    tintedRedMaskBGR    = cv2.multiply( currentRedMask,    redColor     )
    tintedYellowMaskBGR = cv2.multiply( currentYellowMask, yellowColor  )

    #image = image+getMask()
    #height, width, _ = image.shape
   # composedImage = np.zeros( ( height, width, 3 ), dtype="uint8" )

    #draw painted regions on top of image
    cv2.add( image, tintedRedMaskBGR,    image )
    cv2.add( image, tintedBlueMaskBGR,   image )
    cv2.add( image, tintedYellowMaskBGR, image )

    #draw brush
    if lastMousePosition != None:
        if brushShape == BrushShape.RECTANGLE:
            drawRect( image, calculateBrushColor( brushColor ) )
        elif brushShape == BrushShape.CIRCLE:
            drawCircle( image, calculateBrushColor(brushColor) )
        elif brushShape == BrushShape.TRIANGLE:
            drawTriangle( image, calculateBrushColor(brushColor) )

    #show image side by side with mask for current color
    painted = np.hstack( [ image, tintedMaskBGR ] )

    cv2.imshow( windowName, painted )
#===========================================================
def drawBrush( mask, color ):
    if brushShape == BrushShape.RECTANGLE:
        drawRect(mask, color)
    elif brushShape == BrushShape.CIRCLE:
        drawCircle(mask, color)
    elif brushShape == BrushShape.TRIANGLE:
        drawTriangle(mask, color)

# ===========================================================
def mouseCallback(event, x, y, flags, param):
    global lastMousePosition
    global mouseDown

    lastMousePosition = Coordinate(x, y)

    if event == cv2.EVENT_LBUTTONDOWN:
       mouseDown = True

    elif event == cv2.EVENT_LBUTTONUP:
        mouseDown = False

    if mouseDown:
        # compute masks
        currentMask = getMask()
        currentBlueMask   = maskBlue[currentImage]
        currentRedMask    = maskRed[currentImage]
        currentYellowMask = maskYellow[currentImage]

        white = 255, 255, 255
        black = 0, 0, 0

        cursorColor = white if brushColor == Color.BLUE else black
        drawBrush( currentBlueMask, cursorColor )

        cursorColor = white if brushColor == Color.RED else black
        drawBrush(currentRedMask, cursorColor)

        cursorColor = white if brushColor == Color.YELLOW else black
        drawBrush(currentYellowMask, cursorColor)

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
brushSize  = 32
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

    if keyPressed == ord('='):
        brushSize = brushSize + 1
        showImage()

    if keyPressed == ord('-'):
        brushSize = brushSize - 1
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

    elif keyPressed == ord('4'):
        brushColor = Color.ERASE
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
