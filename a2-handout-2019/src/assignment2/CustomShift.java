package assignment2;

import java.util.ArrayList;
import java.util.List;

public class CustomShift {
    private List<Job> jobs;
    private Shift shift;
    private Integer profit;

    public CustomShift(Shift shift) {
        jobs = new ArrayList<>();
        this.shift = shift;
        profit = null;
    }

    public CustomShift(Shift shift, List<Job> jobs) {
        this.jobs = jobs;
        this.shift = shift;
        profit = null;
    }

    public void addJob(Job j) {
        this.jobs.add(j);
    }

    public int getProfit(int[] cost) {
        if (this.profit == null) {
            profit = 0;
            for (int i = shift.start(); i <= shift.end(); i++) {
                profit -= cost[i];
            }
            for (Job j : jobs) {
                profit += j.payment();
            }
            return profit;

        } else {
            return profit;
        }
    }

}
