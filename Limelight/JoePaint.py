import math
from enum import Enum
import random

import cv2
import numpy as np

def showImage(title, image):
    cv2.imshow(title, image)
    cv2.waitKey(0)

#image = cv2.imread('yellowSample.png')
#image = cv2.imread('redSample.png')
image = cv2.imread('snapshots/blueSample.png')
#image = cv2.imread('yellowBest2.png')
showImage('Joe Paint!', image)

