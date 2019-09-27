package assignment2;

public class Recursive {

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
     *         This method must be implemented using a recursive programming
     *         solution to the problem. It is expected to have a worst-case
     *         running time that is exponential in m = jobs.length. (You must
     *         NOT provide a dynamic programming solution to this question.)
     */
    public static int maximumProfitRecursive(int[] cost, int minShiftBreak,
            int maxShiftLength, Job[] jobs) {
        // IMPLEMENT THIS METHOD BY IMPLEMENTING THE PRIVATE METHOD IN THIS
        // CLASS THAT HAS THE SAME NAME
        return maximumProfitRecursive(cost, minShiftBreak, maxShiftLength, jobs,
                0, jobs.length, jobs.length);
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
     *          Additionally:
     * 
     *          (0 <= i <= jobs.length) and (0 <= j <= k <= jobs.length) and (if
     *          j != jobs.length then 0 <= j <= k < i)
     * 
     * @ensure Returns the maximum profit that can be earned by you for your
     *         company given that:
     * 
     *         (i): You can only select job opportunities from index i onwards
     *         in the list of jobs; and
     * 
     *         (ii) If j != jobs.length, then you cannot choose a job that
     *         starts earlier than day jobs[k].end() + 1; and
     * 
     *         (iii): If j != jobs.length, then you must select a shift that
     *         starts on day jobs[j].start(), and ends no earlier than end day
     *         jobs[k].end(). Since you must select a shift of this nature, you
     *         have an obligation to pay for it, and take it into consideration
     *         when you are selecting any further shifts to include.
     * 
     *         (See handout for details.)
     * 
     *         This method must be implemented using a recursive programming
     *         solution to the problem. It is expected to have a worst-case
     *         running time that is exponential. (You must NOT provide a dynamic
     *         programming solution to this question.)
     */
    private static int maximumProfitRecursive(int[] cost, int minShiftBreak,
            int maxShiftLength, Job[] jobs, int i, int j, int k) {
        return -1; // REMOVE THIS LINE AND IMPLEMENT THIS METHOD
    }

}
