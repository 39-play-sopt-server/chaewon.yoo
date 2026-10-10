package org.sopt;

import org.sopt.client.PostClient;
import org.sopt.config.PostConfig;

public class Main {
    public static void main(String[] args) {
        PostClient client = PostConfig.createClient();
        client.run();
    }
}
