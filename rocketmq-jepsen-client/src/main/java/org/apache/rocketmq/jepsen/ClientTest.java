/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.rocketmq.jepsen;

public class ClientTest {
    public static JepsenClient jepsenClient = new JepsenClient();

    public static void main(String[] args) {

        jepsenClient.startup();

        //send and receive
        for (int i = 0; i < 50; i++) {
            System.out.println("dequeue : "+i+": content = " + jepsenClient.dequeue());
            int sendCode = jepsenClient.enqueue(Integer.toString(i));
            System.out.println("enqueue : "+ i + ": sendCode = " + sendCode);

        }

        System.out.println("drain start. ");

        String message = jepsenClient.dequeue();
        //drain
        while (message!=null){
            System.out.println("drain, content = " + message);
            message = jepsenClient.dequeue();
        }

        jepsenClient.shutdown();
    }
}
