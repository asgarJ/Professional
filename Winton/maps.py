''' This code implements the solution to "The Top Researcher" problem.
'''
__author__  = "Asgar Javadov"
__date__    = "2 July, 2014"

from math import sqrt, cos, pi, acos
import matplotlib.pyplot as plt
import matplotlib as mpl
from matplotlib.ticker import LinearLocator, FormatStrFormatter
from matplotlib import cm
from mpl_toolkits.mplot3d import Axes3D
from scipy.stats import norm, lognorm
import numpy as np
import time

CONST_LONG = cos(pi*51./180.) * 111.325     # 1 degree Longitude distance on 51st latitude
CONST_LAT  = 111.319                        # 1 degree Latitude distance which can be considered as constant everywhere.
CONST_RADIUS = 6371.0                       # Earth radius (in km)


# ====  Point class  ====

class Point:

    def __init__(self, *args, **kwargs):
        if args :
            self.x = args[0]
            self.y = args[1]
        else :
            self.x = kwargs.values()[0][0]
            self.y = kwargs.values()[0][1]

    def __getitem__(self, index):
        return (self.x, self.y)[index]

#   Real distance on earth
    def dist(self, p):
        dist = (abs(self.x - p.x)*CONST_LONG)**2 + (abs(self.y - p.y)*CONST_LAT)**2
        cos_fi = (1 - (dist/(CONST_RADIUS**2))/2)
        return CONST_RADIUS * acos(cos_fi)

#   Distance on map.
    def mapDist(self, p):
        dist = (self.x - p.x)**2 + (self.y - p.y)**2
        return sqrt(dist)

    def midpoint(self, p):
        return Point((self.x + p.x)/2, (self.y + p.y)/2)

#   Cross product of 3 points
    def cross(self, lp1, lp2):

        ABx = lp2.x - lp1.x
        ABy = lp2.y - lp1.y
        ACx = self.x - lp1.x
        ACy = self.y - lp1.y
        
        cross = ABx*ACy - ABy*ACx
        return cross

#   Dot product
    def dot(self, lp1, lp2):

        ABx = lp2.x - lp1.x
        ABy = lp2.y - lp1.y
        BCx = self.x - lp2.x
        BCy = self.y - lp2.y
        
        dot = ABx*BCx + ABy*BCy
        return dot

#   Distance from a point to a line segment.
    def point_to_line(self, lp1, lp2):

        d = self.cross(lp1, lp2) / lp1.L2(lp2)
        
        dot1 = self.dot(lp1, lp2)
        if dot1 > 0 : return self.L2(lp2)
        
        dot2 = self.dot(lp2, lp1)
        if dot2 > 0 : return self.L2(lp1)
        
        return abs(d)

#   Minimum distance to Thames from a given point.
    def min_DistanceToThames(self):

        distances = []
        for i in xrange(len(edges)-1):
            distances.append(self.point_to_line(edges[i], edges[i+1]))

        res = reduce(lambda x, y : min(x, y), distances)
        return res

#   L2 norm of vector
    def L2(self, p):
        Dx = self.x - p.x
        Dy = self.y - p.y
        d = sqrt(Dx**2 + Dy**2)
        return d

#   PrettyPrint
    def pp(self, arg=None):
        if arg :
            return '(%f, %f) ' % (self.x, self.y)
        else :
            return '(%.3f, %.3f)' % (self.x, self.y)

class Circle(object):

    def __init__(self, p, r, real_radius=None):
        self.p = p
        self.r = r
        
    def set_Real_Radius(self, p):
        self.real_radius = self.p.dist(p)

    def disToCircle(self, p):
        dist = self.p.dist(p)
        return (dist - self.real_radius)


# ====  Plot Thames graph  ====
'''
Make sure you have the file named 'file.txt' which is available in this directory.
'''
edges = []
try:
    input_Data = open('file.txt', 'r')
except IOError:
    print 'File "file.txt" isn\'t found. It is available in the same directory in github.'

while True:
	line = input_Data.readline()
	if len(line) == 0:
		break
	
	point = Point(float(line.split()[0]), float(line.split()[1]))
	edges.append(point)

for i in xrange(len(edges)-1):
	plt.plot([edges[i][0], edges[i+1][0]], [edges[i][1], edges[i+1][1]])


# ====  Bank of England  ====

bankofEngland = Point(51.514171, -0.088438)
plt.scatter([51.514171], [-0.088438], marker ='o', color='b', s=8)
plt.annotate(
        "Bank of England", 
        xy = bankofEngland, xytext = (-20, 20),
        textcoords = 'offset points', ha = 'right', va = 'bottom',
        bbox = dict(boxstyle = 'round,pad=0.5', fc = 'yellow', alpha = 0.5),
        arrowprops = dict(arrowstyle = '->', connectionstyle = 'arc3,rad=0'))



# ====  Satellite Circle Path  ====

x1= Point(51.451000,-0.300000)
x2= Point(51.560000, 0.000000)
center = x1.midpoint(x2)
r = center.mapDist(x1)
satellite_Circle = Circle(center, r)
satellite_Circle.set_Real_Radius(x1)

plt.scatter([center[0]], [center[1]], marker='o', color='g', s=8)
plt.annotate(
        "Satellite Circle Center", 
        xy = center, xytext = (-20, 20),
        textcoords = 'offset points', ha = 'right', va = 'bottom',
        bbox = dict(boxstyle = 'round,pad=0.5', fc = 'yellow', alpha = 0.5),
        arrowprops = dict(arrowstyle = '->', connectionstyle = 'arc3,rad=0'))


# ====  Distributions  ====

mu = (2.*np.log(4.744) + np.log(3.777))/3.  # scale = mu
sigmaSqr = mu - np.log(3.777)
shape = np.sqrt(sigmaSqr)
scale = np.exp(mu)
mode = np.exp(mu-sigmaSqr)

nd_Sat = norm(0, 1.61224)
nd_Thames = norm(0, 1.39286)
ld = lognorm(shape, loc=0, scale=scale)


def compute_point_result(p):
    
    def realDistance(dis_Thames):
        cos_fi = (1 - ((dis_Thames/CONST_RADIUS)**2)/2)
        return CONST_RADIUS * acos(cos_fi)

    short_way_to_thames = p.min_DistanceToThames()
    d_To_Thames = realDistance(short_way_to_thames)
    prob_Thames = nd_Thames.pdf(d_To_Thames)

    d_To_SatellitePath = satellite_Circle.disToCircle(p)
    prob_Satellite = nd_Sat.pdf(d_To_SatellitePath)

    d_To_BankofEngland = p.dist(bankofEngland)
    prob_BankofEngland = ld.pdf(d_To_BankofEngland)

    final_Prob = prob_Thames * prob_BankofEngland * prob_Satellite

    return final_Prob



# ====  SAMPLING and TESTING  ====

SAMPLE_SIZE = 20000
SAMPLE_SIZE_3D = 200
prob_scale = 900.
epochs = 5

def Plot_2D():
    """
    This method plots the given information and the best address where the researcher is most likely to be.
    """
    start_time = time.clock()
    print '===  2D Plot  ===\nGenerating and processing data...'

    final = []
    for i in xrange(epochs):

        data_X = np.random.normal(51.52, .015, SAMPLE_SIZE)
        data_Y = np.random.normal(-.011, .001, SAMPLE_SIZE)
        dataset = []

        for item in zip(data_X, data_Y):
            p = Point(param=item)
            dataset.append(p)

        res = []
        for p in dataset:

            final_Prob = compute_point_result(p)
            res.append((p, final_Prob))

        current_best = reduce(lambda x, y : x if x[1] > y[1] else y, res)
        final.append(current_best)
    
    end_time = time.clock()
    print 'Visualising data...'
    
    best_point = reduce(lambda x, y : x if x[1] > y[1] else y, final)
    
    f = file('Results.txt', 'a')
    f.write('Result is '+str(best_point[0].pp(1))+str(best_point[1])+'\n')
    print 'Most likely coordinate of the researcher is', best_point[0].pp()
    plt.scatter([best_point[0].x], [best_point[0].y], c='r', marker='o', s=8)
    plt.annotate(
            "Here she is!\n%s" % best_point[0].pp(), 
            xy = best_point[0], xytext = (-20, 20),
            textcoords = 'offset points', ha = 'center', va = 'bottom',
            bbox = dict(boxstyle = 'round,pad=0.5', fc = 'yellow', alpha = 0.5),
            arrowprops = dict(arrowstyle = '->', connectionstyle = 'arc3,rad=0'))

    plt.axis([51.43, 51.57, -.26, .02])
    plt.suptitle('This is the plot of given information and the point where the researcher most likely to be.')
    plt.savefig('2D_Map.png')
    print 'The result is obtained in %.1f secs.' % (end_time - start_time)
    plt.show()



def Plot_3D():
    """
    This method generates a 3D surface of probabilities (of addresses) scaled up by 900.
    """
    start_time = time.clock()
    
    print '\n===  3D Plot  ===\nGenerating and processing data...'
    data_X = np.linspace(51.2, 51.8, SAMPLE_SIZE_3D)
    data_Y = np.linspace(-.35, .05, SAMPLE_SIZE_3D)
    
    X, Y = np.meshgrid(data_X, data_Y, sparse=False)
    
    res = []
    for i in xrange(SAMPLE_SIZE_3D):
        for j in xrange(SAMPLE_SIZE_3D):
            
            p = Point(X[i,j], Y[i, j])
            res.append(compute_point_result(p))
    
    data = np.reshape(np.array(res), (-1, SAMPLE_SIZE_3D))

    print 'Data processed.\nVisualising...'

    fig = plt.figure()
    ax = fig.gca(projection='3d')
    surf = ax.plot_surface( X, Y, data*900., rstride=1, cstride=1, cmap=cm.coolwarm, linewidth=0, antialiased=False)
    ax.set_zlim(0., 1.)
    ax.zaxis.set_major_locator(LinearLocator(10))
    ax.zaxis.set_major_formatter(FormatStrFormatter('%.03f'))
    fig.colorbar(surf, shrink=0.5, aspect=5)
    title = 'This is the probability plot for the location of the researcher.\nThe probabilities are scaled up by a factor of %dx' % int(prob_scale)

    fig.suptitle(title, fontsize=14)
    
    end_time = time.clock()
    print '3D plot is obtained in %.1f secs' % (end_time - start_time)

    plt.axis([51.29, 51.8, -.32, .02])
    plt.savefig('3D_Plot.jpg')
    plt.show()



if __name__ == '__main__':
    Plot_2D()
    Plot_3D()
