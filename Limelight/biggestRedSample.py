import cv2
import numpy as np


def runPipeline(image, llrobot):
    hueimage = cv2.cvtColor(image, cv2.COLOR_BGR2HSV)
    darkred = np.array([0, 225, 97])
    brightestred = np.array([10, 255, 221])
    redmask = cv2.inRange(hueimage, darkred, brightestred)
    shapes, _ = cv2.findContours(redmask, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    goodshapes = []
    for shape in shapes:
        x, y, width, height = cv2.boundingRect(shape)
        Topleft = (x, y)
        Bottomright = (x + width, y + height)

        color = (0, 255, 0)
        thickness = 10
        area = width * height
        ratio = width / height
        if ratio < 3 and ratio > 2 and area > 500:
            goodshapes.append(shape)
            cv2.rectangle(image, Topleft, Bottomright, color, thickness)

    if len(goodshapes) > 0:
        biggestshape = max(goodshapes, key=cv2.contourArea)
        x, y, width, height = cv2.boundingRect(biggestshape)
        Topleft = (x, y)
        Bottomright = (x + width, y + height)
        color = (255, 0, 255)
        thickness = 10
        cv2.rectangle(image, Topleft, Bottomright, color, thickness)

    largestContour = np.array([[]])
    data = [0, 0, 0, 0, 0, 0]
    return largestContour, image, data






