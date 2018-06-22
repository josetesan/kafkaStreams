package es.ing.poc;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Serialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;

import java.util.Arrays;

@SpringBootApplication
@EnableBinding(KStreamProcessor.class)
public class KafkaStreamApplication {

    @StreamListener("input")
    @SendTo("output")
    public KStream<?, String> process(KStream<?, String> input) {
        return input
            .flatMapValues(value -> Arrays.asList(value.toLowerCase().split("\\W+")))
            .map((key, word) -> new KeyValue<>(word, word))
            .groupByKey(Serialized.with(Serdes.String(), Serdes.String()))
            .windowedBy(TimeWindows.of(5000))
            .count(Materialized.as("store-name"))
            .toStream()
            .map((w, c) -> new KeyValue<>(null, String.format("Count for %s: %d", w.key(), c)));
    }

    public static void main(String[] args) {
        SpringApplication.run(KafkaStreamApplication.class, args);
    }

}
