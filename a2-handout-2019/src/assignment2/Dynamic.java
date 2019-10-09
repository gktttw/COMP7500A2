package assignment2;

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

        // lowest level
        storage[jobs.length][jobs.length][jobs.length] = 0;
        int start = getStartJob(jobs.length, maxShiftLength, jobs);
        for (int i = start; i < jobs.length; i++) {
            for (int j = i; j < jobs.length; j++) {
                int profit = 0;
                // lowest level cost, last job must be compatible to first
                // and work you way to the start find compatible ones
                if (jobs[i].compatible(jobs[j]) || i == j) {
                    profit += jobs[j].payment();
                    for (int k = j - 1; k >= i; k--) {
                        if(jobs[k].compatible(jobs[k+1])) {
                            profit += jobs[k].payment();
                        }
                    }
                    for (int day = jobs[i].start(); day <= jobs[j].end(); day++) {
                        profit -= cost[day];
                    }
                    storage[jobs.length][i][j] = Math.max(0, profit);
                }
            }
        }
        for (int i = jobs.length - 1; i > 0; i--) {
            // work backwards to find the possible start of the shift
            start = getStartJob(i, maxShiftLength, jobs);
            for (int j = start; j < i; j++) {
                for (int k = j; k < i; k++) {
                    int nextJob = getNextJobAfterBreak(jobs, minShiftBreak, k);
                    if (jobs[k].end() < jobs[i].start()) {
                        // no overlap
                        if (jobs[i].end() - jobs[j].start() + 1 > maxShiftLength) {
                            // newShift
                            int compromise = 0;
                            for (int jobIndex = k + 1; jobIndex < i ; jobIndex++) {
                                compromise += jobs[jobIndex].payment();
                            }
                            storage[i][j][k] = getProfit(cost, jobs, j, k, i) +
                                    storage[nextJob][jobs.length][jobs.length]
                                    + compromise;
                        } else {
                            // max(take, newShift, skip)
                            int take = storage[i+1][j][i];
                            int compromise = 0;
                            for (int jobIndex = k + 1; jobIndex < i ; jobIndex++) {
                                compromise += jobs[jobIndex].payment();
                            }
                            int newShift = getProfit(cost, jobs, j, k, i) +
                                    storage[nextJob][jobs.length][jobs.length]
                                    + compromise;
                            int skip = storage[i+1][j][k] - jobs[i].payment();
                            storage[i][j][k] = Math.max(skip, Math.max(take,
                                    newShift));
                        }
                    } else {
                        // overlap
                        if (jobs[i].end() - jobs[j].start() + 1 > maxShiftLength) {
                            // newShift
                            int compromise = 0;
                            for (int jobIndex = k + 1; jobIndex < i ; jobIndex++) {
                                compromise += jobs[jobIndex].payment();
                            }
                            storage[i][j][k] = getProfit(cost, jobs, j, k, i) +
                                    storage[nextJob][jobs.length][jobs.length]
                                    + compromise;
                        } else {
                            // max(skip, newShift)
                            int skip = storage[i+1][j][k] - jobs[i].payment();
                            int compromise = 0;
                            for (int jobIndex = k + 1; jobIndex < i ; jobIndex++) {
                                compromise += jobs[jobIndex].payment();
                            }
                            int newShift = getProfit(cost, jobs, j, k, i) +
                                    storage[nextJob][jobs.length][jobs.length]
                                    + compromise;
                            storage[i][j][k] = Math.max(skip, newShift);
                        }
                    }
                }
            }
            storage[i][jobs.length][jobs.length] =
                    Math.max(storage[i+1][i][i],
                            storage[i+1][jobs.length][jobs.length]);
        }
//        for (int[][] plane : storage) {
//            for (int[] line : plane) {
//                for(int i : line) {
//                    System.out.print(i + ", ");
//                }
//                System.out.println("\n");
//            }
//            System.out.println("=====================");
//        }
        return Math.max(storage[1][0][0], storage[1][jobs.length][jobs.length]);
    }

    private static int getStartJob(int ithJob, int maxShiftLength, Job[] jobs) {
        if (ithJob == 0) {
            return 0;
        }
        int count = 0;
        for (Job j : jobs) {
            if (jobs[ithJob - 1].end() - j.start() + 1 < maxShiftLength) {
                return count;
            }
            count++;
        }
        return -1;
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

//        // new implementation
//        List<CustomShift> possibleShift = new ArrayList<>();
//        for (int end = 0; end < jobs.length; end++) {
//            for (int start = 0; start <= end; start++) {
//                //System.out.println(start + " : " + end);
//                if (jobs[end].end() - jobs[start].start() + 1 < maxShiftLength) {
//                    Shift shift = new Shift(jobs[start].start(), jobs[end].end());
//                    ArrayList<Job> selectedJobs = new ArrayList<>();
//                    for (int i = start; i <= end; i++) {
//                        if (selectedJobs.size() == 0 ||
//                                selectedJobs.get(selectedJobs.size() - 1).
//                                        compatible(jobs[i])) {
//                            selectedJobs.add(jobs[i]);
//                        }
//                    }
//                    possibleShift.add(new CustomShift(shift, selectedJobs));
//                }
//            }
//        }
//        System.out.println(possibleShift);
//
//        return 0;
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
