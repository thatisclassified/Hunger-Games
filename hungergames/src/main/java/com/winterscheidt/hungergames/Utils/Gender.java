package com.winterscheidt.hungergames.Utils;

public enum Gender {
    MALE("he", "him", "his", "himself"),
    FEMALE("she", "her", "hers", "herself"),
    NON_BINARY("they", "them", "theirs", "themselves"),
    CREATURE("it", "it", "its", "itself");

    private final String subject;    // he / she / they
    private final String object;     // him / her / them
    private final String possessive; // his / hers / theirs
    private final String reflexive;  // himself / herself / themselves

    Gender(String subject, String object, String possessive, String reflexive) {
        this.subject = subject;
        this.object = object;
        this.possessive = possessive;
        this.reflexive = reflexive;
    }

    public static Gender randomGender() {
        // Depending on audience... This may get changed to just Male / Female
        double rand = Math.random();
        if (rand <= 0.33) {
            return MALE;
        } else if (rand <= 0.66) {
            return FEMALE;
        } else {
            return NON_BINARY;
        }
    }

    public String capSubject() { 
        return subject.substring(0, 0).toUpperCase() + 
            subject.substring(1, subject.length() - 1); 
    }
    public String capObject() {
         return object.substring(0, 0).toUpperCase() + 
            object.substring(1, object.length() - 1);
    }
    public String capPossessive() { 
        return possessive.substring(0, 0).toUpperCase() + 
            possessive.substring(1, possessive.length() - 1); 
    }
    public String capReflexive() { 
        return reflexive.substring(0, 0).toUpperCase() + 
            reflexive.substring(1, reflexive.length() - 1);
    }

    public String subject() { return subject; }
    public String object() { return object; }
    public String possessive() { return possessive; }
    public String reflexive() { return reflexive; }
}
