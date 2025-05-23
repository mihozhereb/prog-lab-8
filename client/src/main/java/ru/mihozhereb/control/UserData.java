package ru.mihozhereb.control;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;

public class UserData {
    private static String userLogin;
    private static String userPassword;

    public static String getUserLogin() {
        return userLogin;
    }

    public static void setUserLogin(String userLogin) {
        UserData.userLogin = userLogin;
    }

    public static String getUserPassword() {
        return DigestUtils.sha3_224Hex(userPassword);
    }

    public static void setUserPassword(String userPassword) {
        UserData.userPassword = userPassword;
    }

    public static boolean register(String login, String password, UDPClient client) throws IOException {
        Response resp = client.sendRequest(new Request(
                "register", "", null, login, DigestUtils.sha3_224Hex(password)
        ));
        return resp.response().equals("Done.");
    }

    public static boolean checkUser(String login, String password, UDPClient client) throws IOException {
        Response resp = client.sendRequest(new Request(
                "check_user", "", null, login, DigestUtils.sha3_224Hex(password)
        ));
        return resp.response().equals("Done.");
    }

    public static int getUserId(UDPClient client) throws IOException {
        Response resp = client.sendRequest(new Request(
                "get_user_id", "", null, getUserLogin(), getUserPassword()
        ));
        return Integer.parseInt(resp.response());
    }
}
