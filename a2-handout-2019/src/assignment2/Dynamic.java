package assignment2;

import java.util.Arrays;

public class Dynamic {

    private static int[][][] storage;

    /**
     * @require The array cost is not null, and all the integers in the array
     *          (the daily costs of the worker) are greater than or equal to
     *          zero. (The number of days that you are running the company is
     *          defined to be n = cost.length).
     * 
     *          The minimum number of days between shifts is greater than or
     *          equal to one (1 <= minShiftBreak). The maximum shift length is
     *          greater than or equal to one (1 <= maxShiftLength).
     * 
     *          The array jobs is not null, and does not contain null values.
     *          The jobs are sorted in ascending order of their end days. The
     *          end day of every job must be strictly less than the length of
     *          the cost array (n = cost.length).
     * 
     * @ensure Returns the maximum profit that can be earned by you for your
     *         company given parameters cost, minShiftBreak, maxShiftLength and
     *         jobs.
     * 
     *         (See handout for details.)
     * 
     *         This method must be implemented using an efficient bottom-up
     *         dynamic programming solution to the problem (not memoised).
     */
    public static int maximumProfitDynamic(int[] cost, int minShiftBreak,
            int maxShiftLength, Job[] jobs) {
        int[][][] storage =
                new int[jobs.length + 1][jobs.length + 1][jobs.length + 1];
        for (int[][] square : storage) {
            for (int[] line : square) {
                Arrays.fill(line, -1);
            }
        }

        storage[jobs.length][jobs.length][jobs.length] = 0;

        for (int i = 0; i < jobs.length; i++) {
            storage[jobs.length][i][i] =
                    getProfit(cost,
                    jobs, i, i, i);
        }

        Dynamic.storage = storage;

        // initialize storage when i == j

        // TODO: 899 -> 988 999 need init

        for (int probe = jobs.length - 1; probe >= 0; probe--) {
            // it all start with a new shift
            int notTake = Dynamic.storage[probe + 1][jobs.length][jobs.length];
//            calculateTake(cost, minShiftBreak, maxShiftLength, jobs, probe + 1,
//                    probe, probe);
//            int take = Dynamic.storage[probe + 1][probe][probe];
            int take = maximumProfitRecursive(cost, minShiftBreak,
                    maxShiftLength, jobs, probe + 1, probe, probe);
            Dynamic.storage[probe + 1][probe][probe] = take;
            Dynamic.storage[probe][jobs.length][jobs.length] = Math.max(take,
                    notTake);
//            maximumProfitRecursive(cost, minShiftBreak, maxShiftLength, jobs,
//                    probe, jobs.length, jobs.length);
        }



//        maximumProfitRecursive(cost, minShiftBreak, maxShiftLength, jobs, 0,
//                jobs.length, jobs.length);

        return Dynamic.storage[0][jobs.length][jobs.length];
    }

    private static void calculateTake(int[] cost, int minShiftBreak,
                                      int maxShiftLength, Job[] jobs, int i, int j,
                                      int k) {
        // see all the possible i j k and update all of them cunt
        // fill in the gap for taking i j k cuz they are not in storage
        // build a tree and do level order traversal and update the storage
        Triplet t = new Triplet(i, j, k);
        Node n = new Node(t);
        NaryTree tree = new NaryTree(n);
        buildTree(cost, minShiftBreak, maxShiftLength, jobs, i, j, k, n);
    }

    private static void buildTree(int[] cost, int minShiftBreak,
                                  int maxShiftLength, Job[] jobs, int i, int j,
                                  int k, Node n) {

    }

    private static int maximumProfitRecursive(int[] cost, int minShiftBreak,
                                              int maxShiftLength, Job[] jobs, int i, int j, int k) {

        if( Dynamic.storage[i][j][k] != -1) {
            return Dynamic.storage[i][j][k];
        }

        System.out.println("" + i + ", " + j + ", " + k);
        // Base Case
        if (i == jobs.length) {
            // only allowed to choose from i onwards
            int answer = Math.max(getProfit(cost, jobs, j, k, i), 0);
            Dynamic.storage[i][j][k] = answer;
            return answer;
        } else {
            if (jobs[i].end() - jobs[i].start() + 1 > maxShiftLength) {
                // job duration exceeds maxShiftLength
                // skip
                int answer = maximumProfitRecursive(cost,
                        minShiftBreak, maxShiftLength, jobs,
                        i + 1 , j, k) - jobs[i].payment();
                Dynamic.storage[i][j][k] = answer;
                return answer;
            }
            // take i or not based on some logic
            // first check j start and k end if it exceeds maxShiftLength
            // exceeds: cant take more job in this shift, new a shift
            // not exceed: take it or not take it
            //
            if (j == jobs.length) {
                // no shift, take the next one or not
                // max(take, don't take)
                int answer = Math.max(maximumProfitRecursive(cost, minShiftBreak,
                        maxShiftLength, jobs, i + 1, i, i),
                        maximumProfitRecursive(cost, minShiftBreak,
                                maxShiftLength, jobs, i + 1, jobs.length,
                                jobs.length));
                Dynamic.storage[i][j][k] = answer;
                return answer;
            } else {
                int nextJob = getNextJobAfterBreak(jobs, minShiftBreak, k);
                if (jobs[k].end() < jobs[i].start()) {
                    // no overlap
                    if (jobs[i].end() - jobs[j].start() + 1 > maxShiftLength) {
                        // newShift

                        int answer = getProfit(cost, jobs, j, k, i) +
                                maximumProfitRecursive(cost, minShiftBreak,
                                        maxShiftLength, jobs, nextJob,
                                        jobs.length, jobs.length);
                        Dynamic.storage[i][j][k] = answer;
                        return answer;
                    } else {
                        // max(take, newShift, skip)

                        int take = maximumProfitRecursive(cost,
                                minShiftBreak, maxShiftLength, jobs,
                                i + 1, j, i);
                        int newShift = getProfit(cost, jobs, j, k, i) +
                                maximumProfitRecursive(cost, minShiftBreak,
                                        maxShiftLength, jobs, nextJob,
                                        jobs.length, jobs.length);
                        int skip = maximumProfitRecursive(cost,
                                minShiftBreak, maxShiftLength, jobs,
                                i + 1, j, k) - jobs[i].payment();
                        int answer = Math.max(skip, Math.max(take, newShift));
                        Dynamic.storage[i][j][k] = answer;
                        return answer;
                    }
                } else {
                    // overlap
                    if (jobs[i].end() - jobs[j].start() + 1 > maxShiftLength) {
                        // newShift

                        int answer = getProfit(cost, jobs, j, k, i) +
                                maximumProfitRecursive(cost,
                                        minShiftBreak, maxShiftLength, jobs,
                                        nextJob, jobs.length, jobs.length);
                        Dynamic.storage[i][j][k] = answer;
                        return answer;
                    } else {
                        // max(skip, newShift)

                        int skip = maximumProfitRecursive(cost,
                                minShiftBreak, maxShiftLength, jobs,
                                i + 1 , j, k) - jobs[i].payment();
                        int newShift = getProfit(cost, jobs, j, k, i) +
                                maximumProfitRecursive(cost, minShiftBreak,
                                        maxShiftLength, jobs, nextJob,
                                        jobs.length, jobs.length);
                        int answer = Math.max(skip, newShift);
                        Dynamic.storage[i][j][k] = answer;
                        return answer;
                    }
                }
            }
        }
    }

    private static int getProfit(int[] cost, Job[] jobs, int j, int k, int i) {
        if (j == jobs.length) {
            return 0;
        }
        int profit = 0;
        for (int l = j; l <= k; l++) {
            profit += jobs[l].payment();
        }
        for (int l = jobs[j].start(); l <= jobs[k].end(); l++) {
            profit -= cost[l];
        }
        return profit;
    }

    private static int getNextJobAfterBreak(Job[] jobs, int minShiftBreak,
                                          int k) {
        for (int l = k + 1; l < jobs.length; l++) {
            if (jobs[l].start() - jobs[k].end() -1 >= minShiftBreak) {
                return l;
            }
        }
        return jobs.length;
    }

    /**
     * @require The array cost is not null, and all the integers in the array
     *          (the daily costs of the worker) are greater than or equal to
     *          zero. (The number of days that you are running the company is
     *          defined to be n = cost.length).
     * 
     *          The minimum number of days between shifts is greater than or
     *          equal to one (1 <= minShiftBreak). The maximum shift length is
     *          greater than or equal to one (1 <= maxShiftLength).
     * 
     *          The array jobs is not null, and does not contain null values.
     *          The jobs are sorted in ascending order of their end days. The
     *          end day of every job must be strictly less than the length of
     *          the cost array (n = cost.length).
     * 
     * @ensure Returns a valid selection of shifts and job opportunities that
     *         results in the largest possible profit to your company (given
     *         parameters cost, minShiftBreak, maxShiftLength and jobs).
     * 
     *         (See handout for details.)
     * 
     *         This method must be implemented using an efficient bottom-up
     *         dynamic programming solution to the problem (not memoised).
     */
    public static Solution optimalSolutionDynamic(int[] cost, int minShiftBreak,
            int maxShiftLength, Job[] jobs) {
        return null; // REMOVE THIS LINE AND IMPLEMENT THIS METHOD
    }
}
