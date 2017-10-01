package tal.managerexpenses.model;

public class Setting {
    private String username;
    private String limitDaily;
    private String limitWeekly;
    private String limitMonthly;

    public Setting() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLimitDaily() {
        return limitDaily;
    }

    public void setLimitDail(String limitDaily) {
        this.limitDaily = limitDaily;
    }

    public String getLimitWeekly() {
        return limitWeekly;
    }

    public void setLimitWeekly(String limitWeekly) {
        this.limitWeekly = limitWeekly;
    }

    public String getLimitMonthly() {
        return limitMonthly;
    }

    public void setLimitMonthly(String limitMonthly) {
        this.limitMonthly = limitMonthly;
    }
}
