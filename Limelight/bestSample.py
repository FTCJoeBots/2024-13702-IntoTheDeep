import math
from enum import Enum

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

def identifySamples(image, hueImage, color, darkColor, lightColor):
    mask = cv2.inRange(hueImage, darkColor, lightColor)
    cv2.imshow('Mask', mask)
    cv2.waitKey(0)
    shapes, _ = cv2.findContours(mask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    samples = []
    for shape in shapes:
        x, y, width, height = cv2.boundingRect(shape)
        topLeft = (x, y)
        bottomRight = (x + width, y + height)

        rectangleColor = (0, 255, 0)
        rectangleThickness = 10
        area = width * height
        ratio = width / height
        if 3 > ratio > 2 and area > 500:
            sample  = Sample()
            sample.color = color
            sample.shape = shape
            samples.append(sample)
            cv2.rectangle(image, topLeft, bottomRight, rectangleColor, rectangleThickness)
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

def runPipeline(image, llrobot):
    hueImage = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    cv2.imshow('hueImage', hueImage)
    cv2.waitKey(0)

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
        rectangleThickness = 10
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
    cv2.imshow('Limelight Image - best sample', image)
    cv2.waitKey(0)
    return largestContour, image, data

#image = cv2.imread('yellowSample.png')
#image = cv2.imread('redSample.png')
#image = cv2.imread('blueSample.png')
image = cv2.imread('yellowBest2.png')
cv2.imshow('Limelight Image - Camera Image', image)
cv2.waitKey(0)

llrobot = [1, 1, 1]
runPipeline(image, llrobot)