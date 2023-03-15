package org.example.orderbook;

// Program for range minimum query using segment tree
class SegmentTree
{
    double st[]; //array to store segment tree
    int max_value;
    boolean compare;
    // A utility function to get minimum of two numbers
    SegmentTree(double[] a, boolean compare) {
        this.compare = compare;
        if (compare) // minimum
            max_value = Integer.MAX_VALUE;
        else
            max_value = 0;
        constructST(a, a.length);
    }


    double minVal(double x, double y) {
        if (compare) return Math.min(x, y);
        return Math.max(x, y);
    }

    // A utility function to get the middle index from corner
    // indexes.
    private int getMid(int s, int e) {
        return s + (e - s) / 2;
    }
    private double RMQUtil(int ss, int se, int qs, int qe, int index)
    {
        // If segment of this node is a part of given range, then
        // return the min of the segment
        if (qs <= ss && qe >= se)
            return st[index];

        // If segment of this node is outside the given range
        if (se < qs || ss > qe)
            return max_value;

        // If a part of this segment overlaps with the given range
        int mid = getMid(ss, se);
        return minVal(RMQUtil(ss, mid, qs, qe, 2 * index + 1),
                RMQUtil(mid + 1, se, qs, qe, 2 * index + 2));
    }

    // Return minimum of elements in range from index qs (query
    // start) to qe (query end). It mainly uses RMQUtil()
    public double RMQ(int n, int qs, int qe)
    {
        // Check for erroneous input values
        if (qs < 0 || qe > n - 1 || qs > qe) {
            System.out.println("Invalid Input");
            return -1;
        }

        return RMQUtil(0, n - 1, qs, qe, 0);
    }

    // A recursive function that constructs Segment Tree for
    // array[ss..se]. si is index of current node in segment tree st
    public double constructSTUtil(double arr[], int ss, int se, int si)
    {
        // If there is one element in array, store it in current
        // node of segment tree and return
        if (ss == se) {
            st[si] = arr[ss];
            return arr[ss];
        }

        // If there are more than one elements, then recur for left and
        // right subtrees and store the minimum of two values in this node
        int mid = getMid(ss, se);
        st[si] = minVal(constructSTUtil(arr, ss, mid, si * 2 + 1),
                constructSTUtil(arr, mid + 1, se, si * 2 + 2));
        return st[si];
    }

    /* Function to construct segment tree from given array. This function
    allocates memory for segment tree and calls constructSTUtil() to
    fill the allocated memory */
    private void constructST(double arr[], int n)
    {
        // Allocate memory for segment tree

        //Height of segment tree
        int x = (int) (Math.ceil(Math.log(n) / Math.log(2)));

        //Maximum size of segment tree
        int max_size = 2 * (int) Math.pow(2, x) - 1;
        st = new double[max_size]; // allocate memory

        // Fill the allocated memory st
        constructSTUtil(arr, 0, n - 1, 0);
    }
}