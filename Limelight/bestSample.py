import math
from enum import Enum
import random

import cv2
import numpy as np

class Color(Enum):
        RED = 0
        YELLOW = 1
        BLUE = 2
        NOTHING = -1

class Sample:
    color = Color.NOTHING
    shape = None

class Rectangle:
    angle = None
    center = None
    size = None
    points = None

class Point:
    x = None
    y = None

class Size:
    width = None
    height = None

def randomColor():
    blue = random.randint(0, 255)
    green = random.randint(0, 255)
    red = random.randint(0, 255)
    return(blue, green, red)

def computeRotatedRectangle(shape):
    rect = cv2.minAreaRect(shape)
    center = rect[0]
    size = rect[1]
    pointsF = cv2.boxPoints(rect)

    rectangle = Rectangle()
    rectangle.center = Point()
    rectangle.center.x = center[0]
    rectangle.center.y = center[1]
    rectangle.size = Size()
    rectangle.size.width = size[0]
    rectangle.size.height = size[1]

    rectangle.angle = rect[2]

    rectangle.points = np.int_(pointsF)

    if rectangle.size.height > rectangle.size.width:
        rectangle.angle = rectangle.angle + math.pi/2

    return rectangle



def colorToBGR(color):
    if color == Color.RED:
        return (0, 0, 255)
    if color == Color.BLUE:
        return (255, 0, 0)
    if color == Color.YELLOW:
        return (0,255, 255)
    else:
        return None


def colorToString(color):
        if color == Color.RED:
            return "Red"
        if color == Color.BLUE:
            return "Blue"
        if color == Color.YELLOW:
            return "Yellow"
        else:
            return "Unknown"

def identifySamples(image, hueImage, color, darkColor, lightColor):
    mask = cv2.inRange(hueImage, darkColor, lightColor)
    showImage(colorToString(color) + ' Mask', mask)
    # remove noise
    mask = cv2.erode(mask, None, iterations = 2)
    showImage(colorToString(color) + 'Eroded Mask', mask)
    #fill in holes
    mask = cv2.dilate(mask, None, iterations = 6)
    showImage(colorToString(color) + 'Dilated Mask', mask)

    shapes, _ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    for shape in shapes:
        cv2.drawContours(image, [shape], -1, randomColor(), 3)

    samples = []
    for shape in shapes:
        rotatedRect = computeRotatedRectangle(shape)
        # cv2.drawContours(image, [rotatedRect.points], -1, colorToBGR(color), 3 )
        #x, y, width, height = cv2.boundingRect(shape)
        # topLeft = (x, y)
        # bottomRight = (x + width, y + height)

        rectangleColor = colorToBGR(color)
        rectangleThickness = 10
        area = rotatedRect.size.width * rotatedRect.size.height
        ratio = rotatedRect.size.width / rotatedRect.size.height
        if 3 > ratio > 2 and area > 500:
            sample  = Sample()
            sample.color = color
            sample.shape = shape
            samples.append(sample)
            # cv2.rectangle(image, topLeft, bottomRight, rectangleColor, rectangleThickness)
            cv2.drawContours(image, [rotatedRect.points], -1, colorToBGR(color), 3)
    return samples

def computeArea(sample):
    return cv2.contourArea( sample.shape )

def findClosest(image, samples):
    if len(samples) > 0:
        biggestSample = max(samples, key=computeArea)
        rectangle=computeRectangle(biggestSample)
        color = (255, 0, 255)
        thickness = 10
        cv2.rectangle(image, rectangle[0], rectangle[1], color, thickness)

def computeRectangle(sample):
    x, y, width, height = cv2.boundingRect(sample.shape)
    topLeft = (x, y)
    bottomRight = (x + width, y + height)
    return topLeft, bottomRight

def computeCenter(sample):
    x, y, width, height = cv2.boundingRect(sample.shape)
    center = (x+width/2, y+height/2)
    return center

def calculateDistance(sampleA, sampleB):
    centerA = computeCenter(sampleA)
    centerB = computeCenter(sampleB)
    xDiff = centerA[0] - centerB[0]
    yDiff = centerA[1] - centerB[1]
    distance = math.sqrt(xDiff**2 + yDiff**2)
    return distance

def minDistance(sample, allSamples):
    closestDistance = float('inf')
    for otherSample in allSamples:
        distance = calculateDistance(sample, otherSample)
        if 0 < distance < closestDistance:
            closestDistance  = distance

    return closestDistance

def chooseSample(allSamples):
    bestDistance = 0
    bestSample = None
    for sample in allSamples:
        distance = minDistance(sample, allSamples)
        if distance > bestDistance:
            bestDistance = distance
            bestSample = sample

    return bestSample

def showImage(title, image):
    cv2.imshow(title, image)
    cv2.waitKey(0)

def runPipeline(image, llrobot):
    hueImage = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

    allSamples = []
    #set exposure to 616
    #set sensor gain to 16.5
    if llrobot[0] == 1:
        redSamples = identifySamples(image,hueImage, Color.RED,  np.array([0, 225, 97]), np.array([10, 255, 221]) )
        allSamples = allSamples + redSamples

    if llrobot[1] == 1:
        blueSamples = identifySamples(image,hueImage, Color.BLUE, np.array([83, 166, 42]), np.array([142, 255, 150]) )
        allSamples = allSamples + blueSamples

    if llrobot[2] == 1:
        yellowSamples = identifySamples(image,hueImage, Color.YELLOW, np.array([8, 240, 111]), np.array([30, 255, 192]) )
        allSamples = allSamples + yellowSamples

    x=0
    y=0
    area=0
    color = Color.NOTHING

    if len(allSamples) > 0:
        chosenSample = chooseSample(allSamples)
        rectangle=computeRectangle(chosenSample)
        rectangleColor = (255, 0, 255)
        rectangleThickness = 1
        cv2.rectangle(image, rectangle[0], rectangle[1], rectangleColor, rectangleThickness)

        center = computeCenter(chosenSample)
        x=center[0]
        y=center[1]
        area= cv2.contourArea(chosenSample.shape)
        color = chosenSample.color

    # findClosest(image, redSamples)
    # findClosest(image, yellowSamples)
    # findClosest(image, blueSamples)

    largestContour = np.array([[]])
    data = [y, x, area, color.value, 0, 0, 0]
    showImage('Limelight Image - best sample', image)
    return largestContour, image, data

#image = cv2.imread('yellowSample.png')
#image = cv2.imread('redSample.png')
image = cv2.imread('blueSample.png')
#image = cv2.imread('yellowBest2.png')
showImage('Limelight Image - Camera Image', image)

llrobot = [1, 1, 1]

random.seed(0)
runPipeline(image, llrobot)