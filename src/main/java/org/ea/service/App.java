package org.ea.service;

import java.lang.Exception;
import java.io.IOException;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;
import java.util.regex.*;

import com.rabbitmq.client.*;
import com.rabbitmq.client.Consumer;

public class App
{
    private final static String INPUT_QUEUE_NAME = "myinput";
    private final static String OUTPUT_QUEUE_NAME = "myoutput";

    public static Channel connect() throws Exception {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost("172.17.0.2");
      factory.setUsername("guest");
      factory.setPassword("guest");
      Connection connection = factory.newConnection();
      return connection.createChannel();
    }

    public static void main( String[] args ) {
      final Channel channel;
      try {
        channel = connect();

        channel.queueDeclare(INPUT_QUEUE_NAME, false, false, false, null);
        channel.queueDeclare(OUTPUT_QUEUE_NAME, false, false, false, null);

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
          @Override
          public void handleDelivery(
              String consumerTag,
              Envelope envelope,
              AMQP.BasicProperties properties,
              byte[] body
              ) throws IOException {
            String message = new String(body, "UTF-8");
            System.out.println(" [x] Received '" + message + "'");
            String result = "Fail in calculation";
            try {
              result = calculate(message);
            } catch(Exception e) {
              e.printStackTrace();
            }

            channel.basicPublish("", OUTPUT_QUEUE_NAME, null, result.getBytes());
            System.out.println(" [x] Sent '" + result + "'");
            channel.basicAck(envelope.getDeliveryTag(), false);
          }
        };
        channel.basicConsume(INPUT_QUEUE_NAME, false, consumer);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public static String calculateExp(String expression, String operator, BinaryOperator<Integer> op) throws Exception {
      if(expression.contains(operator)) {
        String[] numbers = expression.split(Pattern.quote(operator));
        Stream<String> numberStream = Arrays.stream(numbers);
        Integer result = numberStream
          .map(s -> Integer.parseInt(s.trim()))
          .skip(1)
          .reduce(Integer.parseInt(numbers[0].trim()), op);
        return Integer.toString(result);
      }
      return null;
    }

    public static String calculate(String expression) throws Exception {
      String res = null;
      res = calculateExp(expression, "+", (a, b) -> a + b);
      if(res != null) return res;
      res = calculateExp(expression, "-", (a, b) -> a - b);
      if(res != null) return res;
      res = calculateExp(expression, "*", (a, b) -> a * b);
      if(res != null) return res;
      res = calculateExp(expression, "/", (a, b) -> a / b);
      return res;
    }
}
