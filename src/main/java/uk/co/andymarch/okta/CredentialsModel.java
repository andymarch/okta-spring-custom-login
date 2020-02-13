package uk.co.andymarch.okta;

public class CredentialsModel {

    private String user;
    private char[] pword;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public char[] getPword() {
        return pword;
    }

    public void setPword(char[] pword) {
        this.pword = pword;
    }
}
