/*******************************************************************************
 * Copyright (c) 2017 Francisco Gonzalez-Armijo Riádigos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.example.fgonzalez.data;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

public class MockResponseDispatcher {

    private static MockResponse mockResponse = new MockResponse()
            .addHeader("Content-Type", "text/xml")
            .addHeader("Pragma", "no-cache")
            .addHeader("Cache-Control", "no-store")
            .addHeader("Expires", "0")
            .setResponseCode(200);

    public static boolean RETURN_500 = false;

    public static final Dispatcher DISPATCHER = new Dispatcher() {

        @Override
        public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
            if (RETURN_500) {
                return new MockResponse().setResponseCode(500);
            } else if (request.getPath().matches("/posts/1/comments")) {
                return mockResponse.setBody(getFile("comments.json"));
            } else if (request.getPath().matches("/posts")) {
                return mockResponse.setBody(getFile("posts.json"));
            } else if (request.getPath().matches("/users/1")) {
                    return mockResponse.setBody(getFile("user.json"));
            }
            return new MockResponse().setResponseCode(404);
        }
    };

    private MockResponseDispatcher() {
        // no instance by default
    }

    private static String getFile(String fileName) {
        StringBuilder result = new StringBuilder("");
        //Get file from resources folder
        ClassLoader classLoader = MockResponseDispatcher.class.getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile().replace("%20", " "));
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line).append("\n");
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public static void reset() {
        RETURN_500 = false;
    }
}
