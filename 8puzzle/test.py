import os
import time

# t1 = time.time()
# os.system('java-algs4 Solver puzzle36.txt')
# print ('time: %.2f secs' % (time.time() - t1))


i = 0
files = os.listdir()
for f in sorted(files):
    if os.path.splitext(f)[1] == '.txt':
        print ('\n%s' % f)

        t1 = time.time()
        os.system('java-algs4 Solver %s' % f)

        t2 = time.time()
        print ('time: %.2f secs' % (t2 - t1))

        i += 1
        #if i > 2: break

print ('\ntotal %s puzzles' % (i))