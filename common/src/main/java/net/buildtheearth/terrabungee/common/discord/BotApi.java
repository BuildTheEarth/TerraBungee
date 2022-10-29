package net.buildtheearth.terrabungee.common.discord;

import com.google.common.graph.ElementOrder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import net.buildtheearth.terrabungee.common.discord.structures.Builder;
import net.buildtheearth.terrabungee.common.discord.structures.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Scanner;

/**
 * @author Xbox Bedrock
 */
public class BotApi {

    private final String url;

    private final String token;

    private final CloseableHttpClient httpclient = HttpClients.createDefault();
    private final Gson gson = new Gson();

    public BotApi(String url, String token) {
        this.url = url;
        this.token = token;
    }

    private void setHeaders(HttpRequestBase req) {
        req.setHeader("Content-Type", "application/json;charset=utf-8");
        req.setHeader("Authorization", "Bearer " + token);
    }

   private <T> T getWithId(String id, String pathname, Class<T> typeTo, Type type) throws IOException {
        String totalURL = url + "api/v1/" + pathname + "/" + id;
        HttpGet get = new HttpGet(totalURL);
        setHeaders(get);
        HttpResponse res = httpclient.execute(get);
        InputStream reader = res.getEntity().getContent();
        Scanner sc = new Scanner(reader);
        StringBuilder jsonBuilder = new StringBuilder();
        while (sc.hasNext()) jsonBuilder.append(sc.nextLine()).append("\n");
        String json = jsonBuilder.toString();
        JsonObject jsonObj = gson.fromJson(json, JsonObject.class);
        if (jsonObj.has("error")) {
            String error = jsonObj.get("error").getAsString();
            String message = "An error has occured in HTTP method (" + totalURL + ")";
            if (jsonObj.has("message")) message = jsonObj.get("message").getAsString() + " (" + totalURL + ")";

            BotApiErrors.throwException(error, message);
            return null;
        } else {
            return gson.fromJson(json, type);
        }
    }

    public User getUser(String id) throws IOException {
        Type type = new TypeToken<User>(){}.getType();
        return getWithId(id, "role", User.class, type);
    }

    public Builder getBuilder(String id) throws IOException {
        Type type = new TypeToken<Builder>(){}.getType();
        return getWithId(id, "builder", Builder.class, type);
    }


}
