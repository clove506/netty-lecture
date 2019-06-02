package com.lilac.sixth;

import com.google.protobuf.InvalidProtocolBufferException;
import com.googlecode.protobuf.format.JsonFormat;
import com.lilac.object.Galaxy;

/**
 * Created by lilac on 2019-06-02.
 */
public class Protobuf {

    public static void main(String[] args) throws InvalidProtocolBufferException {
//        Galaxy.ServerTest serverTest = Galaxy.ServerTest.newBuilder()
//                .setName("ddd")
//                .setError("error").build();
//
//        System.out.println(JsonFormat.printToString(serverTest));

        Galaxy.Student student = Galaxy.Student.newBuilder()
                .setName("丁香")
                .setAddress("北京")
                .setAge(23).build();

        byte[] bytes = student.toByteArray();

        Galaxy.Student student1 = Galaxy.Student.parseFrom(bytes);

        System.out.println(student1.getName());
        System.out.println(student1.getAddress());
        System.out.println(student1.getAge());

    }
}
