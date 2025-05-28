import math
from enum import Enum

import cv2
import numpy as np

class Color(Enum):
        RED = 0
        YELLOW = 1
        BLUE = 2
        NOTHING = -1

def identifySamples(image, hueImage, darkColor, lightColor):
    mask = cv2.inRange(hueImage, darkColor, lightColor)
    shapes, _ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    goodShapes = []
    for shape in shapes:
        x, y, width, height = cv2.boundingRect(shape)
        topLeft = (x, y)
        bottomRight = (x + width, y + height)

        color = (0, 255, 0)
        thickness = 10
        area = width * height
        ratio = width / height
        if 3 > ratio > 2 and area > 500:
            goodShapes.append(shape)
            cv2.rectangle(image, topLeft, bottomRight, color, thickness)
    return goodShapes

def findClosest(image, samples):
    if len(samples) > 0:
        biggestShape = max(samples, key=cv2.contourArea)
        rectangle=computeRectangle(biggestShape)
        color = (255, 0, 255)
        thickness = 10
        cv2.rectangle(image, rectangle[0], rectangle[1], color, thickness)

def computeRectangle(shape):
    x, y, width, height = cv2.boundingRect(shape)
    topLeft = (x, y)
    bottomRight = (x + width, y + height)
    return topLeft, bottomRight

def computeCenter(shape):
    x, y, width, height = cv2.boundingRect(shape)
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

def runPipeline(image, llrobot):
    hueImage = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)

    #set exposure to 616
    #set sensor gain to 16.5
    redSamples = identifySamples(image,hueImage, np.array([0, 225, 97]), np.array([10, 255, 221]) )
    yellowSamples = identifySamples(image,hueImage, np.array([8, 240, 111]), np.array([30, 255, 192]) )
    blueSamples = identifySamples(image,hueImage, np.array([83, 166, 42]), np.array([142, 255, 150]) )

    allSamples = redSamples + yellowSamples + blueSamples

    x=0
    y=0
    area=0
    color = Color.NOTHING

    if len(allSamples) > 0:
        chosenSample = chooseSample(allSamples)
        rectangle=computeRectangle(chosenSample)
        color = (255, 0, 255)
        thickness = 10
        cv2.rectangle(image, rectangle[0], rectangle[1], color, thickness)

        center = computeCenter(chosenSample)
        x=center[0]
        y=center[1]
        area= cv2.contourArea(chosenSample)
        color = Color.YELLOW

    # findClosest(image, redSamples)
    # findClosest(image, yellowSamples)
    # findClosest(image, blueSamples)

    #TODO: color of the sample

    largestContour = np.array([[]])
    data = [y, x, area, color.value, 0, 0, 0]
    return largestContour, image, data
