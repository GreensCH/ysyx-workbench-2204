import filecmp
from PIL import Image
from sys import argv
import numpy
import os

#只测试过24位彩色图片,其他位数需修改对应参数
if __name__ == '__main__':

    # filepath = input("input file path:")
    filepath = argv[1]
    pixel_bits = 8
    print(filepath+os.linesep)
    image = Image.open(filepath)
    # image.show()
    f = open(filepath+'.hex','w+')

    arr = numpy.array(image)
    # arr = numpy.array([ [[1,2],[3,4]],[[5,6],[7,8]],[[9,10],[11,12]]], dtype=int)
    height = arr.shape[0]
    width = arr.shape[1]
    depth = arr.shape[2]
    f.write('''# When you readmemh this file your should delete #-line'''+os.linesep)
    f.write('# pic height = '+ str(height) +';')
    f.write(' pic width = '+ str(width) +';')
    f.write(' pixel depth = '+ str(depth) +os.linesep)
    f.write('# memery format : reg [%d:0] mem [%d:0]'%(depth*pixel_bits-1,width*height-1)+os.linesep)
    t_arr=arr.T
    nt_arr = t_arr.reshape(depth,height*width)
    for i in range(height*width):
        for j in range(depth):
            f.write(hex(nt_arr[j][i])[2:].zfill(2))#补0
        # print(nt_arr[j][i])
        f.write(os.linesep)
    f.close()
    print('generate successfully!')

# arr = [   [[1,2],[3,4]],
#           [[5,6],[7,8]],
#           [[9,10],[11,12]]    ]
# nt_arr = array([[ 1,  5,  9,  3,  7, 11],
#                   [ 2,  6, 10,  4,  8, 12]])
# 1 2
# 5 6
# 9 10
# 3 4
# 7 8
# 11 12
