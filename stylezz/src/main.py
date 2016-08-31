#!/usr/bin/python

import cv2
import numpy as np

import glob
import time

import webcolors

def detect_objects(imageFile, thresh_area=0.002):
    print ''
    print "==================="

    image = cv2.imread(imageFile)

    print 'Processing image:', imageFile, 'of size:', image.shape

    grayscale_img = cv2.imread(imageFile, 0)
    ret, thresh = cv2.threshold(grayscale_img, 127, 255, 0)
    contours, hierarchy = cv2.findContours(thresh, 1, 2)
    print len(contours), 'contours in the image.'

    # calculate the threshold size
    height, width = grayscale_img.shape[:2]
    image_area = height * width
    object_threshold = thresh_area * image_area

    # filter out valid contours
    valid_contours = filter(lambda cont: cv2.minAreaRect(cont)[1][0] * \
                                         cv2.minAreaRect(cont)[1][1]>object_threshold, contours)
    print len(valid_contours), 'contours to consider in the image.'
    print


    objects = []
    for cnt in valid_contours:
        rect = cv2.minAreaRect(cnt)
        box = cv2.cv.BoxPoints(rect)
        box = np.int0(box)
        cv2.drawContours(image, [box], 0, (0,0,0), 1)
        objects.append(box)

    print process(image, objects, 3)
    cv2.imshow("Contours", image)

def process(image, objects, top=3):
    colors = {}
    for box in objects:
        process_object(image, box, colors)

    colors_sorted = sorted(colors.items(), cmp=lambda x, y: 1 if x[1]<y[1] else -1)
    return map(lambda e: e[0], colors_sorted)[:top]

def process_object(image, box, colors):
    points = get_points(box, image)
    colors_cur = get_colors(image, points, top=1)
    for k, v in colors_cur.items():
        if colors.__contains__(k):
            colors[k] = colors[k] + v
        else:
            colors[k] = v

def convert_color(bgr):
    try:
        b, g, r = bgr
        return webcolors.rgb_to_name((r, g, b))
    except ValueError:
        return closest_color(bgr)

def closest_color(bgr):
    b, g, r = bgr
    color_distance = {}
    for hex, name in webcolors.CSS3_HEX_TO_NAMES.items():
        rc, gc, bc = webcolors.hex_to_rgb(hex)
        rd = (r - rc) ** 2
        gd = (g - gc) ** 2
        bd = (b - bc) ** 2
        color_distance[(rd+gd+bd)] = name
    return color_distance[min(color_distance.keys())]

def pixel_color(image, p):
    return (image.item(p[0],p[1],0), image.item(p[0],p[1],1), image.item(p[0],p[1],2))

def get_colors(image, points, top=3):
    bgr_dict = dict()
    for point in points:
        color = pixel_color(image, point)
        if not bgr_dict.__contains__(color):
            bgr_dict[color] = 1
        else:
            bgr_dict[color] = bgr_dict[color] + 1
    str_colors = dict()
    for k, v in bgr_dict.items():
        color = convert_color(k)
        if str_colors.__contains__(color):
            str_colors[color] = str_colors[color] + v
        else:
            str_colors[color] = v
    return str_colors

def get_points(box, image):
    r = np.int0(min(radius1(box), radius2(box)))
    c = center(box)
    points = []
    for x in xrange(r):
        for y in xrange(r):
            ps = [c + (x, y), c - (x, y), c + (-x, y), c + (x, -y)]
            if distance(ps[0], c) <= r and ps[0][0]<image.shape[0] and ps[0][1] < image.shape[1]:
                for p in ps:
                    points.append(p)
    return points


def center(box):
    p1, p2, p3, p4 = box
    return np.int0((p1 + p2 + p3 + p4)/4)

def radius1(box):
    p1, p2, p3, p4 = box
    mid1 = (p1 + p2) / 2
    mid2 = (p3 + p4) / 2
    center = (mid1 + mid2) / 2
    return distance(center, mid1)

def radius2(box):
    p1, p4, p3, p2 = box
    mid1 = (p1 + p2) / 2
    mid2 = (p3 + p4) / 2
    center = (mid1 + mid2) / 2
    return distance(center, mid1)

def distance(p1, p2):
    return np.sqrt((p1[0] - p2[0]) ** 2 + (p1[1] - p2[1]) ** 2)


if __name__ == "__main__":
    start = time.time()
    print 'Running script...'

    images = glob.glob("images/*.jpg")
    print len(images), 'images found'

    for image in images:
        detect_objects(image)

    print 'Script ran for %.2f seconds' % (time.time() - start)
