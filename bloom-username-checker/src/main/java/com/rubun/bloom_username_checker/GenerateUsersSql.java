package com.rubun.bloom_username_checker;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GenerateUsersSql {

    static String[] names = {
            "aarav","vivaan","aditya","krishna","arjun",
            "rohan","rahul","aman","siddharth","karan",
            "priya","ananya","isha","kavya","meera",
            "pooja","neha","sneha","riya","aarti",
            "akash","nikhil","yash","ritik","deepak",
            "ankit","ayush","manav","shivam","mohit"
    };

    static String[] suffixes = {
            "gaming","dev","official","real",
            "vlogs","travels","music","tech",
            "coder","india","daily","world"
    };

    public static void main(String[] args) throws Exception {

        int TOTAL_USERS = 10000;

        Random random = new Random();
        Set<String> usernames = new HashSet<>();

        BufferedWriter writer =
                new BufferedWriter(new FileWriter("data.sql"));

        writer.write(
                "INSERT IGNORE INTO users (username, email, created_at) VALUES\n"
        );

        int count = 0;

        while (count < TOTAL_USERS) {

            String username =
                    names[random.nextInt(names.length)]
                            + "_"
                            + suffixes[random.nextInt(suffixes.length)]
                            + "_"
                            + random.nextInt(100000);

            if (!usernames.add(username)) {
                continue;
            }

            String email =
                    "user" + count + "@dummy.com";

            writer.write(
                    String.format(
                            "('%s','%s',NOW())",
                            username,
                            email
                    )
            );

            count++;

            if (count == TOTAL_USERS) {
                writer.write(";");
            } else {
                writer.write(",\n");
            }
        }

        writer.close();

        System.out.println("Generated " + TOTAL_USERS + " users.");
    }
}
