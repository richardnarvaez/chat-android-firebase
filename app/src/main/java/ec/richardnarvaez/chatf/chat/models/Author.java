package ec.richardnarvaez.chatf.chat.models;

public class Author {

    private String email;
    private String name;
    private String profile_picture;
    private Boolean sex;
    private Integer type;
    private String uid;
    private String user_name;
    private Boolean verified;

    /**
     * No args constructor for use in serialization
     *
     */
    public Author() {
    }

    /**
     *
     * @param profile_picture
     * @param uid
     * @param sex
     * @param name
     * @param verified
     * @param type
     * @param user_name
     * @param email
     */
    public Author(String email, String name, String profile_picture, Boolean sex, Integer type, String uid, String user_name, Boolean verified) {
        super();
        this.email = email;
        this.name = name;
        this.profile_picture = profile_picture;
        this.sex = sex;
        this.type = type;
        this.uid = uid;
        this.user_name = user_name;
        this.verified = verified;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void setVerified(Boolean verified) {
        this.verified = verified;
    }
}
