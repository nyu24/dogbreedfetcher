package dogapi;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

/**
 * BreedFetcher implementation that relies on the dog.ceo API.
 * Note that all failures get reported as BreedNotFoundException
 * exceptions to align with the requirements of the BreedFetcher interface.
 */
public class DogApiBreedFetcher implements BreedFetcher {
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetch the list of sub breeds for the given breed from the dog.ceo API.
     * @param breed the breed to fetch sub breeds for
     * @return list of sub breeds for the given breed
     */
    @Override
    public List<String> getSubBreeds(String breed) throws BreedNotFoundException{
        //obtaining request from url
        final Request request = new Request.Builder()
                .url("https://dog.ceo/api/breed/" + breed + "/list")
                .build();
        final JSONObject responseBody;

        //try catch the subbreeds list
        try (Response response = client.newCall(request).execute()) {
            assert response.body() != null;
            responseBody = new JSONObject(response.body().string());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (responseBody.get("status").toString().equals("error")) {
            throw new BreedNotFoundException(breed);
        } else {
            //translating JSONArray to ArrayList
            ArrayList<String> subBreeds = new ArrayList<>();
            int value = responseBody.getJSONArray("message").length();
            for (int i = 0; i < value; i++) {
                subBreeds.add(responseBody.getJSONArray("message").getString(i));
            }
            return subBreeds;
        }
    }
}