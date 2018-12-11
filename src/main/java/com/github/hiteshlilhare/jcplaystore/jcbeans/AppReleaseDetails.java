package com.github.hiteshlilhare.jcplaystore.jcbeans;

import com.github.hiteshlilhare.jcplaystore.exception.FieldNotPresentException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;

/**
 *
 * @author Hitesh
 */
public class AppReleaseDetails {

    public enum TableField {
        AppName, Version, DeveloperId, ReleaseDate, CloneURL, SourceCloneURL, 
        Rating, TestedOn, Status, Remarks
    }

    public enum Status {
        PreVerified, Verified, Unknown, Invalid, Rejected;

        public static Status getStatus(String status) {
            if (PreVerified.toString().equalsIgnoreCase(status)) {
                return PreVerified;
            } else if (Verified.toString().equalsIgnoreCase(status)) {
                return Verified;
            } else if (Invalid.toString().equalsIgnoreCase(status)) {
                return Invalid;
            } else if (Rejected.toString().equalsIgnoreCase(status)) {
                return Rejected;
            } else {
                return Unknown;
            }
        }
    }

    //Corresponds to RepoName in repo_details table.
    private String appName;
    //Corresponds to TagName in github_releases table.
    private String version;
    //Corresponds to RepoUserId in repo_details table.
    private String developerId;
    //Corresponds to PublishedAt in github_releases table. 
    private String releaseDate;
    //clone url of the App Store Repository.
    private String cloneURL;
    //clone url for source repository in github.
    private String sourceCloneURL;
    //Corresponds to PublishedAt in github_releases table. 
    private float rating;
    //List of cards on which App is tested. 
    private final ArrayList<String> testedOn = new ArrayList<>();
    //Corresponds to Status in github_releases table and used for updating status of 
    //release in github_releases table.
    private Status status = Status.Unknown;
    //Corresponds to remearks in github_releases table and used for updating remarks for
    //releases in github_releases table.
    private String remarks;

    @Override
    public String toString() {
        return "App Name: " + appName + System.lineSeparator() + " App Version: " + version
                + System.lineSeparator() + "Developer ID: " + developerId + System.lineSeparator()
                + "Relesae Date: " + releaseDate + System.lineSeparator() + "Clone URL: " + cloneURL
                + System.lineSeparator() + "Source Clone URL: " + sourceCloneURL + System.lineSeparator()
                + "Tested On: " + testedOn + System.lineSeparator() + "Rating: " + rating + System.lineSeparator()
                + "Status: " + status + System.lineSeparator() + "Remarks: " + remarks;
    }

    /**
     * Creates AppReleaseDetails bean object from json.
     *
     * @param json
     * @return
     * @throws FieldNotPresentException
     */
    public static AppReleaseDetails createReleasedAppBean(String json)
            throws FieldNotPresentException {
        AppReleaseDetails releasedApp = new AppReleaseDetails();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        //Set release app name.
        JsonElement jsonElement = jsonObject.get("release-app-name");
        if (jsonElement == null || jsonElement.isJsonNull()) {
            throw new FieldNotPresentException("release-app-name field is not present in releases json");
        } else {
            releasedApp.setAppName(jsonElement.isJsonNull() ? null : jsonElement.getAsString());
        }
        //Set release app version.
        jsonElement = jsonObject.get("release-app-version");
        if (jsonElement == null || jsonElement.isJsonNull()) {
            throw new FieldNotPresentException("release-app-version field is not present in releases json");
        } else {
            releasedApp.setVersion(jsonElement.isJsonNull() ? null : jsonElement.getAsString());
        }
        //Set developer id.
        jsonElement = jsonObject.get("developer-id");
        if (jsonElement == null || jsonElement.isJsonNull()) {
            throw new FieldNotPresentException("developer-id field is not present in releases json");
        } else {
            releasedApp.setDeveloperId(jsonElement.isJsonNull() ? null : jsonElement.getAsString());
        }
        //Set Clone URL
        jsonElement = jsonObject.get("release-app-cloneurl");
        if (jsonElement == null || jsonElement.isJsonNull()) {
            throw new FieldNotPresentException("release-app-cloneurl field is not present in releases json");
        } else {
            releasedApp.setCloneURL(jsonElement.isJsonNull() ? null : jsonElement.getAsString());
        }
        //Set release date
        jsonElement = jsonObject.get("release-date");
        if (jsonElement == null || jsonElement.isJsonNull()) {
            throw new FieldNotPresentException("release-date field is not present in releases json");
        } else {
            releasedApp.setReleaseDate(jsonElement.isJsonNull() ? null : jsonElement.getAsString());
        }
        //Set release status
        jsonElement = jsonObject.get("release-status");
        if (jsonElement == null || jsonElement.isJsonNull()) {
            throw new FieldNotPresentException("release-status field is not present in releases json");
        } else {
            releasedApp.setStatus(jsonElement.isJsonNull()
                    ? Status.Unknown.toString()
                    : jsonElement.getAsString());
        }
        //Will display github_releases table remarks.
        jsonElement = jsonObject.get("release-remarks");
        if (jsonElement == null || jsonElement.isJsonNull()) {
            throw new FieldNotPresentException("release-remarks field is not present in releases json");
        } else {
            releasedApp.setRemarks(jsonElement.isJsonNull() ? null : jsonElement.getAsString());
        }
        //Will be same as github_releases table rating
        jsonElement = jsonObject.get("release-app-rating");
        if (jsonElement == null || jsonElement.isJsonNull()) {
            throw new FieldNotPresentException("release-app-rating field is not present in releases json");
        } else {
            releasedApp.setRating(jsonElement.isJsonNull() ? 0.0f : jsonElement.getAsFloat());
        }
        //Set release tested on 
        jsonElement = jsonObject.get("release-tested-on");
        if (jsonElement == null || jsonElement.isJsonNull()) {
            throw new FieldNotPresentException("release-tested-on field is not present in releases json");
        } else {
            if (!jsonElement.isJsonNull()) {
                Gson googleJson = new Gson();
                ArrayList<String> javaArrayListFromGSON
                        = googleJson.fromJson(jsonElement.getAsJsonArray(),
                                ArrayList.class);
                for (String cardName : javaArrayListFromGSON) {
                    releasedApp.addJavaCardTestedOn(cardName);
                }
            }
        }
        return releasedApp;
    }

    public String getID() {
        return sourceCloneURL + "/" + version;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDeveloperId() {
        return developerId;
    }

    public void setDeveloperId(String developerId) {
        this.developerId = developerId;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getCloneURL() {
        return cloneURL;
    }

    public void setCloneURL(String cloneURL) {
        this.cloneURL = cloneURL;
    }

    public String getSourceCloneURL() {
        return sourceCloneURL;
    }

    public void setSourceCloneURL(String sourceCloneURL) {
        this.sourceCloneURL = sourceCloneURL;
    }

    /**
     * Returns rating.
     *
     * @return
     */
    public float getRating() {
        return rating;
    }

    /**
     * Sets rating.
     *
     * @param rating
     */
    public void setRating(float rating) {
        this.rating = rating;
    }

    /**
     * Returns list of java cards on which app is tested.
     *
     * @return
     */
    public ArrayList<String> getTestedOn() {
        ArrayList<String> list = new ArrayList<>();
        for (String cardName : testedOn) {
            list.add(cardName);
        }
        return list;
    }

    public void setTestedOn(String jsonArray) {
        testedOn.clear();
        Gson googleJson = new Gson();
        ArrayList<String> javaArrayListFromGSON = googleJson.fromJson(jsonArray, ArrayList.class);
        for (String javacardName : javaArrayListFromGSON) {
            testedOn.add(javacardName);
        }
    }

    /**
     * Add Java card name on which app is tested.
     *
     * @param cardName
     */
    public void addJavaCardTestedOn(String cardName) {
        testedOn.add(cardName);
    }

    /**
     * Returns status.
     *
     * @return
     */
    public String getStatus() {
        return status.toString();
    }

    /**
     * Sets status.
     *
     * @param status
     */
    public void setStatus(String status) {
        this.status = Status.getStatus(status);
    }

    /**
     * Returns remarks.
     *
     * @return
     */
    public String getRemarks() {
        return remarks;
    }

    /**
     * Set remarks.
     *
     * @param remarks
     */
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    /**
     * Returns JavaCrad names as JSON array.
     *
     * @return
     */
    public String getTestedOnJsonArray() {
        Gson gsonBuilder = new GsonBuilder().create();
        return gsonBuilder.toJson(testedOn);
    }
}
