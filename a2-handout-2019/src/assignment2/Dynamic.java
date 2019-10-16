package assignment2;

import java.util.ArrayList;
import java.util.List;

public class Dynamic {

    private static int[][][] storage = null;

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
        // initialize storage
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
                int compensate = getCompensate(jobs.length, j, jobs);
                storage[jobs.length][i][j] = Math.max(getProfit(cost, jobs, i
                        , j, jobs.length) + compensate, 0) ;
            }
        }
        for (int i = jobs.length - 1; i > 0; i--) {
            // work backwards to find the possible start of the shift
            start = getStartJob(i, maxShiftLength, jobs);
            for (int j = start; j < i; j++) {
                for (int k = j; k < i; k++) {
                    // take or not take first
                    // new shift out of length, dont take it
                    // in range max(take, not)
                    storage[i][jobs.length][jobs.length] =
                            jobs[i].end() - jobs[i].start() + 1 > maxShiftLength ?
                                    storage[i+1][jobs.length][jobs.length]:
                                    Math.max(storage[i+1][i][i],
                                            storage[i+1][jobs.length][jobs.length]);

                    int nextJob = getNextJobAfterBreak(jobs, minShiftBreak, k);
                    if (jobs[k].end() < jobs[i].start()) {
                        // no overlap
                        if (jobs[i].end() - jobs[j].start() + 1 > maxShiftLength) {
                            // newShift
                            int compensate = getCompensate(i, k, jobs);
                            storage[i][j][k] = getProfit(cost, jobs, j, k, i) +
                                    storage[nextJob][jobs.length][jobs.length]
                                    + compensate;
                        } else {
                            // max(take, newShift, skip)
                            int take = storage[i+1][j][i];
                            int compensate = getCompensate(i, k, jobs);
                            int newShift = getProfit(cost, jobs, j, k, i) +
                                    storage[nextJob][jobs.length][jobs.length]
                                    + compensate;
                            int skip = storage[i+1][j][k] - jobs[i].payment();
                            storage[i][j][k] = Math.max(skip, Math.max(take,
                                    newShift));
                        }
                    } else {
                        // overlap
                        if (jobs[i].end() - jobs[j].start() + 1 > maxShiftLength) {
                            // newShift
                            int compensate = getCompensate(i, k, jobs);
                            storage[i][j][k] = getProfit(cost, jobs, j, k, i) +
                                    storage[nextJob][jobs.length][jobs.length]
                                    + compensate;
                        } else {
                            // max(skip, newShift)
                            int skip = storage[i + 1][j][k] - jobs[i].payment();
                            int compensate = getCompensate(i, k, jobs);
                            int newShift = getProfit(cost, jobs, j, k, i) +
                                    storage[nextJob][jobs.length][jobs.length]
                                    + compensate;
                            storage[i][j][k] = Math.max(skip, newShift);
                        }
                    }
                }
            }
        }

        // assign value to class variable
        Dynamic.storage = storage;
        return Math.max(storage[1][0][0], storage[1][jobs.length][jobs.length]);
    }

    /**
     * get the possible starting job in max shift length
     * @param ithJob end of the shift
     * @param maxShiftLength max shift length
     * @param jobs jobs
     * @return return possible starting job in max shift length
     */
    private static int getStartJob(int ithJob, int maxShiftLength, Job[] jobs) {
        if (ithJob == 0) {
            return 0;
        }
        int count = 0;
        for (Job j : jobs) {
            if (jobs[ithJob - 1].end() - j.start() + 1 <= maxShiftLength) {
                return count;
            }
            count++;
        }
        return jobs.length;
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

    private static int getNextJobAfterBreak(Job[] jobs, int minShiftBreak, int k) {
        for (int l = k + 1; l < jobs.length; l++) {
            if (jobs[l].start() - jobs[k].end() -1 >= minShiftBreak) {
                return l;
            }
        }
        return jobs.length;
    }

    private static int getCompensate(int i, int k, Job[] jobs) {
        int compensate = 0;
        for (int jobIndex = k + 1; jobIndex < i ; jobIndex++) {
            compensate += jobs[jobIndex].payment();
        }
        return compensate;
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
        // populate the storage
        maximumProfitDynamic(cost, minShiftBreak, maxShiftLength, jobs);
        int i = 0; int j = jobs.length; int k = jobs.length;
        List<Shift> shifts = new ArrayList<>();// chosen shifts
        List<Job> chosenJobs = new ArrayList<>();// chosen jobs
        List<Job> jobsInShift = new ArrayList<>();// currently chosen jobs
        // working from top to bottom to see what are the jobs got chosen
        while(i != jobs.length) {
            if (j == jobs.length) {
                // start of a new shift
                if (jobs[i].length() > maxShiftLength) {
                    // can't take
                   i++;
                } else {
                    // take or new shift
                    int take = storage[i+1][i][i];
                    int newShift = storage[i+1][jobs.length][jobs.length];
                    if (take > newShift) {
                        jobsInShift.add(jobs[i]);
                        chosenJobs.add(jobs[i]);
                        j = i;
                        k = i;
                        i++;
                    } else {
                        i++;
                    }
                }
            } else {
                // already picked a job in the shift
                int nextJob = getNextJobAfterBreak(jobs, minShiftBreak, k);
                if (jobs[i].compatible(jobs[k])) {
                    // compatible
                    if (jobs[i].end() - jobs[j].start() + 1 > maxShiftLength) {
                        // new shift
                        j = jobs.length;
                        k = jobs.length;
                        shifts.add(new Shift(jobsInShift.get(0).start(),
                                jobsInShift.get(jobsInShift.size()-1).end()));
                        jobsInShift.clear();
                        i++;
                    } else {
                        // take, skip or new shift. Which one is max and trace
                        int take = storage[i+1][j][i];
                        int skip = storage[i+1][j][k] - jobs[i].payment();
                        int newShift = getProfit(cost, jobs, j, k ,i) +
                                storage[nextJob][jobs.length][jobs.length] +
                                getCompensate(i, k, jobs);
                        int max = Math.max(take, Math.max(newShift, skip));
                        if (max == take) {
                            jobsInShift.add(jobs[i]);
                            chosenJobs.add(jobs[i]);
                            k = i;
                            i++;
                        } else if (max == skip) {
                            i++;
                        } else {
                            j = jobs.length;
                            k = jobs.length;
                            shifts.add(new Shift(jobsInShift.get(0).start(),
                                    jobsInShift.get(jobsInShift.size()-1).end()));
                            jobsInShift.clear();
                            i++;
                        }
                    }
                } else {
                    // not compatible
                    if (jobs[i].end() - jobs[j].start() + 1 > maxShiftLength) {
                        // new shift
                        j = jobs.length;
                        k = jobs.length;
                        shifts.add(new Shift(jobsInShift.get(0).start(),
                                jobsInShift.get(jobsInShift.size()-1).end()));
                        jobsInShift.clear();
                        i++;
                    } else {
                        // skip or new shift. Find max and trace
                        int skip = storage[i+1][j][k] - jobs[i].payment();
                        int newShift = getProfit(cost, jobs, j, k ,i) +
                                storage[nextJob][jobs.length][jobs.length] +
                                getCompensate(i, k, jobs);
                        if (skip > newShift) {
                            i++;
                        } else {
                            j = jobs.length;
                            k = jobs.length;
                            shifts.add(new Shift(jobsInShift.get(0).start(),
                                    jobsInShift.get(jobsInShift.size()-1).end()));
                            jobsInShift.clear();
                            i++;
                        }
                    }
                }
            }
        }
        // anything left would be in the last shift
        if (!jobsInShift.isEmpty()) {
            shifts.add(new Shift(jobsInShift.get(0).start(), jobsInShift.get(jobsInShift.size()-1).end()));
        }

        return new Solution(shifts, chosenJobs);
    }
}
