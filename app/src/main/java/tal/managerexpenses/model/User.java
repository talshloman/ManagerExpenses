package tal.managerexpenses.model;

public class User {
    private int id;
    private String username;
    private String firstName;
    private String lastName;
    private String password;
    private String phone;
    private String code;
    private String firstTime;// the first time that the user login.
    private int updateConstanItemsThisMonth;

    public User() {
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(String firstTime) {
        this.firstTime = firstTime;
    }

    public int getUpdateConstanItemsThisMonth() {
        return updateConstanItemsThisMonth;
    }

    public void setUpdateConstanItemsThisMonth(int updateConstanItemsThisMonth) {
        this.updateConstanItemsThisMonth = updateConstanItemsThisMonth;
    }
}
